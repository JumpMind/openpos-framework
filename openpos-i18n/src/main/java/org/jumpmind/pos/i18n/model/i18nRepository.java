package org.jumpmind.pos.i18n.model;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@DependsOn(value = { "i18nModule" })
public class i18nRepository {

    final Logger log = LoggerFactory.getLogger(getClass());

    private Query<Resource> patternLookup = new Query<Resource>().named("patternLookup").result(Resource.class);

    @Autowired
    @Qualifier("i18nSession")
    @Lazy
    private DBSession dbSession;

    private Map<String, Object> generateHashMap(String base, String key, String locale, String brand) {
        Map<String, Object> params = new HashMap<>();
        params.put("brand", brand != null ? brand : "*");
        params.put("noBrand", "*");
        params.put("base", base);
        params.put("key", key);
        params.put("locale", locale != null ? locale : Locale.getDefault().toString());
        return params;
    }

    public String getString(String base, String key, String locale, String brand) {        
        Map<String, Object> params = generateHashMap(base, key, locale, brand);
        List<Resource> phrases = dbSession.query(patternLookup, params);
        String string = null;
        if (phrases != null) {
            Resource row = phrases.stream().filter((p) -> p.getBrand().equals(brand)).findFirst().orElse(null);
            if (row == null) {
                row = phrases.stream().filter((p) -> p.getBrand().equals("*")).findFirst().orElse(null);
            }
            string = row.getPattern();
        }
        return string;
    }

    public void save(Resource resource) {
        dbSession.save(resource);
    }
}