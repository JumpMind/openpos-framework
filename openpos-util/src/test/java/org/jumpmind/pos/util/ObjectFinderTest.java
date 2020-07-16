package org.jumpmind.pos.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ObjectFinderTest {

    Container parent;

    @Before
    public void setUp() {
        parent = new Container();
        parent.name = "parent";
        parent.amount = null;
        parent.data = new char[]{'a', 'b'};
        ClassSought item1 = new ClassSought("item1");
        parent.item = item1; // 1
        ClassSought child1 = new ClassSought("child1");
        ClassSought child2 = new ClassSought("child2");
        ClassSought child3 = new ClassSought("child3");
        DerivedClassSought derivedChild1 = new DerivedClassSought("derivedChild1", 1);
        ClassSought[] children = new ClassSought[]{
                child1,
                child2,
                child3,
                derivedChild1
        };
        parent.moreItems = children; // 4
        parent.otherItems = new HashSet<>(Arrays.asList(children)); // 4


        Container subContainer1 = new Container();
        subContainer1.name = "subContainer1";
        subContainer1.amount = 1.0;
        ClassSought sub1Item1 = new ClassSought("sub1.item1");
        subContainer1.item = sub1Item1; // 1
        subContainer1.itemMap = new HashMap<>();
        subContainer1.itemMap.put(sub1Item1.name, sub1Item1); // 1
        subContainer1.subContainerMap = new HashMap<>();

        List<Container> subContainers = new ArrayList<>();
        subContainers.add(subContainer1);
        parent.subContainers = subContainers;

        Container subContainer2 = new Container();
        subContainer2.name = "subContainer2";
        ClassSought sub2Item1 = new ClassSought("sub2.item1");
        subContainer2.otherItems = new HashSet<>();
        subContainer2.otherItems.add(new ClassSought("sub2.item2")); // 1
        subContainer2.itemMap = new HashMap<>();
        subContainer2.itemMap.put(sub2Item1.name, sub2Item1); // 1
        subContainer2.moreItems = new ClassSought[]{sub1Item1, child1, child2, derivedChild1}; // 4

        subContainer1.subContainerMap.put(subContainer2.name, subContainer2);
    }

    @Test
    public void testDistinctSearch() {
        ObjectFinder<ClassSought> finder = new ObjectFinder<>(ClassSought.class);
        finder.searchRecursive(parent);
        List<ClassSought> results = finder.getResults();
        assertEquals(8, results.size());
        assertNotNull(results.stream().filter(c -> c.name.equals("item1")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("child1")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("child2")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("child3")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("derivedChild1")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("sub1.item1")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("sub2.item1")).findFirst().orElse(null));
        assertNotNull(results.stream().filter(c -> c.name.equals("sub2.item2")).findFirst().orElse(null));
    }

    @Test
    public void testMultipleSearch() {
        ObjectFinder<ClassSought> finder = new ObjectFinder<>(ClassSought.class, false);
        finder.searchRecursive(parent);
        List<ClassSought> results = finder.getResults();
        assertEquals(17, results.size());
        assertEquals(1, results.stream().filter(c -> c.name.equals("item1")).count());
        assertEquals(3, results.stream().filter(c -> c.name.equals("child1")).count());
        assertEquals(3, results.stream().filter(c -> c.name.equals("child2")).count());
        assertEquals(2, results.stream().filter(c -> c.name.equals("child3")).count());
        assertEquals(3, results.stream().filter(c -> c.name.equals("derivedChild1")).count());
        assertEquals(3, results.stream().filter(c -> c.name.equals("sub1.item1")).count());
        assertEquals(1, results.stream().filter(c -> c.name.equals("sub2.item1")).count());
        assertEquals(1, results.stream().filter(c -> c.name.equals("sub2.item2")).count());
    }

    static class Container {
        String name;
        Double amount;
        char[] data;
        ClassSought item;
        List<Container> subContainers;
        Set<ClassSought> otherItems;
        Map<String, ClassSought> itemMap;
        Map<String, Container> subContainerMap;
        ClassSought[] moreItems;
    }

    static class ClassSought {
        String name;
        ClassSought(String name) {
            this.name = name;
        }
    }

    static class DerivedClassSought extends ClassSought {
        int size;
        DerivedClassSought(String name, int size) {
            super(name);
            this.size = size;
        }
    }

}