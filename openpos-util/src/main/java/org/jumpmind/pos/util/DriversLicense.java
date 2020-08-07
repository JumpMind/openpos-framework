package org.jumpmind.pos.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DriversLicense {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("MMddyyyy");

    private Map<String, String> elements;

    private String firstLine;

    private String complianceIndicator;

    private String fileType;

    private String issuerIDNumber;

    private String AAMVABarCodeVersion;

    private String jurisdictionBarCodeVersion;

    private String numberOfSubFiles;
    
    private String subfileDesignators;

    private String jurisdictionVehicleClass;

    private String jurisdictionRestrictionCode;

    private String jurisdictionEndorsementCode;

    private Date expirationDate;

    private String lastName;

    private String firstName;

    private String[] middleNames;

    private Date issueDate;

    private Character sex;

    private Date dateOfBirth;

    private String eyeColor;

    private String height;

    private String addressStreet1;

    private String city;

    private String stateCode;

    private String postalCode;

    private String idNumber;

    private String documentDiscriminator;

    private String countryID;

    private Character lastNameTruncationStatus;

    private Character firstNameTruncationStatus;

    private Character middleNameTruncationStatus;

    private String addressStreet2;

    private String hairColor;

    private String placeOfBirth;

    private String auditInformation;

    private String inventoryControlNumber;

    private String lastNameAlias;

    private String firstNameAlias;

    private String nameSuffixAlias;

    private String nameSuffix;

    private Integer weightRange;

    private String raceOrEthnicity;

    private String standardVehicleClassification;

    private String standardEndorsementCode;

    private String standardRestrictionCode;

    private String jurisdictionVehicleClassDescription;

    private String jurisdictionEndorsementCodeDescription;

    private String jurisdictionRestrictionCodeDescription;

    private Character complianceType;

    private Date cardRevisionDate;

    private Date hazmatEndorsementExpirationDate;

    private boolean limitedDurationIndicator;

    private Integer weightInPounds;

    private Integer weightInKilograms;

    private Date under18Until;

    private Date under19Until;

    private Date under21Until;

    private boolean organDonorIndicator;

    private boolean veteranIndicator;

    public DriversLicense() {
        elements = new HashMap<>();
    }

    public DriversLicense(String data) {
        elements = new HashMap<>();
        parse(data);
        setAttributes();
    }

    public void parse(String data)  {
        elements.clear();
        String[] lines = data.split("\r\n|\n|\r");
        complianceIndicator = lines[0];
        fileType = lines[1].substring(0, 5);
        issuerIDNumber = lines[1].substring(5, 11);
        AAMVABarCodeVersion = lines[1].substring(11, 13);
        jurisdictionBarCodeVersion = lines[1].substring(13, 15);
        numberOfSubFiles = lines[1].substring(15, 17);
        int lengthOfSubfileDesignators = 10 * Integer.parseInt(numberOfSubFiles);
        subfileDesignators = lines[1].substring(17, 17 + lengthOfSubfileDesignators);
        //skip subfile name and put in the first element
        elements.put(
                lines[1].substring(17 + lengthOfSubfileDesignators + 2, 17 + lengthOfSubfileDesignators + 5),
                lines[1].substring(17 + lengthOfSubfileDesignators + 5)
                );
        for (int i = 2; i < lines.length; ++i) {
            String line = lines[i];
            if (line.length() > 2) {
               elements.put(line.substring(0, 3), line.substring(3));
            }
        }
        setAttributes();
    }

    private void setAttributes() {
        try {

            jurisdictionVehicleClass = getString("DCA");
            jurisdictionRestrictionCode = getString("DCB");
            jurisdictionEndorsementCode = getString("DCD");
            expirationDate = parseDate("DBA");
            lastName = getString("DCS");
            firstName = getString("DAC");
            middleNames = getString("DAD").split(",");
            issueDate = parseDate("DBD");
            sex = Objects.equals(getString("DBC"), "9") ? 'U' : (Objects.equals(getString("DBC"),"1")) ? 'M' : 'F';
            dateOfBirth = parseDate("DDB");
            eyeColor = getString("DAY");
            height = getString("DAU");
            addressStreet1 = getString("DAG");
            city = getString("DAI");
            stateCode = getString("DAJ");
            postalCode = getString("DAK");
            idNumber = getString("DAQ");
            documentDiscriminator = getString("DCF");
            countryID = getString("DCG");
            lastNameTruncationStatus = StringUtils.isNotEmpty(getString("DDE")) ? getString("DDE").charAt(0) : null;
            firstNameTruncationStatus = StringUtils.isNotEmpty(getString("DDF")) ? getString("DDF").charAt(0) : null;
            middleNameTruncationStatus = StringUtils.isNotEmpty(getString("DDG")) ? getString("DDG").charAt(0) : null;

            //optional
            addressStreet2 = getString("DAH");
            hairColor = getString("DAZ");
            placeOfBirth = getString("DCI");
            auditInformation = getString("DCJ");
            inventoryControlNumber = getString("DCK");
            lastNameAlias = getString("DBN");
            firstNameAlias = getString("DBG");
            nameSuffixAlias = getString("DBS");
            nameSuffix = getString("DCU");
            weightRange = parseInt("DCE");
            raceOrEthnicity = getString("DCL");
            standardVehicleClassification = getString("DCM");
            standardEndorsementCode = getString("DCN");
            standardRestrictionCode = getString("DCO");
            jurisdictionVehicleClassDescription = getString("DCP");
            jurisdictionEndorsementCodeDescription = getString("DCQ");
            jurisdictionRestrictionCodeDescription = getString("DCR");
            complianceType = (getString("DDA") != null && getString("DDA").length() > 1) ? getString("DDA").charAt(0) : null;
            cardRevisionDate = parseDate("DDB");
            hazmatEndorsementExpirationDate = parseDate("DDC");
            limitedDurationIndicator = getString("DDD") != null && getString("DDD").contains("1");
            weightInPounds = parseInt("DAW");
            weightInKilograms = parseInt("DAX");
            under18Until = parseDate("DDH");
            under19Until = parseDate("DDI");
            under21Until = parseDate("DDJ");
            organDonorIndicator = getString("DDK") != null && getString("DDK").contains("1");
            veteranIndicator = getString("DDL") != null && getString("DDL").contains("1");
        } catch (Exception e) {
            log.warn("Failed to parse Driver's License, most likely due to an incorrectly formatted value.", e);
        }

    }

    private String getString(String elementName) {
        if(elements.containsKey(elementName)){
            return elements.get(elementName);
        }

        return null;
    }

    private Integer parseInt(String elementName) {
        String value = getString(elementName);
        try {
            return StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : null;
        }
        catch (Exception e) {
            log.warn("Unable to parse value {} as integer for Driver License element {}", value, elementName, e);
            return null;
        }
    }

    private Date parseDate(String elementName) {
        Date parsedDate = null;
        String value = getString(elementName);
        if (StringUtils.isNotEmpty(value)) {
            try {
                parsedDate = DATE_FORMAT.parse(value);
            }
            catch (Exception e) {
                log.warn("Unable to parse value {} as date for Driver License element {}", value, elementName, e);
            }
        }
        return parsedDate;
    }

    public String getFileType() { return fileType; }

    public String getIssuerIDNumber() { return issuerIDNumber; }

    public String getAAMVABarCodeVersion() { return AAMVABarCodeVersion; }

    public String getJurisdictionBarCodeVersion() { return jurisdictionBarCodeVersion; }

    public String getNumberOfSubFiles() { return numberOfSubFiles; }

    public String getJurisdictionVehicleClass() { return jurisdictionVehicleClass; }

    public String getJurisdictionRestrictionCode() { return jurisdictionRestrictionCode; }

    public String getHurisdictionEndorsementCode() { return jurisdictionEndorsementCode; }

    public Date getExpirationDate() { return expirationDate; }

    public String getLastName() { return lastName; }

    public String getFirstName() { return firstName; }

    public String[] getMiddleNames() { return middleNames; }

    public Date getIssueDate() { return issueDate; }

    public Character getSex() { return sex; }

    public Date getDateOfBirth() { return dateOfBirth; }

    public String getEyeColor() { return eyeColor; }

    public String getHeight() { return height; }

    public String getAddressStreet1() { return addressStreet1; }

    public String getCity() { return city; }

    public String getStateCode() { return stateCode; }

    public String getPostalCode() { return postalCode; }

    public String getIDNumber() { return idNumber; }

    public String getDocumentDiscriminator() { return documentDiscriminator; }

    public String getCountryID() { return countryID; }

    public Character getLastNameTruncationStatus() { return lastNameTruncationStatus; }

    public Character getFirstNameTruncationStatus() { return firstNameTruncationStatus; }

    public Character getMiddleNameTruncationStatus() { return middleNameTruncationStatus; }

    public String getAddressStreet2() { return addressStreet2; }

    public String getHairColor() { return hairColor; }

    public String getPlaceOfBirth() { return placeOfBirth; }

    public String getAuditInformation() { return auditInformation; }

    public String getInventoryControlNumber() { return inventoryControlNumber; }

    public String getLastNameAlias() { return lastNameAlias; }

    public String getFirstNameAlias() { return firstNameAlias; }

    public String getNameSuffixAlias() { return nameSuffixAlias; }

    public String getNameSuffix() { return nameSuffix; }

    public Integer getWeightRange() { return weightRange; }

    public String getRaceOrEthnicity() { return raceOrEthnicity; }

    public String getStandardVehicleClassification() { return standardVehicleClassification; }

    public String getStandardEndorsementCode() { return standardEndorsementCode; }

    public String getStandardRestrictionCode() { return standardRestrictionCode; }

    public String getJurisdictionVehicleClassDescription() { return jurisdictionVehicleClassDescription; }

    public String getJurisdictionEndorsementCodeDescription() { return jurisdictionEndorsementCodeDescription; }

    public String getJurisdictionRestrictionCodeDescription() { return jurisdictionRestrictionCodeDescription; }

    public Character getComplianceType() { return complianceType; }

    public Date getCardRevisionDate() { return cardRevisionDate; }

    public Date getHazmatEndorsementExpirationDate() { return hazmatEndorsementExpirationDate; }

    public boolean getLimitedDurationIndicator() { return limitedDurationIndicator; }

    public Integer getWeightInPounds() { return weightInPounds; }

    public Integer getWeightInKilograms() { return weightInKilograms; }

    public Date getUnder18Until() { return under18Until; }

    public Date getUnder19Until() { return under19Until; }

    public Date getUnder21Until() { return under21Until; }

    public boolean getOrganDonorIndicator() { return organDonorIndicator; }

    public boolean getVeteranIndicator() { return veteranIndicator; }
}
