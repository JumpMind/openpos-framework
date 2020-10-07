package org.jumpmind.pos.persist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableDef(name="script_version", description="This table is used to track runtime information about scripts that have been applied",
        primaryKey={"installationId","fileName"})
public class ScriptVersionModel extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @ColumnDef
    String installationId;

    @ColumnDef
    String fileName;

    @ColumnDef
    String checkSum;

}
