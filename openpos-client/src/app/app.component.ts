import { Component } from '@angular/core';
import { PluginService, SessionService } from 'openpos-core/services';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  constructor(protected pluginService: PluginService, protected session: SessionService) {
      this.session.setTheme('default-theme');
  }

}
