package org.jumpmind.pos.tax.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@DependsOn(value = { "TaxModule" })
public class TaxRepository {

    private Query<Jurisdiction> taxJurisdictionLookup = new Query<Jurisdiction>().named("taxJurisdictionLookup").result(Jurisdiction.class);

    private Query<GroupRule> taxGroupRuleLookup = new Query<GroupRule>().named("taxGroupRuleLookup").result(GroupRule.class);
    
    private Query<RateRule> taxRateRuleLookup = new Query<RateRule>().named("taxRateRuleLookup").result(RateRule.class);
    
    @Autowired
    @Qualifier("taxDbSession")
    @Lazy
    private DBSession dbSession;

    public List<Jurisdiction> findTaxJurisdictions(String geoCode) {
        List<Jurisdiction> jurisdictions = dbSession.query(taxJurisdictionLookup, geoCode);
        for (Jurisdiction jurisdiction : jurisdictions) {
			String authorityID = jurisdiction.getAuthorityId();
			Authority authority = dbSession.findByNaturalId(Authority.class, authorityID);
			jurisdiction.setAuthority(authority);
			List<GroupRule> groupRules = dbSession.query(taxGroupRuleLookup, authorityID);
			for (GroupRule groupRule : groupRules) {
				authority.addGroupRule(groupRule);
				groupRule.setAuthority(authority);
				String groupID = groupRule.getGroupId();
				Group group = dbSession.findByNaturalId(Group.class, groupID);
				groupRule.setGroup(group);
				Map<String, Object> params = new HashMap<>();
				params.put("groupID", groupID);
				params.put("authorityID", authorityID);
				List<RateRule> rateRules = dbSession.query(taxRateRuleLookup, params);
				for (RateRule rateRule : rateRules) {
					groupRule.addRateRule(rateRule);
				}
				
			}
		}
        return jurisdictions;
    }

}
