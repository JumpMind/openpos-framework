package org.jumpmind.pos.server.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class ActionTest {

    @Test
    public void testCausedBy() throws Exception {
        Action levelOne = new Action("one");
        Action levelTwo = new Action("two");
        levelOne.setCausedBy(levelTwo);
        Action levelThree = new Action("three");
        levelTwo.setCausedBy(levelThree);
        assertTrue(levelOne.causedBy("three"));
    }
}
