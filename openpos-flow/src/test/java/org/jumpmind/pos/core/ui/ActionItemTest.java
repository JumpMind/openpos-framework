package org.jumpmind.pos.core.ui;

import org.junit.Test;

import static org.junit.Assert.*;

public class ActionItemTest {

    @Test
    public void noArgsConstructorDefaultValues() {
        ActionItem item = new ActionItem();
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
    }

    @Test
    public void actionOnlyConstructorValues() {
        ActionItem item = new ActionItem("Action");
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
    }

    @Test
    public void actionTitleConstructorValues() {
        ActionItem item = new ActionItem("Action", "Title");
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
    }

    @Test
    public void actionTitleIconConstructorValues() {
        ActionItem item = new ActionItem("Action", "Title", "Icon");
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
    }

    @Test
    public void autoAssignActionTitleIconConstructorValues() {
        ActionItem item = new ActionItem(false, "Action", "Title", "Icon");
        assertTrue(item.isEnabled());
        assertFalse(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
    }

    @Test
    public void actionTitleIconConfirmationMessageConstructorValues() {
        ActionItem item = new ActionItem("Action", "Title", "Icon", "ConfirmationMessage");
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
        assertEquals("Title", item.getConfirmationDialog().getTitle());
        assertEquals("ConfirmationMessage", item.getConfirmationDialog().getMessage());
    }

    @Test
    public void actionTitleIconNullConfirmationMessageConstructorValues() {
        ActionItem item = new ActionItem("Action", "Title", "Icon", (String) null);
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
        assertNull(item.getConfirmationDialog());
    }

    @Test
    public void actionTitleIconConfirmationDialogConstructorValues() {
        ConfirmationDialog dialog = new ConfirmationDialog("ConfirmationMessage", "ConfirmationTitle");
        ActionItem item = new ActionItem("Action", "Title", "Icon", dialog);
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
        assertEquals(dialog, item.getConfirmationDialog());
    }

    @Test
    public void titleActionEnabledConstructorValues() {
        ActionItem item = new ActionItem("Title", "Action", false);
        assertFalse(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
    }

    @Test
    public void titleActionIconEnabledConstructorValues() {
        ActionItem item = new ActionItem("Title", "Action", "Icon", false);
        assertFalse(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertEquals("Icon", item.getIcon());
    }

    @Test
    public void titleActionEnabledSensitiveConstructorValues() {
        ActionItem item = new ActionItem("Title", "Action", false, true);
        assertFalse(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
        assertTrue(item.isSensitive());
    }

    @Test
    public void builderDefaultValues() {
        ActionItem item = ActionItem.builder().action("Action").title("Title").build();
        assertTrue(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
    }

    @Test
    public void builderEnabledFalseValues() {
        ActionItem item = ActionItem.builder().action("Action").title("Title").enabled(false).build();
        assertFalse(item.isEnabled());
        assertTrue(item.isAutoAssignEnabled());
        assertEquals("Action", item.getAction());
        assertEquals("Title", item.getTitle());
    }
}
