package org.jumpmind.pos.i18n.service;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jumpmind.pos.i18n.service.i18nEndpoint;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.cars.TestConfig;
import org.jumpmind.pos.i18n.model.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
public class i18nEndpointTest {	
	
    @Autowired
	i18nEndpoint i18nEndpoint;	
    
    @Autowired
    @Qualifier("i18nSession")
    private DBSession dbSession; 
	
	 
	/**
	 * Test missing resource with incorrect base with a brand field
	 */
	@Test
	public void getStringMissingResourceBaseTest () {
		String base = "nothing";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	 
	/**
	 * Test missing resource with incorrect base with no brand field
	 */
	@Test
	public void getStringMissingResourceBaseNoBrandTest () {
		String base = "nothing";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	 
	/**
	 *  Test missing resource with a properties file that exists, but bad key
	 */
	@Test
	public void getStringMissingResourceKeyTest () {
		String base = "test";
		String key = "_nothing";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	 
	/**
	 *  Test missing resource with a properties file and brand that exists, but bad key
	 */
	@Test
	public void getStringMissingResourceKeyNoBrandTest () {
		String base = "test";
		String key = "_nothing";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	 
	/**
	 *  Test for an unrecognized locale to default to the default brand file
	 */
	@Test
	public void getStringIncorrectToDefaultLocaleTest () {
		String base = "test";
		String key = "_company";
		Locale locale = new Locale("foo", "foh");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<Company Name>");
	}
	
	 
	/**
	 *  Test for an unrecognized locale to default to the default locale file
	 */
	@Test
	public void getStringIncorrectToDefaultLocaleNoBrandTest () {
		String base = "test";
		String key = "_me";
		Locale locale = new Locale("foo", "foh");
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "me");
	}
	
	 
	/**
	 *  Test for a bad brand with no key in any test_other file
	 */
	@Test
	public void getStringMissingResourceBrandTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.FRANCE;
		String brand = "4Tran";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	 
	/**
	 *  Test for a bad brand with antest_other valid key in antest_other file
	 */
	@Test
	public void getStringBadBrandUseLocaleTest () {
		String base = "test";
		String key = "_company";
		Locale locale = Locale.FRANCE;
		String brand = "4Tran";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<Enterprise Titre>");
	}
	
	 
	/**
	 *  Basic Translation from locale file Test
	 */
	@Test
	public void getStringTranslateNoBrandTest () {
		String base = "test";
		String key = "_company";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<Enterprise Titre>");
	}
	
	 
	/**
	 *  Basic locale override test
	 */
	@Test
	public void getStringTranslateOverrideNoBrandTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<Las Enterprise>");
	}
	
	 
	/**
	 *  Test to get from brand file from default locale
	 */
	@Test
	public void getStringBrandDefaultTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Big Lots");
	}
	
	 
	/**
	 *  Test to ensure the brand override file works
	 */
	@Test
	public void getStringBrandDefaultOverrideTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Biggest Lots");
	}
	
	 
	/**
	 *  Test to translate from brand file
	 */
	@Test
	public void getStringTranslateBrandTest () {
		String base = "test";
		String key = "_company1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Grand Lots");
	}
	
	 
	/**
	 *  Test to override translation from brand file
	 */
	@Test
	public void getStringTranslateBrandOverrideTest () {
		String base = "test";
		String key = "_company2";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Plus Grand Lots");
	}
	
	 
	/**
	 * 	Test to ensure retrieving english strings works
	 */
	@Test
	public void getStringDefaultNoBrandTest () {
		String base = "test";
		String key = "_you";
		Locale locale = Locale.getDefault();
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "you");
	}
	
	 
	/**
	 *  Test to ensure brand does not interfere with getting english strings
	 */
	@Test
	public void getStringDefaultBrandNoConseqTest () {
		String base = "test";
		String key = "_you";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "you");
	}
	
	
	 
	/**
	 * Test to ensure brand does not interfere with translation
	 */
	@Test
	public void getStringTranslateBrandNoConseqTest () {
		String base = "test";
		String key = "_me";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "moi");
	}
	
	 
	/**
	 *  Test where (translation) key is in the override file but not the base locale file
	 */
	@Test
	public void getStringTranslateInOverrideNotBaseTest () {
		String base = "test";
		String key = "_override";
		Locale locale = Locale.FRANCE;
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Je m'appelle");
	}
	
	 
	/**
	 *  Test where (english) key is in the override file but not the base locale file
	 */
	@Test
	public void getStringDefaultInOverrideNotBaseTest () {
		String base = "test";
		String key = "_override";
		Locale locale = Locale.getDefault();
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "My name is");
	}
	
	 
	/**
	 * Test to get (english) key in the brand override file, not the base brand file
	 */
	@Test
	public void getStringDefaultInBrandOverrideNotBrandTest () {
		String base = "test";
		String key = "_overrideB";
		Locale locale = Locale.getDefault();
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Biggest Lots Override");
	}
	
	 
	/**
	 * Test to get (translation) key in the brand override file, not the base brand file
	 */
	@Test
	public void getStringTranslateInBrandOverrideNotBrandTest () {
		String base = "test";
		String key = "_overrideB";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Plus Grand Lots Override");
	}
	
	 
	/**
	 * Test to get (translation) key in the brand file, not the base locale file
	 */
	@Test
	public void getStringTranslateInBrandNotBaseTest () {
		String base = "test";
		String key = "_logo_color";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "jaune");
	}
	
	 
	/**
	 * Test where locale key field is blank
	 */
	@Test
	public void getStringTranslateEmptyField () {
		String base = "test";
		String key = "_empty";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "");
	}
	
	 
	/**
	 * Test where brand override key field is blank, but brand is not
	 */
	@Test
	public void getStringTranslateEmptyFieldInOverride () {
		String base = "test";
		String key = "_logo_color1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "");
	}
	
     
	 
	/**
	 * Test to translate a key in a base, not "test"
	 */
	@Test
	public void getStringTranslateDifferentBaseTest () {
		String base = "test_other";
		String key = "_are";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "\u00eates");
	}
	
	 
	/**
	 * Test to translate a key in a non-French, non-constant Locale with no brand
	 */
	@Test
	public void getStringTranslateNonConstantLocaleNoBrandTest () {
		String base = "test";
		String key = "_hello";
		Locale locale = new Locale ("es", "MX");
		String brand = "";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Hola!");
	}
	
	 
	/**
	 * Test to translate a key in a non-French, non-constant Locale with a Brand
	 */
	@Test
	public void getStringTranslateNonConstantLocale () {
		String base = "test";
		String key = "_hello";
		Locale locale = new Locale ("es", "MX");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "Hola!");
	}
	
	 
	/**
	 * Test where key is bad, but good in different locale
	 */
	@Test
	public void getStringKeyInDifferentLocale () {
		String base = "test";
		String key = "_you";
		Locale locale = new Locale ("es", "MX");
		String brand = "BigLots";
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand);
		Assert.assertEquals(res, "<MISSING RESOURCE>");
	}
	
	
	/* 	Message Formatter Tests */
	
	 
	/**
	 *  Test for date and time format with NO format specifier
	 */
	@Test
	public void getStringMessageFormatDateTimeTest () {
		String base = "test";
		String key = "_test_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "At " + DateFormat.getTimeInstance().format(date) + 
				" on " + DateFormat.getDateInstance().format(date);

		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for date and time format with SHORT format specifier
	 */
	@Test
	public void getStringMessageFormatDateTimeShortTest () {
		String base = "test";
		String key = "_test_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "At " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date) + 
				" on " + DateFormat.getDateInstance(DateFormat.SHORT).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for TRANSLATION date and time format with NO format specifier
	 */
	@Test
	public void getStringMessageTranslateFormatDateTimeTest () {
		String base = "test";
		String key = "_test_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "\u00c0 " + DateFormat.getTimeInstance(DateFormat.DEFAULT, locale).format(date) + 
				" en " + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
     
	 
	/**
	 *  Test for TRANSLATION date and time format with SHORT format specifier
	 */
	@Test
	public void getStringMessageTranslateFormatDateTimeShortTest () {
		String base = "test";
		String key = "_test_string1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Date date = new Date();
		Object[] args = {
				date,
		};
		
		String exp = "\u00c0 " + DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date) + 
				" en " + DateFormat.getDateInstance(DateFormat.SHORT, locale).format(date);

		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for currency format US Eng
	 */
	@Test
	public void getStringMessageFormatCurrencyTest () {
		String base = "test";
		String key = "_currency_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				1,
		};

		String exp = "I have $1.00"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
     
	 
	/**
	 *  Test for currency format French
	 */
	@Test
	public void getStringMessageTranslateFormatCurrencyTest () {
		String base = "test";
		String key = "_currency_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				1,
		};

		String exp = "J'ai 1,00 \u20ac"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for number format in US Eng
	 */
	@Test
	public void getStringMessageFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "It's the number 34,592.09"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for currency format in French
	 */
	@Test
	public void getStringMessageTranslateFrFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "C'est le nombre 34\u00a0592,09"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for number format in German
	 */
	@Test
	public void getStringMessageTranslateDeFormatNumberTest () {
		String base = "test";
		String key = "_number_string0";
		Locale locale = Locale.GERMANY;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "Es ist die Nummer 34.592,09"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for number (INTEGER) format US Eng
	 */
	@Test
	public void getStringMessageFormatNumberIntegerTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "It's the number 34,592"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for number (INTEGER) format US French
	 */
	@Test
	public void getStringMessageTranslateFormatNumberIntegerTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				34592.09,
		};

		String exp = "C'est le nombre 34\u00a0592"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for conflicting parameter passed
	 */
	@Test
	public void getStringMessageBadParameterNumberTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				"DC makes a good movie",
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for no parameters passed
	 */
	@Test
	public void getStringMessageNoParameterNumberTest () {
		String base = "test";
		String key = "_number_string1";
		Locale locale = Locale.US;
		String brand = "BigLots";
		
		Object[] args = {
				
		};

		String exp = "It's the number {0}"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for pattern with invalid format passed with no args
	 */
	@Test
	public void getStringMessageBadPatternNoArgsTest () {
		String base = "test";
		String key = "_bad_pattern0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				,
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for pattern with invalid format passed with no args
	 */
	@Test
	public void getStringMessageBadPatternWithArgsTest () {
		String base = "test";
		String key = "_bad_pattern0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				"small cats",
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for pattern with invalid (not a number) parameter reference
	 */
	@Test
	public void getStringMessageBadPatternBadReferenceTest () {
		String base = "test";
		String key = "_bad_pattern1";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "<UNABLE TO APPLY PATTERN>"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for pattern with custom style string
	 */
	@Test
	public void getStringMessageBadPatternWrongFormatSpecTest () {
		String base = "test";
		String key = "_custom_style0";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "Nous avons \"custom_style\"45"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	 
	/**
	 *  Test for pattern with quoted brackets
	 */
	@Test
	public void getStringMessageQuotedTest () {
		String base = "test";
		String key = "_quoted";
		Locale locale = Locale.FRANCE;
		String brand = "BigLots";
		
		Object[] args = {
				45,
		};

		String exp = "Nous avons {0, number}"; 
		
		String res = i18nEndpoint.getString(base, key, locale.toString(), brand, args);
		Assert.assertEquals(res, exp);
	}
	
	/**
	 *  Basic database test query
	 */
	@Test
	public void getStringDatabaseNoOverlapTest () {
 
            Resource resource = new Resource();
            resource.setBaseName("common");
            resource.setLocale(Locale.US.toString());
            resource.setBrand("Target");
            resource.setStringKey("_hello");
            
            resource.setPattern("hi there");
            dbSession.save(resource);
            
            String res = i18nEndpoint.getString("common", "_hello", Locale.US.toString(), "Target");
            String exp = "hi there";
            
            Assert.assertEquals(exp, res);

	}
	
	/**
	 *  Test to ensure the no brand function works
	 */
	@Test
	public void getStringDatabaseNoBrandTest () {
		  
            Resource resource = new Resource();
            resource.setBaseName("common");
            resource.setLocale(Locale.US.toString());
            resource.setBrand("*");
            resource.setStringKey("_test");
            
            resource.setPattern("test");
            dbSession.save(resource);
            
            String res = i18nEndpoint.getString("common", "_test", Locale.US.toString(), "");
            String exp = "test";
            
            Assert.assertEquals(exp, res);

	}
	
	/**
	 *  Test to ensure database is chosen first in getting string
	 */
	@Test
	public void getStringDatabaseOverlapTest () {
		
            Resource resource = new Resource();
            resource.setBaseName("common");
            resource.setLocale(Locale.US.toString());
            resource.setBrand("*");
            resource.setStringKey("_me");
            
            resource.setPattern("myself");
            dbSession.save(resource);
            
            String res = i18nEndpoint.getString("common", "_me", Locale.US.toString(), "");
            String exp = "myself";
            
            Assert.assertEquals(exp, res);

	}
	
	/**
	 *  Test query the database where there are multiple locales
	 */
	@Test 
	public void getStringDatabaseMultiLocaleTest () {
		  
            Resource resource = new Resource();
            resource.setBaseName("common");
            resource.setLocale(Locale.US.toString());
            resource.setBrand("*");
            resource.setStringKey("_please");
            resource.setPattern("Please");
            
            dbSession.save(resource);
            
            Resource resource1 = new Resource();
            resource1.setBaseName("common");
            resource1.setLocale(Locale.FRANCE.toString());
            resource1.setBrand("*");
            resource1.setStringKey("_please");
            resource1.setPattern("Puis");
            
            dbSession.save(resource1);
            
            String res = i18nEndpoint.getString("common", "_please", Locale.US.toString(), "");
            String exp = "Please";
            
            String res1 = i18nEndpoint.getString("common", "_please", Locale.FRANCE.toString(), "");
            String exp1 = "Puis";
            
            Assert.assertEquals(exp, res);
            Assert.assertEquals(exp1, res1);   
        }
		
		/**
		 *  Test database query with brand override function
		 */
	@Test
	public void getStringDatabaseBrandOverrideTest () {
		
	            Resource resource1 = new Resource();
	            resource1.setBaseName("common");
	            resource1.setLocale(Locale.US.toString());
	            resource1.setBrand("*");
	            resource1.setStringKey("_company");
	            resource1.setPattern("<company>");
	            
	            dbSession.save(resource1);
	            
	            Resource resource = new Resource();
	            resource.setBaseName("common");
	            resource.setLocale(Locale.US.toString());
	            resource.setBrand("Target");
	            resource.setStringKey("_company");
	            resource.setPattern("Target");
	            
	            dbSession.save(resource);
	            
	            String res = i18nEndpoint.getString("common", "_company", Locale.US.toString(), "");
	            String exp = "<company>";
	            
	            String res1 = i18nEndpoint.getString("common", "_company", Locale.US.toString(), "Target");
	            String exp1 = "Target";
	            
	            Assert.assertEquals(exp, res);
	            Assert.assertEquals(exp1, res1);
	}
	
	/**		 
	 *   Test database query with format pattern
	 */
	@Test 
	public void getStringDatabaseFormatTest () {
			
	            Resource resource = new Resource();
	            resource.setBaseName("common");
	            resource.setLocale(Locale.US.toString());
	            resource.setBrand("*");
	            resource.setStringKey("_test_string1");
	            
	            resource.setPattern("At {0, time, short} on {0, date, short}");
	            dbSession.save(resource);
	            
	            Date date = new Date();
	    		Object[] args = {
	    				date,
	    		};
	    		
	            String res = i18nEndpoint.getString("common", "_test_string1", Locale.US.toString(), "", args);
	    		
	    		
	    		String exp = "At " + DateFormat.getTimeInstance(DateFormat.SHORT).format(date) + 
	    				" on " + DateFormat.getDateInstance(DateFormat.SHORT).format(date);
	            
	            Assert.assertEquals(exp, res);
		}
		
		/**
		 *  Test database query with bad pattern
		 */
		@Test
		public void getStringDatabaseFormatBadPatternTest () {
			
	            Resource resource = new Resource();
	            resource.setBaseName("common");
	            resource.setLocale(Locale.US.toString());
	            resource.setBrand("*");
	            resource.setStringKey("_test_string1");
	            
	            resource.setPattern("At {0, BAD, short} on {0, date, short}");
	            dbSession.save(resource);
	            
	            Date date = new Date();
	    		Object[] args = {
	    				date,
	    		};
	    		
	            String res = i18nEndpoint.getString("common", "_test_string1", Locale.US.toString(), "", args);
	    		
	    		
	    		String exp = "<UNABLE TO APPLY PATTERN>";
	            
	            Assert.assertEquals(exp, res);
		}
	
		
}
