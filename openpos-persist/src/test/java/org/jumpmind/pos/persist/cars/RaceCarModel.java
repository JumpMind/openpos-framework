package org.jumpmind.pos.persist.cars;

import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="race_car")
public class RaceCarModel extends CarModel {

    private static final long serialVersionUID = 1L;
    
    @ColumnDef
    private boolean turboCharged;
    
    public void setTurboCharged(boolean turboCharged) {
        this.turboCharged = turboCharged;
    }
    
    public boolean isTurboCharged() {
        return turboCharged;
    }

}
