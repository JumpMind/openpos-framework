package org.jumpmind.pos.core.flow;

import static org.junit.Assert.*;

import org.jumpmind.pos.server.model.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)

public class BeforeActionStateLifecycleServiceTest {
    @Mock
    private IStateManager stateManager;

    private BeforeActionStateLifecycleService beforeActionService;
    
    @Before
    public void setUp() throws Exception {
        beforeActionService = new BeforeActionStateLifecycleService();
        
    }

    @Test
    public void testClassWithOneBeforeActionMethod() {
        ClassWithOneBeforeActionMethod obj = new ClassWithOneBeforeActionMethod();
        assertFalse(obj.beforeActionInvoked);
        beforeActionService.executeBeforeActionMethods(stateManager, obj, new Action("Action1"));
        assertTrue(obj.beforeActionInvoked);
    }
    
    @Test
    public void testClassWithMultipleBeforeActionMethods() {
        ClassWithMultipleBeforeActionMethods obj = new ClassWithMultipleBeforeActionMethods();
        assertFalse(obj.beforeActionXInvoked);
        assertFalse(obj.beforeActionYInvoked);
        assertFalse(obj.beforeActionZInvoked);
        beforeActionService.executeBeforeActionMethods(stateManager, obj, new Action(ClassWithMultipleBeforeActionMethods.ACTION_NAME));
        assertTrue(obj.beforeActionXInvoked);
        assertTrue(obj.beforeActionYInvoked);
        assertTrue(obj.beforeActionZInvoked);
    }

    @Test
    public void testClassWithMultipleBeforeActionMethodsAndOrdering() {
        ClassWithMultipleBeforeActionMethodsAndOrdering obj = new ClassWithMultipleBeforeActionMethodsAndOrdering();
        assertFalse(obj.beforeActionXInvoked);
        assertFalse(obj.beforeActionYInvoked);
        assertFalse(obj.beforeActionZInvoked);
        // ClassWithMultipleBeforeActionMethodsAndOrdering will check that methods are run in correct order
        beforeActionService.executeBeforeActionMethods(stateManager, obj, new Action(ClassWithMultipleBeforeActionMethodsAndOrdering.ACTION_NAME));
        assertTrue(obj.beforeActionXInvoked);
        assertTrue(obj.beforeActionYInvoked);
        assertTrue(obj.beforeActionZInvoked);
    }

    @Test
    public void testClassWithMultipleBeforeActionMethodsAndFailOnExceptionTrue() {
        ClassWithMultipleBeforeActionMethodsAndFailOnExceptionTrue obj = new ClassWithMultipleBeforeActionMethodsAndFailOnExceptionTrue();
        assertFalse(obj.beforeActionXInvoked);
        assertFalse(obj.beforeActionYInvoked);
        assertFalse(obj.beforeActionZInvoked);
        try {
            beforeActionService.executeBeforeActionMethods(stateManager, obj, new Action(ClassWithMultipleBeforeActionMethodsAndFailOnExceptionTrue.ACTION_NAME));
        } catch (FlowException ex) {
            assertTrue(obj.beforeActionYInvoked);
            assertFalse(obj.beforeActionXInvoked);
            assertFalse(obj.beforeActionZInvoked);
            return;
        }
        fail("FlowException should have been raised");
    }

    @Test
    public void testClassWithMultipleBeforeActionMethodsAndFailOnExceptionFalse() {
        ClassWithMultipleBeforeActionMethodsAndFailOnExceptionFalse obj = new ClassWithMultipleBeforeActionMethodsAndFailOnExceptionFalse();
        assertFalse(obj.beforeActionXInvoked);
        assertFalse(obj.beforeActionYInvoked);
        assertFalse(obj.beforeActionZInvoked);
        try {
            beforeActionService.executeBeforeActionMethods(stateManager, obj, new Action(ClassWithMultipleBeforeActionMethodsAndFailOnExceptionFalse.ACTION_NAME));
        } catch (FlowException ex) {
            fail("FlowException should NOT have been raised");
        }
        
        assertFalse(obj.beforeActionYInvoked);
        assertTrue(obj.beforeActionXInvoked);
        assertTrue(obj.beforeActionZInvoked);
    }
    
