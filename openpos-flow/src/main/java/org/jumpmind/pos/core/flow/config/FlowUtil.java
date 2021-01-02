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
 * http://www.gnu.org/licenses.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow.config;

import java.lang.reflect.Method;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.flow.ITransitionStep;
import org.jumpmind.pos.core.flow.OnArrive;
import org.jumpmind.pos.util.event.OnEvent;
import org.jumpmind.pos.core.flow.OnGlobalAction;

@Slf4j
public class FlowUtil {

    public static String getStateName(Class<? extends Object> state) {
        // TODO may consider annotation in the future.
        return state.getSimpleName();
    }

    public static boolean isState(Class<?> clazz) {
        try {
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnArrive.class, true, true);
            if ((IState.class.isAssignableFrom(clazz) || clazz.isAssignableFrom(IState.class)) || (methods != null && !methods.isEmpty())
                    || ITransitionStep.class.isAssignableFrom(clazz) || clazz.isAssignableFrom(ITransitionStep.class)) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            log.debug("Failed to check if class has @OnArrive annotation " + clazz, ex);
            return false;
        }
    }

    public static boolean isTransitionStep(Class<?> clazz) {
        if (ITransitionStep.class.isAssignableFrom(clazz) || clazz.isAssignableFrom(ITransitionStep.class)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isFlowClass(Class<? extends Object> clazz ) {
        return isState(clazz) || isTransitionStep(clazz);
    }

    public static boolean isGlobalActionHandler(Class<? extends Object> clazz) {
        try {
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnGlobalAction.class, true, true);
            if (methods != null && !methods.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            log.debug("Failed to check isGlobalActionHandler on clazz " + clazz, ex);
            return false;
        }
    }

    public static boolean isEventHandler(Class<? extends Object> clazz) {
        try {
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnEvent.class, true, true);
            if (methods != null && !methods.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            log.debug("Failed to check if class has @OnEvent annotation " + clazz, ex);
            return false;
        }
    }


}
