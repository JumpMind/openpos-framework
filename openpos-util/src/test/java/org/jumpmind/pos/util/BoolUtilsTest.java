package org.jumpmind.pos.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoolUtilsTest {
    public static final String[] EXTRA_TRUTHYS = {"checked", "enabled"};

    @Test
    public void isTruthyTest() {

        assertFalse(BoolUtils.isTruthy(null));
        assertFalse(BoolUtils.isTruthy(""));

        assertTrue(BoolUtils.isTruthy("y"));
        assertTrue(BoolUtils.isTruthy("yes"));
        assertTrue(BoolUtils.isTruthy("YES"));
        assertTrue(BoolUtils.isTruthy("Yes"));

        assertFalse(BoolUtils.isTruthy("no"));
        assertFalse(BoolUtils.isTruthy("NO"));

        assertTrue(BoolUtils.isTruthy("t"));
        assertTrue(BoolUtils.isTruthy("true"));
        assertTrue(BoolUtils.isTruthy("TRUE"));
        assertTrue(BoolUtils.isTruthy("TrUe"));

        assertFalse(BoolUtils.isTruthy("f"));
        assertFalse(BoolUtils.isTruthy("false"));

        assertTrue(BoolUtils.isTruthy("on"));
        assertTrue(BoolUtils.isTruthy("ON"));
        assertTrue(BoolUtils.isTruthy("On"));

        assertFalse(BoolUtils.isTruthy("off"));
        assertFalse(BoolUtils.isTruthy("Off"));
        assertFalse(BoolUtils.isTruthy("OFF"));

        assertTrue(BoolUtils.isTruthy("1"));
        assertFalse(BoolUtils.isTruthy("0"));
        assertFalse(BoolUtils.isTruthy("2"));
        assertFalse(BoolUtils.isTruthy("-1"));

        assertFalse(BoolUtils.isTruthy("checked"));
        assertFalse(BoolUtils.isTruthy("checked", null));
        assertFalse(BoolUtils.isTruthy("checked", new String[]{}));
        assertTrue(BoolUtils.isTruthy("checked", EXTRA_TRUTHYS));
        assertTrue(BoolUtils.isTruthy("CHECKED", EXTRA_TRUTHYS));
        assertTrue(BoolUtils.isTruthy("Checked", EXTRA_TRUTHYS));

        assertFalse(BoolUtils.isTruthy("enabled"));
        assertTrue(BoolUtils.isTruthy("ENAbled", EXTRA_TRUTHYS));

        assertFalse(BoolUtils.isTruthy(null, null));
    }
}