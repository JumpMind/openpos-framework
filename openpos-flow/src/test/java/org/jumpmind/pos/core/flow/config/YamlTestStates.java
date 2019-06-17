package org.jumpmind.pos.core.flow.config;

import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.server.model.Action;

public class YamlTestStates {
    
    public static class InitialState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class InlineState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class InlineState2 implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class NestedState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class FirstLevelState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class SimpleState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class InitialState2 implements IState {
        @Override
        public void arrive(Action action) {
        }
    }
    
    public static class SubstateClassTestState implements IState {
        @Override
        public void arrive(Action action) {
        }
    }

}
