package org.jumpmind.pos.translate;

public interface IActionTranslator extends ITranslator {
    
    public void translate(ITranslationManager manager, ITranslationManagerSubscriber subscriber);

}
