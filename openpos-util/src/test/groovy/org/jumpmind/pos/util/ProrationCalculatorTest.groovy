package org.jumpmind.pos.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProrationCalculatorTest {

	MoneyCalculator moneyCalculator = new MoneyCalculator("USD");
    ProrationCalculator prorationCalculator = new ProrationCalculator("USD");

    @Test
    public void testProrate() {
        BigDecimal amountToBeProrated = new BigDecimal(57.34);
        List<BigDecimal>existingAmounts = new ArrayList<BigDecimal>();
        existingAmounts.add(new BigDecimal(10.11));
        existingAmounts.add(new BigDecimal(121.34));
        existingAmounts.add(new BigDecimal(37.67));                
        List<BigDecimal> proratedAmounts = prorationCalculator.prorate(amountToBeProrated, existingAmounts);
        assertEquals(moneyCalculator.amount(new BigDecimal(3.43)),proratedAmounts.get(0));        		
        assertEquals(moneyCalculator.amount(new BigDecimal(41.14)),proratedAmounts.get(1));        		
        assertEquals(moneyCalculator.amount(new BigDecimal(12.77)),proratedAmounts.get(2));        		
    }

    @Test
    public void testProratePrecision() {
        BigDecimal amountToBeProrated = new BigDecimal(259.90);
        List<BigDecimal>existingAmounts = new ArrayList<BigDecimal>();
        existingAmounts.add(new BigDecimal(59.95));
        existingAmounts.add(new BigDecimal(199.95));
        List<BigDecimal> proratedAmounts = prorationCalculator.prorate(amountToBeProrated, existingAmounts);
        assertEquals(moneyCalculator.amount(new BigDecimal(59.95)),proratedAmounts.get(0));
        assertEquals(moneyCalculator.amount(new BigDecimal(199.95)),proratedAmounts.get(1));
    }
    
    @Test
    public void testProrateWithRemainder() {
        BigDecimal amountToBeProrated = new BigDecimal(1);
        List<BigDecimal>existingAmounts = new ArrayList<BigDecimal>();
        existingAmounts.add(new BigDecimal(1));
        existingAmounts.add(new BigDecimal(1));
        existingAmounts.add(new BigDecimal(1));                
        List<BigDecimal> proratedAmounts = prorationCalculator.prorate(amountToBeProrated, existingAmounts);
        assertEquals(moneyCalculator.amount(new BigDecimal(0.33)),proratedAmounts.get(0));        		
        assertEquals(moneyCalculator.amount(new BigDecimal(0.33)),proratedAmounts.get(1));        		
        assertEquals(moneyCalculator.amount(new BigDecimal(0.34)),proratedAmounts.get(2));        		
    }
}
