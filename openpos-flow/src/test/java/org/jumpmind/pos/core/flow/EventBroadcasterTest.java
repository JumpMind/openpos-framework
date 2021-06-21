package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.OnEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class EventBroadcasterTest {
    final IStateManager stateManager = Mockito.mock(IStateManager.class);
    EventBroadcaster eventBroadcaster;

    @Before
    public void setup() {
        Mockito.when(stateManager.getAppId()).thenReturn("pos");
        Mockito.when(stateManager.getDeviceId()).thenReturn("001");
        eventBroadcaster = new EventBroadcaster(stateManager);
        Target.withNoArgs = false;
        Target.onAllEvents = null;
        Target.onEventFromOthers = null;
        Target.onEventFromPairedDevice = null;
    }

    @Test
    public void testFromSelfPassingInObject() {
        Target target = new Target();
        AppEvent appEvent = new AppEvent("001", "pos");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertNull(Target.onEventFromOthers);
        assertNull(Target.onEventFromPairedDevice);
        assertFalse(Target.withNoArgs);
    }

    @Test
    public void testFromOtherPassingInObject() {
        Target target = new Target();
        AppEvent appEvent = new AppEvent("001", "customerDisplay");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertEquals(appEvent, Target.onEventFromOthers);
        assertEquals(appEvent, Target.onEventFromPairedDevice);
        assertTrue(Target.withNoArgs);
    }

    @Test
    public void testFromSelfNotPassingInObject() {
        AppEvent appEvent = new AppEvent("001", "pos");
        eventBroadcaster.postEventToObject(Target.class, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertNull(Target.onEventFromOthers);
        assertNull(Target.onEventFromPairedDevice);
        assertFalse(Target.withNoArgs);
    }

    @Test
    public void testFromSelfToPairedDevice() {
        Mockito.when(stateManager.getPairedDeviceId()).thenReturn("002");

        Target target = new Target();
        AppEvent appEvent = new AppEvent("001", "pos", "002");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertNull(Target.onEventFromOthers);
        assertNull(Target.onEventFromPairedDevice);
        assertFalse(Target.withNoArgs);
    }

    @Test
    public void testFromPairedDeviceToSelf() {
        Mockito.when(stateManager.getDeviceId()).thenReturn("002");

        Target target = new Target();
        AppEvent appEvent = new AppEvent("002", "customerdisplay", "001");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertEquals(appEvent, Target.onEventFromOthers);
        assertEquals(appEvent, Target.onEventFromPairedDevice);
        assertTrue(Target.withNoArgs);
    }

    static class Target {

        static AppEvent onAllEvents;
        static AppEvent onEventFromOthers;
        static AppEvent onEventFromPairedDevice;
        static boolean withNoArgs;

        @OnEvent(receiveEventsFromSelf = true)
        public void onAllEvents(AppEvent event) {
            this.onAllEvents = event;
        }

        @OnEvent(receiveEventsFromSelf = false)
        public void onEventFromOthers(AppEvent event) {
            this.onEventFromOthers = event;
        }

        @OnEvent(receiveEventsFromPairedDevice = true)
        public void onEventFromPairedDevice(AppEvent event) {
            this.onEventFromPairedDevice = event;
        }

        @OnEvent
        public void withNoArgs() {
            withNoArgs = true;
        }
    }
}
