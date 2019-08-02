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
package org.jumpmind.pos.core.ui;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActionItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String action;
    protected String title;
    protected String icon;
    protected boolean enabled = true;
    protected ConfirmationDialog confirmationDialog;
    protected List<ActionItem> children;
    protected boolean sensitive;
    protected String buttonSize;
    protected String fontSize;
    protected String keybind;

    protected String doNotBlockForResponse;

    @JsonIgnore
    protected transient boolean autoAssignEnabled = true;

    public final static String FONT_SIZE_XS = "text-xs";
    public final static String FONT_SIZE_SM = "text-sm";
    public final static String FONT_SIZE_MD = "text-md";
    public final static String FONT_SIZE_LG = "text-lg";
    public final static String FONT_SIZE_XL = "text-xl";

    public final static String BUTTON_SIZE_XS = "menuItem-xs";
    public final static String BUTTON_SIZE_SM = "menuItem-sm";
    public final static String BUTTON_SIZE_MD = "menuItem-md";
    public final static String BUTTON_SIZE_LG = "menuItem-lg";
    public final static String BUTTON_SIZE_XL = "menuItem-xl";
    
    public ActionItem() {
    }
    
    
    public ActionItem(String action) {
        this.action = action;    
    }

    public ActionItem(String action, String title) {
        this.action = action;
        this.title = title;
    }

    public ActionItem(String action, String title, String icon) {
        this(action, title);
        this.icon = icon;
    }    
    
    public ActionItem(boolean autoAssignEnabled, String action, String title, String icon) {
        this(action, title, icon);
        this.autoAssignEnabled = autoAssignEnabled;
    } 
    
    public ActionItem(String action, String title, String icon, String confirmationMessage) {
        this(action, title, icon);
        if (confirmationMessage != null) {
            this.confirmationDialog = new ConfirmationDialog();
            this.confirmationDialog.setTitle(title);
            this.confirmationDialog.setMessage(confirmationMessage);
        }
    }
    
    public ActionItem(String action, String title, String icon, ConfirmationDialog confirmationDialog) {
    	this(action, title, icon );
    	this.confirmationDialog = confirmationDialog;
    }
    
    public ActionItem(String title, String action, boolean enabled) {
        super();
        this.action = action;
        this.title = title;
        this.enabled = enabled;
    }
    
    public ActionItem(String title, String action, String icon, boolean enabled) {
        super();
        this.action = action;
        this.title = title;
        this.enabled = enabled;
        this.icon = icon;
    }
    
    public ActionItem(String title, String action, boolean enabled, boolean sensitive) {
        super();
        this.action = action;
        this.title = title;
        this.enabled = enabled;
        this.sensitive = sensitive;
    }

    
    public String getAction() {
        return action;
    }

    
    public void setAction(String action) {
        this.action = action;
    }

    public ActionItem action(String action) {
        this.setAction(action);
        return this;
    }
    
    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }

    public ActionItem title(String title) {
        this.setTitle(title);
        return this;
    }
    
    
    public String getIcon() {
        return icon;
    }

    
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ActionItem icon(String icon) {
        this.setIcon(icon);
        return this;
    }
    
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    
    public boolean isEnabled() {
        return enabled;
    }
    
    public ActionItem enabled(boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }
    
    public void setChildren(List<ActionItem> children) {
        this.children = children;
    }
    
    public List<ActionItem> getChildren() {
        return children;
    }
    
    public ActionItem children(List<ActionItem> children) {
        this.setChildren(children);
        return this;
    }
    
    public String getConfirmationMessage() {
    	if( this.confirmationDialog != null ) {
            return this.confirmationDialog.getMessage();
    	}
    	
    	return null;
    }
    
    public void setConfirmationMessage(String confirmationMessage) {
    	
    	if( this.confirmationDialog == null ) {
    		this.confirmationDialog = new ConfirmationDialog();
    	}
    	    	
        this.confirmationDialog.setMessage(confirmationMessage);
    }

    public ActionItem confirmationMessage(String confirmationMessage) {
        this.setConfirmationMessage(confirmationMessage);
        return this;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public ActionItem sensitive(boolean sensitive) {
        this.setSensitive(sensitive);
        return this;
    }

    public String getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(String buttonSize) {
        this.buttonSize = buttonSize;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }


	public ConfirmationDialog getConfirmationDialog() {
		return confirmationDialog;
	}


	public void setConfirmationDialog(ConfirmationDialog confirmationDialog) {
		this.confirmationDialog = confirmationDialog;
	}


    public String getKeybind() {
        return keybind;
    }


    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }
    
    public ActionItem keybind(String keybind) {
        this.setKeybind(keybind);
        return this;
    }
    
    public void setAutoAssignEnabled(boolean autoAssignEnabled) {
        this.autoAssignEnabled = autoAssignEnabled;
    }
    
    public boolean isAutoAssignEnabled() {
        return autoAssignEnabled;
    }


    public String getDoNotBlockForResponse() {
        return doNotBlockForResponse;
    }

    public void setDoNotBlockForResponse(String doNotBlockForResponse) {
        this.doNotBlockForResponse = doNotBlockForResponse;
    }
    
}
