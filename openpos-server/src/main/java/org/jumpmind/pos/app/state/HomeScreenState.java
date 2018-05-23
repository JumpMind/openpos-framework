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
package org.jumpmind.pos.app.state;

import org.apache.log4j.Logger;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.flow.ActionHandler;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.screen.HomeScreen;
import org.jumpmind.pos.core.screen.MenuItem;

public class HomeScreenState extends AbstractState implements IState {
    
    private Logger logger = Logger.getLogger(HomeScreenState.class);

    @Override
    public void arrive(Action action) {
        super.arrive(action);
        stateManager.endSession();
        stateManager.showScreen(buildScreen());
    }
    
    @Override
    protected String getDefaultBundleName() {
        return "home";
    }
    
    @ActionHandler
    public void onSell(Action action) {
        logger.info("Sell action intercepted for testing purposes.");
        stateManager.doAction(action);
    }

    protected Screen buildScreen() {
        HomeScreen screen = new HomeScreen();
        screen.addMenuItem(new MenuItem("Sell", "Sell", "credit_card"));
        screen.addMenuItem(new MenuItem("AdvanceSearch", "Item Search", "search"));
        screen.addMenuItem(new MenuItem("DailyOps", "Daily Ops", "store"));
        screen.setName("Home");
        screen.setIcon("home");
        screen.setType("Home");
        return screen;
    }
}
