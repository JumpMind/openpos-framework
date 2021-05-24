package org.jumpmind.pos.service.repository;

import java.util.Collection;

public interface ServiceRepository<KEY, SERVICE> {

    boolean hasService(KEY key);

    SERVICE getService(KEY key);

    Collection<SERVICE> getAllServices();
}
