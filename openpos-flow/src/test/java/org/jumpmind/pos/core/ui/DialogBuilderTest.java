package org.jumpmind.pos.core.ui;

import static org.junit.Assert.*;

import org.jumpmind.pos.core.ui.message.DialogUIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DialogBuilderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Ensure that if the dialog type is set to OK_CANCEL_TYPE, and no customizations
     * are made, that the dialog generated just has Ok and Cancel buttons with 
     * default titles (Ok, Cancel) and actions (Ok, Cancel).
     */
    @Test
    public void testDefaultOkCancelDialogBuild() {
        DialogBuilder config = new DialogBuilder(DialogBuilder.OK_CANCEL_TYPE, "Message Line 1");
        DialogUIMessage screen = config.build();
        
        assertEquals(2, screen.getButtons().size());
        assertEquals(1, screen.getButtons().stream().filter(
            m -> DialogBuilder.CANCEL_BUTTON_KEY.equals(m.getTitle()) && DialogBuilder.CANCEL_BUTTON_KEY.equals(m.getAction())).count()
        );
        assertEquals(1, screen.getButtons().stream().filter(
                m -> DialogBuilder.OK_BUTTON_KEY.equals(m.getTitle()) && DialogBuilder.OK_BUTTON_KEY.equals(m.getAction())).count()
        );
        DialogHeaderPart headerPart = (DialogHeaderPart)screen.get(MessagePartConstants.DialogHeader);
        assertNull(headerPart.getHeaderText());
        assertEquals(1, screen.getMessage().size());
        assertEquals("Message Line 1", screen.getMessage().get(0));
    }

    /**
     * Ensure that if the dialog type is set to OK_TYPE, and no customizations
     * are made, that the dialog generated just has Ok button with 
     * default title (Ok) and actions (Ok).
     */
    @Test
    public void testDefaultOkDialogBuild() {
        DialogBuilder config = new DialogBuilder(DialogBuilder.OK_TYPE, "Message Line 1", "Message Line 2");
        DialogUIMessage screen = config.build();
        
        assertEquals(1, screen.getButtons().size());
        assertEquals(1, screen.getButtons().stream().filter(
                m -> DialogBuilder.OK_BUTTON_KEY.equals(m.getTitle()) && DialogBuilder.OK_BUTTON_KEY.equals(m.getAction())).count()
        );

        DialogHeaderPart headerPart = (DialogHeaderPart)screen.get(MessagePartConstants.DialogHeader);
        assertNull(headerPart.getHeaderText());
        assertEquals(2, screen.getMessage().size());
        assertEquals("Message Line 1", screen.getMessage().get(0));
        assertEquals("Message Line 2", screen.getMessage().get(1));
    }
    
    /**
     * Ensure that if the dialog type is set to OK_CANCEL_TYPE, and the Ok button is customized
     * with a custom action name, that the dialog generated has the custom
     * action name on the Ok button.
     */
    @Test
    public void testCustomOkButton_ForOkCancelDialogBuild() {
        DialogBuilder config = new DialogBuilder(DialogBuilder.OK_CANCEL_TYPE, "Message Line 1")
                .title("My title").putAction(DialogBuilder.OK_BUTTON_KEY, "customOkAction");
        DialogUIMessage screen = config.build();
        
        assertEquals(2, screen.getButtons().size());
        assertEquals(1, screen.getButtons().stream().filter(
            m -> DialogBuilder.CANCEL_BUTTON_KEY.equals(m.getTitle()) && DialogBuilder.CANCEL_BUTTON_KEY.equals(m.getAction())).count()
        );
        assertEquals(1, screen.getButtons().stream().filter(
                m -> DialogBuilder.OK_BUTTON_KEY.equals(m.getTitle()) && "customOkAction".equals(m.getAction())).count()
        );
        DialogHeaderPart headerPart = (DialogHeaderPart)screen.get(MessagePartConstants.DialogHeader);
        assertEquals("My title", headerPart.getHeaderText());
        assertEquals(1, screen.getMessage().size());
        assertEquals("Message Line 1", screen.getMessage().get(0));
        
    }
    
    /**
     * Ensure that if the dialog type is set to OK_CANCEL_TYPE, and the Ok button is customized
     * with a custom action title and action, that the ok button in the  dialog generated has
     * both custom title and action.
     */
    @Test
    public void testCustomButtonTitle() {
        DialogBuilder config = new DialogBuilder(DialogBuilder.OK_CANCEL_TYPE, "Message Line 1")
                .title("My title").putAction(DialogBuilder.OK_BUTTON_KEY, "Yes", "customOkAction");
        DialogUIMessage screen = config.build();

        assertEquals(1, screen.getButtons().stream().filter(
                m -> "Yes".equals(m.getTitle()) && "customOkAction".equals(m.getAction())).count()
        );

    }
    
}
