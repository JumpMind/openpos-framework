import { AppPage } from './app.po';
import { browser, logging, Key, by, element } from 'protractor';

describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach( () => {
    page = new AppPage();
    page.navigateToTest('loadingscreen');
    page.personalize();
  });

  it( 'should show loading screen', async () => {
      page.sendKey(Key.F1);

      expect(element(by.tagName('body')).getTagName()).toBeTruthy();
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
