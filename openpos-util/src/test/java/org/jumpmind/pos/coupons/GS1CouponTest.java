package org.jumpmind.pos.coupons;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;

import org.junit.Test;

public class GS1CouponTest {
    
    @Test
    public void testSave50Cents() throws Exception {
        String databar = "8110100722402015652501101003190303";
        GS1Coupon coupon = new GS1Coupon(databar);
        assertEquals(50, coupon.getSaveValue());
        assertEquals(1, coupon.getPrimaryPurchaseRequirement());
        assertEquals(0, coupon.getSecondaryPurchaseRequirement());
        assertEquals(0, coupon.getTertiaryPurchaseRequirement());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-03-03 23:59:59"), coupon.getExpirationDate());
    }
    
    @Test
    public void testSave5dollars() throws Exception {
        String databar = "8110003057303856535001106313190217";
        GS1Coupon coupon = new GS1Coupon(databar);
        assertEquals(500, coupon.getSaveValue());
        assertEquals(1, coupon.getPrimaryPurchaseRequirement());
        assertEquals(0, coupon.getSecondaryPurchaseRequirement());
        assertEquals(0, coupon.getTertiaryPurchaseRequirement());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-02-17 23:59:59"), coupon.getExpirationDate());
        System.out.println(coupon.toString());
        
    }
}
