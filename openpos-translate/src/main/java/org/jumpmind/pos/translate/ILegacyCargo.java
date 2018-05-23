package org.jumpmind.pos.translate;

public interface ILegacyCargo {
    
    public <T> T getCargo();
    
    public String getOperatorFirstLastName();
    
    public String getOperatorLoginId();
    
    /**
     * Returns the RetailTransaction if the cargo has it, otherwise returns null.
     * @return
     */
    public ILegacyRetailTransaction getRetailTransaction();

}
