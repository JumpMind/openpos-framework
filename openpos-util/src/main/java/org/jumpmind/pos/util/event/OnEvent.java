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
package org.jumpmind.pos.util.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnEvent {
    /**
     * Only applies if the event type is NOT AppEvent OR it IS an AppEvent (or subclass) and the
     * device ID of the event is the same as the device ID of the current StateManager.
     * In that case, setting this value to {@code true} will allow the event to be processed regardless of
     * the app ID value of the event.
     */
    boolean receiveEventsFromSelf() default false;

    /**
     * TODO describe
     */
    boolean receiveEventsFromPairedDevice() default false;

    /**
     * If this is set, then all event types will be received.  Ignored if {@link #ofTypes()} identifies specific
     * Event types that are to be handled.
     */
    boolean receiveAllEvents() default false;

    /**
     * Use this to specify specific event types that should be handled.  This has the highest precedence of all.
     */
    Class<? extends Event>[] ofTypes() default {};
}
