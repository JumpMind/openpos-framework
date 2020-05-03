package org.jumpmind.pos.persist.cars;

import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.Extends;

import java.io.Serializable;

@Getter
@Setter
@Extends(entityClass = CarModel.class)
public class CarModelExtension implements Serializable {

    @ColumnDef
    private boolean trailerHitch;
}
