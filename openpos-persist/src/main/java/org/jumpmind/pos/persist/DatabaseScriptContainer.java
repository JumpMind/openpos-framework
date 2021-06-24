package org.jumpmind.pos.persist;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.h2.H2DatabasePlatform;
import org.jumpmind.db.platform.oracle.OracleDatabasePlatform;
import org.jumpmind.db.sql.ISqlTemplate;
import org.jumpmind.db.sql.SqlScript;
import org.jumpmind.exception.IoException;
import org.jumpmind.pos.persist.model.ScriptVersionModel;
import org.jumpmind.symmetric.io.data.Batch;
import org.jumpmind.symmetric.io.data.DataProcessor;
import org.jumpmind.symmetric.io.data.DbImport;
import org.jumpmind.symmetric.io.data.reader.ProtocolDataReader;
import org.jumpmind.symmetric.io.data.writer.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class DatabaseScriptContainer {
    final static String IMPORT_PREFIX = "-- import:";

    private List<DatabaseScript> preInstallScripts = new ArrayList<DatabaseScript>();
    private List<DatabaseScript> postInstallScripts = new ArrayList<DatabaseScript>();

    private JdbcTemplate jdbcTemplate;
    private IDatabasePlatform platform;
    private DBSession dbSession;
    private String installationId;
    private Map<String, String> replacementTokens;

    private List<String> scriptLocations;

    public DatabaseScriptContainer(List<String> scriptLocations, DBSession dbSession, String installationId) {
        try {
            this.installationId = installationId;
            this.dbSession = dbSession;
            this.scriptLocations = scriptLocations;
            this.platform = dbSession.getDatabasePlatform();
            this.jdbcTemplate = new JdbcTemplate(platform.getDataSource());

            replacementTokens = new HashMap<String, String>();
            // Add any replacement tokens

            for (String scriptLocation : scriptLocations) {
                Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
                        .getResources(String.format("classpath*:%s/*.*", scriptLocation));
                for (Resource r : resources) {
                    DatabaseScript script = new DatabaseScript(r);
                    if (script.getWhen() == DatabaseScript.WHEN_PREINSTALL) {
                        preInstallScripts.add(script);
                    } else if (script.getWhen() == DatabaseScript.WHEN_POSTINSTALL) {
                        postInstallScripts.add(script);
                    }
                }
            }
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    public void executePreInstallScripts(boolean failOnError) {
        executeScripts(this.preInstallScripts, failOnError);
    }

    public void executePostInstallScripts(boolean failOnError) {
        executeScripts(this.postInstallScripts, failOnError);
    }

    public void executeScripts(List<DatabaseScript> scripts, boolean failOnError) {
        if (scripts != null) {
            Collections.sort(scripts);
            for (DatabaseScript s : scripts) {
                try {
                    boolean executeScript = false;
                    ScriptVersionModel version = dbSession.findByNaturalId(ScriptVersionModel.class, new ModelId("installationId", installationId, "fileName", s.getResource().getFilename()));
                    String md5Hash = null;
                    try (InputStream is = s.getResource().getInputStream()) {
                        md5Hash = DigestUtils.md5DigestAsHex(is);
                    } catch (IOException ex) {
                        throw new IoException(ex);
                    }
                    if (version == null) {
                        log.info("Running {} for the first time", s.getResource().getFilename());
                        executeScript = true;
                    } else if (!md5Hash.equals(version.getCheckSum())) {
                        log.info("Running {} again because the content changed", s.getResource().getFilename());
                        executeScript = true;

                    }
                    if (isDatabaseMatch(s) && executeScript) {
                        try {
                            executeImports(s, s.getResource(), failOnError);
                            execute(s, s.getResource(), failOnError);
                            if (version == null) {
                                version = ScriptVersionModel.builder().
                                        installationId(installationId).
                                        fileName(s.getResource().getFilename()).
                                        checkSum(md5Hash).build();
                            } else {
                                version.setCheckSum(md5Hash);
                            }
                            dbSession.save(version);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (RuntimeException ex) {
                    if (!failOnError) {
                        log.warn("Failed to run script {}.  Ignoring because failOnError is false. {}", s.getResource().getFilename(), ex.getMessage());
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }

    void executeImports(DatabaseScript databaseScript, Resource resource, boolean failOnError) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith(IMPORT_PREFIX)) {
                    String file = line.substring(IMPORT_PREFIX.length()).trim();
                    Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
                            .getResources(String.format("classpath*:%s", file));
                    for (Resource resource2 : resources) {
                        execute(databaseScript, resource2, failOnError);
                    }
                }
                line = reader.readLine();
            }
        }
    }

    void execute(DatabaseScript databaseScript, final Resource resource, boolean failOnError) throws IOException {
        String compareString = resource.getFilename().toLowerCase();
        if (compareString.endsWith(".sql")) {
            loadSql(resource.getURL(), failOnError);
        } else if (compareString.endsWith(".csv")) {
            loadCsv(databaseScript, resource, failOnError);
        } else if (compareString.endsWith(".batch")) {
            loadFromBatchCsv(databaseScript, resource, failOnError);
        }
    }

    void loadFromBatchCsv(DatabaseScript script, Resource resource, boolean failOnError) throws IOException {
        DefaultDatabaseWriter writer = new DefaultDatabaseWriter(platform, buildDatabaseWriterSettings(failOnError));
        try (InputStream is = script.getResource().getInputStream()) {
            ProtocolDataReader reader = new ProtocolDataReader(Batch.BatchType.LOAD, "localhost", is);
            DataProcessor dataProcessor = new DataProcessor(reader, writer, "import");
            dataProcessor.process();
        }
    }

    DatabaseWriterSettings buildDatabaseWriterSettings(boolean failOnError) {
        DatabaseWriterSettings settings = new DatabaseWriterSettings();
        settings.setMaxRowsBeforeCommit(10000);
        settings.setCommitSleepInterval(0);
        settings.setDefaultConflictSetting(buildConflictSettings());
        settings.setUsePrimaryKeysFromSource(false);
        settings.setAlterTable(false);
        settings.setCreateTableDropFirst(false);
        settings.setCreateTableFailOnError(false);
        settings.setDatabaseWriterFilters(new ArrayList<IDatabaseWriterFilter>(0));
        settings.setIgnoreMissingTables(true);
        settings.setCreateTableAlterCaseToMatchDatabaseDefault(true);
        if (!failOnError) {
            settings.addErrorHandler(new DatabaseWriterErrorIgnorer());
        }
        return settings;
    }

    Conflict buildConflictSettings() {
        Conflict conflict = new Conflict();
        conflict.setDetectType(Conflict.DetectConflict.USE_OLD_DATA);
            conflict.setResolveType(Conflict.ResolveConflict.FALLBACK);
        return conflict;
    }

    void loadSql(URL script, boolean failOnError) {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            public Object doInConnection(Connection c) throws SQLException, DataAccessException {
                ISqlTemplate template = platform.getSqlTemplate();
                SqlScript sqlscript = new SqlScript(script, template, true, ";", replacementTokens);
                sqlscript.setFailOnSequenceCreate(true);
                sqlscript.setFailOnDrop(false);
                sqlscript.setFailOnError(true);
                try {
                    sqlscript.execute();
                } catch (Exception ex) {
                    String message = "Failed to execute script: " + script;
                    if (failOnError) {
                        throw new SQLException(message, ex);
                    } else {
                        log.warn(message + ".  Ignoring because failOnError is false.  " + ex.getMessage());
                    }
                }
                return null;
            }
        });
    }

    void loadCsv(DatabaseScript script, Resource resource, boolean failOnError) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            String tableName = script.getDescription().replaceAll("-", "_");
            // delete versus truncate so symds syncs deletes
            jdbcTemplate.execute(String.format("delete from %s", tableName));
            DbImport importer = new DbImport(platform);
            importer.setFormat(DbImport.Format.CSV);
            importer.setCommitRate(1000);
            importer.setForceImport(!failOnError);
            importer.setAlterCaseToMatchDatabaseDefaultCase(true);
            importer.importTables(is, tableName);
        }
    }

    boolean isDatabaseMatch(DatabaseScript script) {
        if (script.getDescription().equals("H2Only")) {
            return platform instanceof H2DatabasePlatform;
        } else if (script.getDescription().equals("OracleOnly")) {
            return platform instanceof OracleDatabasePlatform;
        } else {
            return true;
        }
    }
}
