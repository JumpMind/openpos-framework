import { browser, by, element } from 'protractor';

export class AppPage {
  navigateToTest( testName: string) {
    return browser.get('/#/tests/' + testName) as Promise<any>;
  }

  personalize() {
    browser.executeScript('localStorage.setItem("serverName","localhost")');
    browser.executeScript('localStorage.setItem("serverPort", "6140")');
    browser.executeScript('localStorage.setItem("deviceId", "22222-222")');
    return browser.refresh();
  }

  sendKey(key: string) {
      browser.actions().sendKeys(key).perform();
  }

  loadingDialog() {
    return element(by.id('loader-dialog'));
  }
}