    public static class ClassWithOneBeforeActionMethod {
        
        boolean beforeActionInvoked = false;
        
        @BeforeAction
        public void beforeActionMethod() {
            assertFalse(beforeActionInvoked);
            beforeActionInvoked = true;
        }
    }

    public static class ClassWithMultipleBeforeActionMethods {
        public static final String ACTION_NAME = "Action1";
        
        boolean beforeActionXInvoked = false;
        boolean beforeActionYInvoked = false;
        boolean beforeActionZInvoked = false;
        
        @BeforeAction
        public void beforeActionMethodX(Action action) {
            assertEquals(ACTION_NAME, action.getName());
            assertFalse(beforeActionXInvoked);
            beforeActionXInvoked = true;
        }
        
        @BeforeAction
        public void beforeActionMethodY(Action action) {
            assertEquals(ACTION_NAME, action.getName());
            assertFalse(beforeActionYInvoked);
            beforeActionYInvoked = true;
        }

        @BeforeAction
        public void beforeActionMethodZ(Action action) {
            assertEquals(ACTION_NAME, action.getName());
            assertFalse(beforeActionZInvoked);
            beforeActionZInvoked = true;
        }
        
    }
    
    public static class ClassWithMultipleBeforeActionMethodsAndOrdering extends ClassWithMultipleBeforeActionMethods {
        @BeforeAction(order=30)
        @Override
        public void beforeActionMethodX(Action action) {
            assertTrue(beforeActionZInvoked);
            assertTrue(beforeActionYInvoked);
            assertFalse(beforeActionXInvoked);
            super.beforeActionMethodX(action);
            assertTrue(beforeActionXInvoked);
        }
        
        @BeforeAction(order=10)
        @Override
        public void beforeActionMethodY(Action action) {
            assertFalse(beforeActionXInvoked);
            assertFalse(beforeActionZInvoked);
            super.beforeActionMethodY(action);
        }

        @BeforeAction(order=20)
        @Override
        public void beforeActionMethodZ(Action action) {
            assertTrue(beforeActionYInvoked);
            assertFalse(beforeActionXInvoked);
            super.beforeActionMethodZ(action);
        }
    }
    
    public static class ClassWithMultipleBeforeActionMethodsAndFailOnExceptionTrue extends ClassWithMultipleBeforeActionMethodsAndOrdering {
        @BeforeAction(order=20)
        @Override
        public void beforeActionMethodZ(Action action) {
            throw new RuntimeException("Throwing Exception from second BeforeAction executed");
        }
    
    }

    public static class ClassWithMultipleBeforeActionMethodsAndFailOnExceptionFalse extends ClassWithMultipleBeforeActionMethodsAndOrdering {
        @BeforeAction(order=10, failOnException=false)
        @Override
        public void beforeActionMethodY(Action action) {
            assertFalse(beforeActionYInvoked);
            assertFalse(beforeActionXInvoked);
            assertFalse(beforeActionZInvoked);
            throw new RuntimeException("Throwing Exception from first BeforeAction executed");
        }
        
        @BeforeAction(order=20)
        @Override
        public void beforeActionMethodZ(Action action) {
            assertFalse(beforeActionYInvoked);
            assertFalse(beforeActionXInvoked);
            assertFalse(beforeActionZInvoked);
            beforeActionZInvoked=true;
        }
        
        @BeforeAction(order=30)
        @Override
        public void beforeActionMethodX(Action action) {
            assertTrue(beforeActionZInvoked);
            assertFalse(beforeActionYInvoked);
            assertFalse(beforeActionXInvoked);
            beforeActionXInvoked=true;
        }
    
    }
    
}
