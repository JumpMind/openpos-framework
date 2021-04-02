package org.jumpmind.pos.coupons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GS1Coupon {

    String applicationIdentifer;

    int primaryCompanyPrefixVLI;
    String primaryCompanyPrefix;
    String offerCode;
    int saveValueVLI;
    int saveValue;
    int primaryPurchaseRequirementVLI;
    int primaryPurchaseRequirement;
    String primaryPurchaseRequirementCode;
    String primaryPurchaseFamilyCode;

    // Optional data field 1: second qualifying purchase
    String secondaryAdditionalPurchaseRulesCode;
    int secondaryPurchaseRequirementVLI;
    int secondaryPurchaseRequirement;
    String secondaryPurchaseRequirementCode;
    String secondaryPurchaseFamilyCode;
    int secondaryPurchaseCompanyPrefixVLI;
    String secondaryPurchaseCompanyPrefix;

    // Optional data field 2: third qualifying purchase
    int tertiaryPurchaseRequirementVLI;
    int tertiaryPurchaseRequirement;
    String tertiaryPurchaseRequirementCode;
    String tertiaryPurchaseFamilyCode;
    int tertiaryPurchaseCompanyPrefixVLI;
    String tertiaryPurchaseCompanyPrefix;

    // Optional data field 3: expiry date
    Date expirationDate;

    // Optional data field 4: start date
    String startDate;

    // Optional data field 5: serial number
    int serialNumberVLI;
    String serialNumber;

    // Optional data field 6: retailer ID
    int retailerCompanyPrefixVLI;
    String retailerCompanyPrefixOrGLN;

    // Optional data field 9: miscellaneous
    String saveValueCode = "0";
    String saveValueAppliesToWhichItem = "0";
    String storeCouponFlag = "0";
    String doNotMultiplyFlag = "0";

    public static boolean isGS1Databar(String gs1Databar) {
        gs1Databar = gs1Databar.trim();
        return gs1Databar.startsWith("8110");
    }

    public GS1Coupon(String gs1Databar) throws ParseException {
        if (!isGS1Databar(gs1Databar)) {
            throw new NumberFormatException("Not a gs1 databar string");
        }

        int i = 0;
        applicationIdentifer = gs1Databar.substring(i, i + 4);
        i += 4;

        primaryCompanyPrefixVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
        primaryCompanyPrefix = gs1Databar.substring(i, i + 6 + primaryCompanyPrefixVLI);
        i += 6 + primaryCompanyPrefixVLI;

        offerCode = gs1Databar.substring(i, i + 6);
        i += 6;

        saveValueVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
        saveValue = Integer.parseInt(gs1Databar.substring(i, i + saveValueVLI));
        i += saveValueVLI;

        primaryPurchaseRequirementVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
        primaryPurchaseRequirement = Integer.parseInt(gs1Databar.substring(i, i + primaryPurchaseRequirementVLI));
        i += primaryPurchaseRequirementVLI;

        primaryPurchaseRequirementCode = gs1Databar.substring(i, ++i);

        primaryPurchaseFamilyCode = gs1Databar.substring(i, i + 3);
        i += 3;

        if (i < gs1Databar.length()) {
            int optionalFieldIndctr = Integer.parseInt(gs1Databar.substring(i, ++i));
            while (i < gs1Databar.length()) {
                switch (+optionalFieldIndctr) {
                    case 1:
                        // Second qualifying purchase
                        secondaryAdditionalPurchaseRulesCode = gs1Databar.substring(i, ++i);
                        secondaryPurchaseRequirementVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        secondaryPurchaseRequirement = Integer.parseInt(gs1Databar.substring(i, i + +secondaryPurchaseRequirementVLI));
                        i += +secondaryPurchaseRequirementVLI;

                        secondaryPurchaseRequirementCode = gs1Databar.substring(i, ++i);
                        secondaryPurchaseFamilyCode = gs1Databar.substring(i, i + 3);
                        i += 3;

                        secondaryPurchaseCompanyPrefixVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        if (secondaryPurchaseCompanyPrefixVLI <= 6) {
                            secondaryPurchaseCompanyPrefix = gs1Databar.substring(i, i + 6 + +secondaryPurchaseCompanyPrefixVLI);
                            i += 6 + +secondaryPurchaseCompanyPrefixVLI;
                        }
                        break;

                    case 2:
                        // Third qualifying purchase
                        tertiaryPurchaseRequirementVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        tertiaryPurchaseRequirement = Integer.parseInt(gs1Databar.substring(i, i + +tertiaryPurchaseRequirementVLI));
                        i += +tertiaryPurchaseRequirementVLI;

                        tertiaryPurchaseRequirementCode = gs1Databar.substring(i, ++i);
                        tertiaryPurchaseFamilyCode = gs1Databar.substring(i, i + 3);
                        i += 3;

                        tertiaryPurchaseCompanyPrefixVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        if (tertiaryPurchaseCompanyPrefixVLI <= 6) {
                            tertiaryPurchaseCompanyPrefix = gs1Databar.substring(i, i + 6 + +tertiaryPurchaseCompanyPrefixVLI);
                            i += 6 + +tertiaryPurchaseCompanyPrefixVLI;
                        }
                        break;

                    case 3:
                        // Expiry date
                        String date = gs1Databar.substring(i, i + 6);
                        Date parsedDate = new SimpleDateFormat("yyMMdd").parse(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parsedDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        expirationDate  = calendar.getTime();
                        i += 6;
                        break;

                    case 4:
                        // Start date
                        startDate = gs1Databar.substring(i, i + 6);
                        i += 6;
                        break;

                    case 5:
                        // Serial number
                        serialNumberVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        serialNumber = gs1Databar.substring(i, i + 6 + +serialNumberVLI);
                        i += 6 + +serialNumberVLI;
                        break;

                    case 6:
                        // Retailer identification
                        retailerCompanyPrefixVLI = Integer.parseInt(gs1Databar.substring(i, ++i));
                        retailerCompanyPrefixOrGLN = gs1Databar.substring(i, i + 7 + +retailerCompanyPrefixVLI);
                        i += 7 + +retailerCompanyPrefixVLI;
                        break;

                    case 9:
                        // Miscellaneous
                        saveValueCode = gs1Databar.substring(i, ++i);
                        saveValueAppliesToWhichItem = gs1Databar.substring(i, ++i);
                        storeCouponFlag = gs1Databar.substring(i, ++i);
                        doNotMultiplyFlag = gs1Databar.substring(i, ++i);
                        break;
                }

                if (i < gs1Databar.length()) {
                    optionalFieldIndctr = Integer.parseInt(gs1Databar.substring(i, ++i));
                }
            }
        }
    }

    public String getApplicationIdentifer() {
        return applicationIdentifer;
    }

    public void setApplicationIdentifer(String applicationIdentifer) {
        this.applicationIdentifer = applicationIdentifer;
    }

    public int getPrimaryCompanyPrefixVLI() {
        return primaryCompanyPrefixVLI;
    }

    public void setPrimaryCompanyPrefixVLI(int primaryCompanyPrefixVLI) {
        this.primaryCompanyPrefixVLI = primaryCompanyPrefixVLI;
    }

    public String getPrimaryCompanyPrefix() {
        return primaryCompanyPrefix;
    }

    public void setPrimaryCompanyPrefix(String primaryCompanyPrefix) {
        this.primaryCompanyPrefix = primaryCompanyPrefix;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public int getSaveValueVLI() {
        return saveValueVLI;
    }

    public void setSaveValueVLI(int saveValueVLI) {
        this.saveValueVLI = saveValueVLI;
    }

    public int getSaveValue() {
        return saveValue;
    }

    public void setSaveValue(int saveValue) {
        this.saveValue = saveValue;
    }

    public int getPrimaryPurchaseRequirementVLI() {
        return primaryPurchaseRequirementVLI;
    }

    public void setPrimaryPurchaseRequirementVLI(int primaryPurchaseRequirementVLI) {
        this.primaryPurchaseRequirementVLI = primaryPurchaseRequirementVLI;
    }

    public int getPrimaryPurchaseRequirement() {
        return primaryPurchaseRequirement;
    }

    public void setPrimaryPurchaseRequirement(int primaryPurchaseRequirement) {
        this.primaryPurchaseRequirement = primaryPurchaseRequirement;
    }

    public String getPrimaryPurchaseRequirementCode() {
        return primaryPurchaseRequirementCode;
    }

    public void setPrimaryPurchaseRequirementCode(String primaryPurchaseRequirementCode) {
        this.primaryPurchaseRequirementCode = primaryPurchaseRequirementCode;
    }

    public String getPrimaryPurchaseFamilyCode() {
        return primaryPurchaseFamilyCode;
    }

    public void setPrimaryPurchaseFamilyCode(String primaryPurchaseFamilyCode) {
        this.primaryPurchaseFamilyCode = primaryPurchaseFamilyCode;
    }

    public String getSecondaryAdditionalPurchaseRulesCode() {
        return secondaryAdditionalPurchaseRulesCode;
    }

    public void setSecondaryAdditionalPurchaseRulesCode(String secondaryAdditionalPurchaseRulesCode) {
        this.secondaryAdditionalPurchaseRulesCode = secondaryAdditionalPurchaseRulesCode;
    }

    public int getSecondaryPurchaseRequirementVLI() {
        return secondaryPurchaseRequirementVLI;
    }

    public void setSecondaryPurchaseRequirementVLI(int secondaryPurchaseRequirementVLI) {
        this.secondaryPurchaseRequirementVLI = secondaryPurchaseRequirementVLI;
    }

    public int getSecondaryPurchaseRequirement() {
        return secondaryPurchaseRequirement;
    }

    public void setSecondaryPurchaseRequirement(int secondaryPurchaseRequirement) {
        this.secondaryPurchaseRequirement = secondaryPurchaseRequirement;
    }

    public String getSecondaryPurchaseRequirementCode() {
        return secondaryPurchaseRequirementCode;
    }

    public void setSecondaryPurchaseRequirementCode(String secondaryPurchaseRequirementCode) {
        this.secondaryPurchaseRequirementCode = secondaryPurchaseRequirementCode;
    }

    public String getSecondaryPurchaseFamilyCode() {
        return secondaryPurchaseFamilyCode;
    }

    public void setSecondaryPurchaseFamilyCode(String secondaryPurchaseFamilyCode) {
        this.secondaryPurchaseFamilyCode = secondaryPurchaseFamilyCode;
    }

    public int getSecondaryPurchaseCompanyPrefixVLI() {
        return secondaryPurchaseCompanyPrefixVLI;
    }

    public void setSecondaryPurchaseCompanyPrefixVLI(int secondaryPurchaseCompanyPrefixVLI) {
        this.secondaryPurchaseCompanyPrefixVLI = secondaryPurchaseCompanyPrefixVLI;
    }

    public String getSecondaryPurchaseCompanyPrefix() {
        return secondaryPurchaseCompanyPrefix;
    }

    public void setSecondaryPurchaseCompanyPrefix(String secondaryPurchaseCompanyPrefix) {
        this.secondaryPurchaseCompanyPrefix = secondaryPurchaseCompanyPrefix;
    }

    public int getTertiaryPurchaseRequirementVLI() {
        return tertiaryPurchaseRequirementVLI;
    }

    public void setTertiaryPurchaseRequirementVLI(int tertiaryPurchaseRequirementVLI) {
        this.tertiaryPurchaseRequirementVLI = tertiaryPurchaseRequirementVLI;
    }

    public int getTertiaryPurchaseRequirement() {
        return tertiaryPurchaseRequirement;
    }

    public void setTertiaryPurchaseRequirement(int tertiaryPurchaseRequirement) {
        this.tertiaryPurchaseRequirement = tertiaryPurchaseRequirement;
    }

    public String getTertiaryPurchaseRequirementCode() {
        return tertiaryPurchaseRequirementCode;
    }

    public void setTertiaryPurchaseRequirementCode(String tertiaryPurchaseRequirementCode) {
        this.tertiaryPurchaseRequirementCode = tertiaryPurchaseRequirementCode;
    }

    public String getTertiaryPurchaseFamilyCode() {
        return tertiaryPurchaseFamilyCode;
    }

    public void setTertiaryPurchaseFamilyCode(String tertiaryPurchaseFamilyCode) {
        this.tertiaryPurchaseFamilyCode = tertiaryPurchaseFamilyCode;
    }

    public int getTertiaryPurchaseCompanyPrefixVLI() {
        return tertiaryPurchaseCompanyPrefixVLI;
    }

    public void setTertiaryPurchaseCompanyPrefixVLI(int tertiaryPurchaseCompanyPrefixVLI) {
        this.tertiaryPurchaseCompanyPrefixVLI = tertiaryPurchaseCompanyPrefixVLI;
    }

    public String getTertiaryPurchaseCompanyPrefix() {
        return tertiaryPurchaseCompanyPrefix;
    }

    public void setTertiaryPurchaseCompanyPrefix(String tertiaryPurchaseCompanyPrefix) {
        this.tertiaryPurchaseCompanyPrefix = tertiaryPurchaseCompanyPrefix;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getSerialNumberVLI() {
        return serialNumberVLI;
    }

    public void setSerialNumberVLI(int serialNumberVLI) {
        this.serialNumberVLI = serialNumberVLI;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getRetailerCompanyPrefixVLI() {
        return retailerCompanyPrefixVLI;
    }

    public void setRetailerCompanyPrefixVLI(int retailerCompanyPrefixVLI) {
        this.retailerCompanyPrefixVLI = retailerCompanyPrefixVLI;
    }

    public String getRetailerCompanyPrefixOrGLN() {
        return retailerCompanyPrefixOrGLN;
    }

    public void setRetailerCompanyPrefixOrGLN(String retailerCompanyPrefixOrGLN) {
        this.retailerCompanyPrefixOrGLN = retailerCompanyPrefixOrGLN;
    }

    public String getSaveValueCode() {
        return saveValueCode;
    }

    public void setSaveValueCode(String saveValueCode) {
        this.saveValueCode = saveValueCode;
    }

    public String getSaveValueAppliesToWhichItem() {
        return saveValueAppliesToWhichItem;
    }

    public void setSaveValueAppliesToWhichItem(String saveValueAppliesToWhichItem) {
        this.saveValueAppliesToWhichItem = saveValueAppliesToWhichItem;
    }

    public String getStoreCouponFlag() {
        return storeCouponFlag;
    }

    public void setStoreCouponFlag(String storeCouponFlag) {
        this.storeCouponFlag = storeCouponFlag;
    }

    public String getDoNotMultiplyFlag() {
        return doNotMultiplyFlag;
    }

    public void setDoNotMultiplyFlag(String doNotMultiplyFlag) {
        this.doNotMultiplyFlag = doNotMultiplyFlag;
    }

    @Override
    public String toString() {
        return "GS1Coupon [applicationIdentifer=" + applicationIdentifer + ", primaryCompanyPrefix=" + primaryCompanyPrefix + ", offerCode="
                + offerCode + ", saveValue=" + saveValue + ", primaryPurchaseRequirement=" + primaryPurchaseRequirement
                + ", primaryPurchaseRequirementCode=" + primaryPurchaseRequirementCode + ", primaryPurchaseFamilyCode="
                + primaryPurchaseFamilyCode + ", secondaryAdditionalPurchaseRulesCode=" + secondaryAdditionalPurchaseRulesCode
                + ", secondaryPurchaseRequirement=" + secondaryPurchaseRequirement + ", secondaryPurchaseRequirementCode="
                + secondaryPurchaseRequirementCode + ", secondaryPurchaseFamilyCode=" + secondaryPurchaseFamilyCode
                + ", secondaryPurchaseCompanyPrefix=" + secondaryPurchaseCompanyPrefix + ", tertiaryPurchaseRequirement="
                + tertiaryPurchaseRequirement + ", tertiaryPurchaseRequirementCode=" + tertiaryPurchaseRequirementCode
                + ", tertiaryPurchaseFamilyCode=" + tertiaryPurchaseFamilyCode + ", tertiaryPurchaseCompanyPrefix="
                + tertiaryPurchaseCompanyPrefix + ", expirationDate=" + expirationDate + ", startDate=" + startDate + ", serialNumber="
                + serialNumber + ", retailerCompanyPrefixOrGLN=" + retailerCompanyPrefixOrGLN + ", saveValueCode=" + saveValueCode
                + ", saveValueAppliesToWhichItem=" + saveValueAppliesToWhichItem + ", storeCouponFlag=" + storeCouponFlag
                + ", doNotMultiplyFlag=" + doNotMultiplyFlag + "]";
    }

}
