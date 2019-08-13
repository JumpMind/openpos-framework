package org.jumpmind.pos.core.clientconfiguration;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultClientConfigSelectorTest {

    @Test
    public void testUniqueTagGroupCombinationsWith3Tags() {
        List<String> tags = Arrays.asList("default", "desktop", "training-mode");
        List<List<String>> tagGroups = DefaultClientConfigSelector.uniqueTagGroupCombinations(tags);
        assertEquals(7, tagGroups.size());
        List<String> groups = convertListsToStrings(tagGroups);
        assertEquals("default", groups.get(0));
        assertEquals("default, desktop", groups.get(1));
        assertEquals("desktop", groups.get(2));
        assertEquals("default, training-mode", groups.get(3));
        assertEquals("default, desktop, training-mode", groups.get(4));
        assertEquals("desktop, training-mode", groups.get(5));
        assertEquals("training-mode", groups.get(6));
    }

    @Test
    public void testUniqueTagGroupCombinationsWith4Tags() {
        List<String> tags = Arrays.asList("a", "b", "c", "d");
        List<List<String>> tagGroups = DefaultClientConfigSelector.uniqueTagGroupCombinations(tags);
        assertEquals(15, tagGroups.size());
        List<String> groups = convertListsToStrings(tagGroups);
        assertEquals("a", groups.get(0));
        assertEquals("a, b", groups.get(1));
        assertEquals("b", groups.get(2));
        assertEquals("a, c", groups.get(3));
        assertEquals("a, b, c", groups.get(4));
        assertEquals("b, c", groups.get(5));
        assertEquals("c", groups.get(6));
        assertEquals("a, d", groups.get(7));
        assertEquals("a, b, d", groups.get(8));
        assertEquals("b, d", groups.get(9));
        assertEquals("a, c, d", groups.get(10));
        assertEquals("a, b, c, d", groups.get(11));
        assertEquals("b, c, d", groups.get(12));
        assertEquals("c, d", groups.get(13));
        assertEquals("d", groups.get(14));
    }

    private List<String> convertListsToStrings(List<List<String>> tagGroups) {
        List<String> groups = new ArrayList<>();
        for(List<String> group: tagGroups) {
            String tagGroup = "";
            for(int i = 0; i<group.size(); i++) {
                if(i == 0) {
                    tagGroup = tagGroup.concat(group.get(i));
                } else {
                    tagGroup = tagGroup.concat(", " + group.get(i));
                }
            }
            groups.add(tagGroup);
        }
        return groups;
    }
}
