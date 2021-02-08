package org.jumpmind.pos.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class DriversLicense {
    private String rawData;
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
}

