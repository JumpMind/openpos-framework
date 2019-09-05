/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeforeActionStateLifecycleService implements IBeforeActionService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private IErrorHandler errorHandler;

    @Override
    public void executeBeforeActionMethods(IStateManager stateManager, Object state, Action currentAction) {
        List<Method> beforeActionMethods = this.getBeforeActionMethods(state);
        for (Method meth : beforeActionMethods) {
            this.invokeBeforeActionMethod(stateManager, state, currentAction, meth);
        }
        
    }
    
    protected void invokeBeforeActionMethod(IStateManager stateManager, Object state, Action action, Method method) {
        method.setAccessible(true);
        List<Object> arguments = new ArrayList<Object>();
        boolean failOnException = method.getAnnotation(BeforeAction.class).failOnException();
        try {
            for (Class<?> type : method.getParameterTypes()) {
                if (type.isAssignableFrom(Action.class)) {
                    arguments.add(action);
                } else {
                    arguments.add(null);
                }
            }
            logger.debug("Executing BeforeAction method named '{}' on state '{}'...", method.getName(), state.getClass().getSimpleName());
            method.invoke(state, arguments.toArray(new Object[arguments.size()]));
            logger.debug("Finished executing BeforeAction method named '{}' on state '{}'.", method.getName(), state.getClass().getSimpleName());
        } catch (Exception ex) {
            String msg = String.format("Failed to invoke BeforeAction method named '%s' on state '%s'.  Reason: %s", 
                    method.getName(), state.getClass().getName(), ex.getMessage() != null ? ex.getMessage() : (ex.getCause() != null ? ex.getCause().getMessage() : null));
            if (failOnException) {
                if (errorHandler != null) {
                    errorHandler.handleError(stateManager, new FlowException(msg, ex));
                } else {
                    throw new FlowException(msg, ex);
                }
            } else {
                this.logger.warn(msg, ex);
            }
        }
    }
    
    protected List<Method> getBeforeActionMethods(Object state) {
        Class<?> clazz = state.getClass();

        List<Method> methodsFromTargetClassAndSuper = MethodUtils.getMethodsListWithAnnotation(clazz, BeforeAction.class, true, true);
        List<Method> overriddenSuperMethodsToRemove = new ArrayList<>();

        methodsFromTargetClassAndSuper.forEach(m -> {
            // Get the hierarchy of overridden methods for this method (skipping the first one
            // since it is the top level subclass method).  If any of the
            // overridden methods is in the list of all the annotated methods
            // returned from above, queue that method for exclusion
            Set<Method> overrideHierarchy = MethodUtils.getOverrideHierarchy(m, Interfaces.EXCLUDE).stream().skip(1).collect(Collectors.toSet());
            overrideHierarchy.forEach(overriddenMethod -> {
                if (methodsFromTargetClassAndSuper.contains(overriddenMethod) &&
                    ! overriddenSuperMethodsToRemove.contains(overriddenMethod)) {
                    overriddenSuperMethodsToRemove.add(overriddenMethod);
                }
            });
            
        });

        methodsFromTargetClassAndSuper.removeAll(overriddenSuperMethodsToRemove);
        List<Method> methods = methodsFromTargetClassAndSuper;
        
        // Sort BeforeAction methods ascending using 'order' attribute
        methods.sort((m1, m2) -> {
            int m1Order = m1.getAnnotation(BeforeAction.class).order();
            int m2Order = m2.getAnnotation(BeforeAction.class).order();
            
            return m1Order == m2Order ? 0 : (m1Order < m2Order ? -1 : 1);
        });

        return methods;
    }

}
