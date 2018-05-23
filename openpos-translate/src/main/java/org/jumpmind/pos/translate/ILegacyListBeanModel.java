package org.jumpmind.pos.translate;

import java.util.List;

public interface ILegacyListBeanModel {
    public void setSelectedValue(Object aValue);
    public void setSelectedRows(int[] selectedRows);
    public void setSelectedRows(List<Integer> selectedRows);
}
