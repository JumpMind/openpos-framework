package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.OnEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class EventBroadcasterTest {

    EventBroadcaster eventBroadcaster;

    @Before
    public void setup() {
        final IStateManager stateManager = Mockito.mock(IStateManager.class);
        Mockito.when(stateManager.getAppId()).thenReturn("pos");
        Mockito.when(stateManager.getDeviceId()).thenReturn("001");
        eventBroadcaster = new EventBroadcaster(stateManager);
        Target.withNoArgs = false;
        Target.onAllEvents = null;
        Target.onEventFromOthers = null;
    }

    @Test
    public void testFromSelfPassingInObject() {
        Target target = new Target();
        AppEvent appEvent = new AppEvent("001", "pos");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertNull(Target.onEventFromOthers);
        assertFalse(Target.withNoArgs);
    }

    @Test
    public void testFromOtherPassingInObject() {
        Target target = new Target();
        AppEvent appEvent = new AppEvent("001", "customerDisplay");
        eventBroadcaster.postEventToObject(target, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertEquals(appEvent, Target.onEventFromOthers);
        assertTrue(Target.withNoArgs);
    }

    @Test
    public void testFromSelfNotPassingInObject() {
        AppEvent appEvent = new AppEvent("001", "pos");
        eventBroadcaster.postEventToObject(Target.class, appEvent);
        assertEquals(appEvent, Target.onAllEvents);
        assertNull(Target.onEventFromOthers);
        assertFalse(Target.withNoArgs);
    }


    static class Target {

        static AppEvent onAllEvents;
        static AppEvent onEventFromOthers;
        static boolean withNoArgs;

        @OnEvent(receiveEventsFromSelf = true)
        public void onAllEvents(AppEvent event) {
            this.onAllEvents = event;
        }

        @OnEvent(receiveEventsFromSelf = false)
        public void onEventFromOthers(AppEvent event) {
            this.onEventFromOthers = event;
        }

        @OnEvent
        public void withNoArgs() {
            withNoArgs = true;
        }
    }
}
