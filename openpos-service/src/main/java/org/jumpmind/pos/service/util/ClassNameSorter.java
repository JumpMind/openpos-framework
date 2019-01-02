package org.jumpmind.pos.service.util;

import java.util.Comparator;

public class ClassNameSorter implements Comparator<Class<?>> {

    @Override
    public int compare(Class<?> o1, Class<?> o2) {        
        return o1.getSimpleName().compareTo(o2.getSimpleName());
    }
}
