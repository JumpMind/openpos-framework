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
package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.List;

public class MenuItem implements IUIAction, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String action;
    private String title;
    private String icon;
    private boolean enabled = true;
    private String confirmationMessage;
    private List<MenuItem> children;
    private boolean sensitive;
    
    public MenuItem() {
    }
    
    
    public MenuItem(String action) {
        this.action = action;    
    }

    public MenuItem(String action, String title) {
        this.action = action;
        this.title = title;
    }

    public MenuItem(String action, String title, String icon) {
        this(action, title);
        this.icon = icon;
    }
    
    public MenuItem(String action, String title, IIcon icon) {
        this(action, title, icon.getName());
    }
    
    public MenuItem(String action, String title, String icon, String confirmationMessage) {
        this(action, title, icon );
        this.confirmationMessage = confirmationMessage;
    }  
    
    public MenuItem(String title, String action, boolean enabled) {
        super();
        this.action = action;
        this.title = title;
        this.enabled = enabled;
    }
    
    public MenuItem(String title, String action, boolean enabled, boolean sensitive) {
        super();
        this.action = action;
        this.title = title;
        this.enabled = enabled;
        this.sensitive = sensitive;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setChildren(List<MenuItem> children) {
        this.children = children;
    }
    
    public List<MenuItem> getChildren() {
        return children;
    }
    
    public String getConfirmationMessage() {
        return this.confirmationMessage;
    }
    
    public void setConfirmationMessage( String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }


    public boolean isSensitive() {
        return sensitive;
    }


    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

}
