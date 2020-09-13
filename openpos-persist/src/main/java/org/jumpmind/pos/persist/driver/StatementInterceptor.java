/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.persist.driver;

import java.util.*;

import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.sql.LogSqlBuilder;
import org.jumpmind.properties.TypedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatementInterceptor extends WrapperInterceptor {

    public static final String LONG_RUNNING_THRESHOLD_PROPERTY = "jumpmind.commerce.longRunningThreshold";

    private final static Logger log = LoggerFactory.getLogger(StatementInterceptor.class);
    protected List<Object> psArgs = new ArrayList<Object>();
    protected LogSqlBuilder sqlBuilder = new LogSqlBuilder();

    protected long longRunningThreshold = 20000;

    private InProgressSqlKey sqlKey;

    public StatementInterceptor(Object wrapped, TypedProperties systemPlusEngineProperties) {
        super(wrapped);
        String longRunningThresholString = systemPlusEngineProperties.get(LONG_RUNNING_THRESHOLD_PROPERTY);
        if (!StringUtils.isEmpty(longRunningThresholString)) {
            longRunningThreshold = Long.parseLong(longRunningThresholString.trim());
            if (log.isDebugEnabled()) {
                log.debug("Long Running SQL threshold is: " + longRunningThreshold + "ms.");
            }
        }
    }

    @Override
    public InterceptResult preExecute(String methodName, Object... parameters) {
        if (getWrapped() instanceof PreparedStatementWrapper) {
            return preparedStatementPreExecute((PreparedStatementWrapper)getWrapped(),  methodName, parameters);
        }
        return new InterceptResult();
    }

    protected InterceptResult preparedStatementPreExecute(PreparedStatementWrapper ps, String methodName, Object[] parameters) {
        if (methodName.startsWith("set") && (parameters != null && parameters.length > 1)) {
            if (methodName.equals("setNull")) {
                psArgs.add(null);
            } else {
                psArgs.add(parameters[1]);
            }
        }

        if (methodName.startsWith("execute")) {
            sqlKey = generateKey(ps);
            SqlWatchdog.sqlBegin(sqlKey,
                    new InProgressSqlStatement(ps.getStatement(), psArgs, System.currentTimeMillis(), Thread.currentThread().getName() ) );
        }

        return new InterceptResult();
    }

    private InProgressSqlKey generateKey(PreparedStatementWrapper ps) {
        InProgressSqlKey key = new InProgressSqlKey();
        key.setPsWrapper(ps);
        key.setThreadName(Thread.currentThread().getName());
        key.setSalt(new Random().nextLong());
        return key;
    }

    @Override
    public InterceptResult postExecute(String methodName, Object result, long startTime, long endTime, Object... parameters) {
        if (getWrapped() instanceof PreparedStatementWrapper) {
            return preparedStatementPostExecute((PreparedStatementWrapper)getWrapped(), methodName, result, startTime, endTime, parameters);
        } else if (getWrapped() instanceof StatementWrapper) {
            return statementPostExecute((StatementWrapper)getWrapped(), methodName, result, startTime, endTime, parameters);
        } else {
            return new InterceptResult();
        }
    }

    public InterceptResult preparedStatementPostExecute(PreparedStatementWrapper ps, String methodName, Object result, long startTime, long endTime, Object... parameters) {
        if (methodName.startsWith("execute")) {
            long elapsed = endTime-startTime;
            preparedStatementExecute(methodName, elapsed, ps.getStatement(), psArgs.toArray());
        }

        return new InterceptResult();
    }

    public InterceptResult statementPostExecute(StatementWrapper ps, String methodName, Object result, long startTime, long endTime, Object... parameters) {
        if (methodName.startsWith("execute")) {
            long elapsed = endTime-startTime;
            statementExecute(methodName, elapsed, parameters);
        }

        return new InterceptResult();
    }

    public void preparedStatementExecute(String methodName, long elapsed, String sql, Object[] args) {
        if (elapsed > longRunningThreshold) {
            String dynamicSql = sqlBuilder.buildDynamicSqlForLog(sql, args, null);
            log.warn("Long Running (" + elapsed + "ms.) " + dynamicSql.trim());
        }
        if (log.isInfoEnabled()) {
            String dynamicSql = sqlBuilder.buildDynamicSqlForLog(sql, args, null);
            log.info("PreparedStatement." + methodName + " (" + elapsed + "ms.) " + dynamicSql.trim()) ;
        }
    }

    public void statementExecute(String methodName, long elapsed, Object... parameters) {
        if (elapsed > longRunningThreshold) {
            log.warn("Long Running (" + elapsed + "ms.) " + Arrays.toString(parameters));
        }
        if (log.isInfoEnabled()) {
            log.info("Statement." + methodName + " (" + elapsed + "ms.) " + Arrays.toString(parameters));
        }
    }

    @Override
    public void cleanupExecute(String methodName, Exception thrownException) {
        SqlWatchdog.sqlEnd(sqlKey);
        if (thrownException != null
            && log.isDebugEnabled()
            && (getWrapped() instanceof PreparedStatementWrapper)) {

            PreparedStatementWrapper ps = (PreparedStatementWrapper) getWrapped();
            String sql = sqlBuilder.buildDynamicSqlForLog(ps.getStatement(), psArgs.toArray(), null);

            log.debug("SQL Caused Exception " + sql, thrownException);
        }
    }
}

