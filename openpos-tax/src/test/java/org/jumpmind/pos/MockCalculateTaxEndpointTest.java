package org.jumpmind.pos;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.jumpmind.pos.tax.model.TaxAmount;
import org.jumpmind.pos.tax.model.TaxCalculationRequest;
import org.jumpmind.pos.tax.model.TaxCalculationResponse;
import org.jumpmind.pos.tax.model.TaxableItem;
import org.jumpmind.pos.tax.service.CalculateTaxEndpoint;
import org.junit.Before;
import org.junit.Test;

public class MockCalculateTaxEndpointTest {
    CalculateTaxEndpoint calculateTaxEndpoint;

    MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

    @Before
    public void setup() {
        calculateTaxEndpoint = new MockCalculateTaxEndpoint();
    }

    @Test
    public void testGroupPercentTax() {
        TaxCalculationRequest request = new TaxCalculationRequest();
        request.addTaxableItem(getTaxableItem("100", 0.99));
        request.addTaxableItem(getTaxableItem("100", 1.99));
        request.addTaxableItem(getTaxableItem("101", 6.99));
        request.addTaxableItem(getTaxableItem("101", 3.99));
        request.addTaxableItem(getTaxableItem("102", 4.99));
        request.addTaxableItem(getTaxableItem("102", 5.99));
        TaxCalculationResponse response = calculateTaxEndpoint.calculateTax(request);
        assertTax(response, "1", "100", 0.16, 5.25);
        assertTax(response, "1", "101", 0.58, 5.25);
        assertTax(response, "1", "101", 0.58, 5.25);
    }

    @Test
    public void testTransactionTableTax() {
        TaxCalculationRequest request = new TaxCalculationRequest();
        request.addTaxableItem(getTaxableItem("500", 4.99));
        request.addTaxableItem(getTaxableItem("500", 2.99));
        TaxCalculationResponse response = calculateTaxEndpoint.calculateTax(request);
        assertTax(response, "1", "500", 0.48);
    }

    @Test
    public void testTransactionTableCycleTax() {
        TaxCalculationRequest request = new TaxCalculationRequest();
        request.addTaxableItem(getTaxableItem("600", 1.71));
        TaxCalculationResponse response = calculateTaxEndpoint.calculateTax(request);
        assertTax(response, "1", "600", 0.11);

        request = new TaxCalculationRequest();
        request.addTaxableItem(getTaxableItem("600", 85.95));
        response = calculateTaxEndpoint.calculateTax(request);
        assertTax(response, "1", "600", 5.16);
    }

//    /* TODO: Have JJ Implement this test in addition to testing using real database 
//     *       Model off of other @Test methods.  These were old test methods that have 
//     *       been converted to a new API (RetailTransaction -> TaxCalculationRequest etc.)
//     * 
    @Test
    public void testCompoundingTax() {
        TaxCalculationRequest request = new TaxCalculationRequest();
        request.addTaxableItem(getTaxableItem("450", 1.04));
        TaxCalculationResponse response = calculateTaxEndpoint.calculateTax(request);
        assertTax(response, "1", "450", 0.01, 1.25);
        assertTax(response, "3", "450", 0.06, 5.25);
    }
    

    private TaxableItem getTaxableItem(String groupId, double amount) {
        TaxableItem taxableItem = new TaxableItem();
        taxableItem.setGroupId(groupId);
        taxableItem.setExtendedAmount(new BigDecimal(amount));
        return taxableItem;
    }

    private void assertTax(TaxCalculationResponse tran, String authorityId, String groupId, double expectedAmount) {
        TaxAmount taxAmount = tran.getTaxAmount(authorityId, groupId);
        assertNotNull("Expected tax for tax group " + authorityId + "-" + groupId, taxAmount);
        assertTrue("Expected tax of " + expectedAmount + " instead of " + taxAmount.getTaxAmount(),
                taxAmount.getTaxAmount().compareTo(new BigDecimal(expectedAmount, mc)) == 0);
    }

    private void assertTax(TaxCalculationResponse tran, String authorityId, String groupId, double expectedAmount, double expectedPercent) {
        assertTax(tran, authorityId, groupId, expectedAmount);
        TaxAmount taxAmount = tran.getTaxAmount(authorityId, groupId);
        assertTrue("Expected percent of " + expectedPercent + " instead of " + taxAmount.getTaxPercent(),
                taxAmount.getTaxPercent().compareTo(new BigDecimal(expectedPercent, mc)) == 0);
    }

}
