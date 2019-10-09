# Change Log

## Unreleased 
Released on: 

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#211](https://github.com/JumpMind/openpos-framework/pull/211) | Introduce a pluggable logging framework uses console methods as it&#39;s api |  |Dan Kaste|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |maxwellpettit|
  | [#215](https://github.com/JumpMind/openpos-framework/pull/215) | Build openpos-client-personalize via gradle |  |Chris Henson|
  | [#218](https://github.com/JumpMind/openpos-framework/issues/218) | Layout with bottom components (total, tax, etc) and scrolling is funky on sale screen and return screen when there are &gt; 7 or 8 items |  |Dan Kaste|
  | [#228](https://github.com/JumpMind/openpos-framework/pull/228) | add UIMessage version of dynamic form screen and tests |  |Dan Kaste|
  | [#231](https://github.com/JumpMind/openpos-framework/pull/231) | Feature/client context |  |Dan Kaste|
  | [#233](https://github.com/JumpMind/openpos-framework/pull/233) | Bugfix/cleanup form functions |  |Klementina S. Chirico|
  | [#235](https://github.com/JumpMind/openpos-framework/pull/235) | Feature/incident reporting |  |Dan Kaste|
  | [#239](https://github.com/JumpMind/openpos-framework/pull/239) | Client-side ActionItem implementation |  |Eric Amiralian|
  | [#240](https://github.com/JumpMind/openpos-framework/pull/240) | Baconator builds BaconStrips |  |Eric Amiralian|
  | [#241](https://github.com/JumpMind/openpos-framework/pull/241) | Add support for property crawling of top level String[] property |  |Jason Mihalick|
  | [#246](https://github.com/JumpMind/openpos-framework/pull/246) | Change to printing to support configuration via yml.  Also removed deprecated fields from ChooseOptionsUIMessage |  |Chris Henson|
  | [#248](https://github.com/JumpMind/openpos-framework/pull/248) | Convert boolean to integer to play nicely with databases |  |Chris Henson|
  | [#251](https://github.com/JumpMind/openpos-framework/pull/251) | Feature/mobile fixes |  |maxwellpettit|
  | [#252](https://github.com/JumpMind/openpos-framework/pull/252) | Feature/add instructions to choose options message |  |Klementina S. Chirico|
  | [#254](https://github.com/JumpMind/openpos-framework/pull/254) | Added some Time helpers |  |Dan Kaste|
  | [#255](https://github.com/JumpMind/openpos-framework/pull/255) | Don&#39;t save last dialog if the dialog is closable |  |Chris Henson|
  | [#256](https://github.com/JumpMind/openpos-framework/pull/256) | Remove deprecated projects that aren&#39;t really being used |  |Chris Henson|
  | [#258](https://github.com/JumpMind/openpos-framework/pull/258) | Make the session timer more robust by using spring&#39;s infrastructure |  |Chris Henson|
  | [#261](https://github.com/JumpMind/openpos-framework/pull/261) | Trim sql to avoid blank lines in the log file |  |Chris Henson|
  | [#263](https://github.com/JumpMind/openpos-framework/pull/263) | Convert AbstractTypeCode to String to place nicely with dbSession.fin… |  |Chris Henson|
  | [#267](https://github.com/JumpMind/openpos-framework/pull/267) | Log a UUID called sessionId in the client context so client and server can be correlated |  |Chris Henson|
  | [#268](https://github.com/JumpMind/openpos-framework/pull/268) | Make sure to check for selectionChangedAction in onItemListChange not… |  |Klementina S. Chirico|
  | [#271](https://github.com/JumpMind/openpos-framework/pull/271) | Fix keybinding on sale and return screen |  |Chris Henson|
  | [#272](https://github.com/JumpMind/openpos-framework/pull/272) | adjust minimum button padding |  |Dan Kaste|
  | [#273](https://github.com/JumpMind/openpos-framework/pull/273) | Add the focus conditionally. If the back button was disabled then the focus was going to the hamburger which was awkward. |  |Chris Henson|
  | [#274](https://github.com/JumpMind/openpos-framework/pull/274) | Feature/task list |  |Dan Kaste|
  | [#275](https://github.com/JumpMind/openpos-framework/pull/275) | Bugfix/allow pre selected items on selection list screen |  |Klementina S. Chirico|
  | [#280](https://github.com/JumpMind/openpos-framework/pull/280) | selection-list should only set the selectedItems or selectedItem if t… |  |Klementina S. Chirico|
  | [#281](https://github.com/JumpMind/openpos-framework/pull/281) | Flushing out payment gateway api |  |Chris Henson|
  | [#282](https://github.com/JumpMind/openpos-framework/pull/282) | move serrated edge to theme and create a mixin version |  |Dan Kaste|
  | [#283](https://github.com/JumpMind/openpos-framework/pull/283) | icon didn&#39;t size properly for smaller that 24x24 |  |Dan Kaste|
  | [#284](https://github.com/JumpMind/openpos-framework/pull/284) | Feature/task list style |  |Dan Kaste|
  | [#289](https://github.com/JumpMind/openpos-framework/issues/289) | Should the header icon be to the left of the header title for a dialog header? |  |Klementina S. Chirico|
  | [#291](https://github.com/JumpMind/openpos-framework/pull/291) | Add TimeoutException to openpos-util |  |Klementina S. Chirico|
  | [#292](https://github.com/JumpMind/openpos-framework/pull/292) | Add event publishing and subscribing to the architecture |  |Chris Henson|
  | [#294](https://github.com/JumpMind/openpos-framework/pull/294) | Handle multiple theme classes |  |Dan Kaste|
  | [#295](https://github.com/JumpMind/openpos-framework/pull/295) | Feature/after transition |  |Dan Kaste|
  | [#296](https://github.com/JumpMind/openpos-framework/pull/296) | Bugfix/dialog header icon alignment fix |  |Klementina S. Chirico|
  | [#298](https://github.com/JumpMind/openpos-framework/pull/298) | feature/usb-printing-support |  |mmichalek|
  | [#299](https://github.com/JumpMind/openpos-framework/pull/299) | add console scanner for testing |  |Dan Kaste|
  | [#300](https://github.com/JumpMind/openpos-framework/pull/300) | Feature/override material theme |  |Dan Kaste|
  | [#301](https://github.com/JumpMind/openpos-framework/pull/301) | Feature/price checker |  |Dan Kaste|
  | [#303](https://github.com/JumpMind/openpos-framework/pull/303) | Feature/cash drawer support |  |mmichalek|
  | [#304](https://github.com/JumpMind/openpos-framework/pull/304) | Prompt button doesn&#39;t work |  |Chris Henson|
  | [#305](https://github.com/JumpMind/openpos-framework/pull/305) | Use imageUrl branch in selection list |  |Chris Henson|
  | [#306](https://github.com/JumpMind/openpos-framework/pull/306) | Change the disconnected text from &quot;Reconnecting&quot; to &quot;Connecting&quot; |  |Chris Henson|
  | [#307](https://github.com/JumpMind/openpos-framework/pull/307) | make icons resize with different media sizes |  |Dan Kaste|
  | [#308](https://github.com/JumpMind/openpos-framework/pull/308) | update font sizes and color |  |Dan Kaste|
  | [#309](https://github.com/JumpMind/openpos-framework/pull/309) | fix materials toggle button to be responsive to the font size |  |Dan Kaste|
  | [#310](https://github.com/JumpMind/openpos-framework/pull/310) | make checkboxes use responsive icons |  |Dan Kaste|
  | [#311](https://github.com/JumpMind/openpos-framework/pull/311) | fix a bug with the bottom of the serrated edge |  |Dan Kaste|
  | [#312](https://github.com/JumpMind/openpos-framework/pull/312) | Need to set the current state manager when processing an event |  |Chris Henson|
  | [#313](https://github.com/JumpMind/openpos-framework/pull/313) | Feature/mobile footer |  |Dan Kaste|
  | [#314](https://github.com/JumpMind/openpos-framework/pull/314) | transition steps should not be singletons.  this probably isn&#39;t the final solution |  |Chris Henson|
  | [#315](https://github.com/JumpMind/openpos-framework/pull/315) | add rounded input component |  |Dan Kaste|
  | [#316](https://github.com/JumpMind/openpos-framework/pull/316) | add notifications panel |  |Dan Kaste|
  | [#317](https://github.com/JumpMind/openpos-framework/pull/317) | Feature/success color |  |Dan Kaste|
  | [#318](https://github.com/JumpMind/openpos-framework/pull/318) | add Serializable |  |Dan Kaste|
  | [#319](https://github.com/JumpMind/openpos-framework/pull/319) | fix import path in LocationOverrideDialogComponent |  |Dan Kaste|
  | [#320](https://github.com/JumpMind/openpos-framework/pull/320) | style up the mat toggle button |  |Dan Kaste|
  | [#321](https://github.com/JumpMind/openpos-framework/pull/321) | Don&#39;t throw an error when a keybind is null |  |maxwellpettit|
  | [#322](https://github.com/JumpMind/openpos-framework/pull/322) | add icons for orders |  |Dan Kaste|
  | [#323](https://github.com/JumpMind/openpos-framework/pull/323) | Fix buttons too small in some cases |  |Dan Kaste|
  | [#324](https://github.com/JumpMind/openpos-framework/pull/324) | Feature/self checkout |  |maxwellpettit|
  | [#325](https://github.com/JumpMind/openpos-framework/pull/325) | Core support for better responsive styling |  |Dan Kaste|
  | [#327](https://github.com/JumpMind/openpos-framework/pull/327) | Include src/test/groovy as an intellij test folder so you can run groovy tests |  |Chris Henson|
  | [#329](https://github.com/JumpMind/openpos-framework/pull/329) | put logo on bacon-strip component |  |Klementina S. Chirico|
  | [#330](https://github.com/JumpMind/openpos-framework/pull/330) | Make rest request/response logging configurable in application.yml |  |Chris Henson|
  | [#331](https://github.com/JumpMind/openpos-framework/pull/331) | add some more common styles and the bottom sheet: |  |Dan Kaste|
  | [#334](https://github.com/JumpMind/openpos-framework/pull/334) | Change the default timeout to 2 seconds.  Application become unusable with a default of 30. |  |Chris Henson|
  | [#335](https://github.com/JumpMind/openpos-framework/pull/335) | Add setting to cache remote content for performance |  |Chris Henson|

## 0.4.7 
Released on: 2019-10-07 14:20:23

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#334](https://github.com/JumpMind/openpos-framework/pull/334) | Change the default timeout to 2 seconds.  Application become unusable with a default of 30. |  |Chris Henson|
  | [#335](https://github.com/JumpMind/openpos-framework/pull/335) | Add setting to cache remote content for performance |  |Chris Henson|

## 0.5.7 
Released on: 2019-10-05 22:35:28

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#241](https://github.com/JumpMind/openpos-framework/pull/241) | Add support for property crawling of top level String[] property |  |Jason Mihalick|
  | [#264](https://github.com/JumpMind/openpos-framework/pull/264) | Adding support for status strip on Home Page Component |  |stevencarley|
  | [#265](https://github.com/JumpMind/openpos-framework/pull/265) | Add comment for restore focus |  |maxwellpettit|
  | [#267](https://github.com/JumpMind/openpos-framework/pull/267) | Log a UUID called sessionId in the client context so client and server can be correlated |  |Chris Henson|
  | [#269](https://github.com/JumpMind/openpos-framework/pull/269) | Check if dialog is closeable on screen refresh |  |maxwellpettit|
  | [#270](https://github.com/JumpMind/openpos-framework/pull/270) | Making kebab menu selections full line clickable instead of text only |  |stevencarley|
  | [#277](https://github.com/JumpMind/openpos-framework/pull/277) | Adding min-height to drawer-button, older version of chrome rendering… |  |stevencarley|
  | [#278](https://github.com/JumpMind/openpos-framework/pull/278) | Expanded clickable area for Transaction Menu in Sale Component |  |Eric Amiralian|
  | [#286](https://github.com/JumpMind/openpos-framework/pull/286) | Add loading dialog delay to client configuration |  |maxwellpettit|
  | [#288](https://github.com/JumpMind/openpos-framework/pull/288) | Set the max rows to the max results and set auto commit to false so that the entire result isn&#39;t returned |  |Chris Henson|
  | [#306](https://github.com/JumpMind/openpos-framework/pull/306) | Change the disconnected text from &quot;Reconnecting&quot; to &quot;Connecting&quot; |  |Chris Henson|

## 0.4.5 
Released on: 2019-09-30 12:58:53

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#250](https://github.com/JumpMind/openpos-framework/pull/250) | Added missing AlphanumericText formatter, removed old onBarcodePaste |  |Eric Amiralian|
  | [#285](https://github.com/JumpMind/openpos-framework/pull/285) | Paginate &gt; 15 homescreen menu items |  |Eric Amiralian|
  | [#287](https://github.com/JumpMind/openpos-framework/pull/287) | Feature/banner screen part |  |stevencarley|
  | [#290](https://github.com/JumpMind/openpos-framework/pull/290) | Test occasionally failing on CI with multiple invocations of error handler |  |stevencarley|

## 0.5.6 
Released on: 2019-09-17 17:18:54

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#279](https://github.com/JumpMind/openpos-framework/pull/279) | Unblock actions when a toast message is received |  |Chris Henson|

## 0.4.4 
Released on: 2019-09-12 19:53:00

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#260](https://github.com/JumpMind/openpos-framework/pull/260) | Feature/bacon strip back button |  |stevencarley|
  | [#262](https://github.com/JumpMind/openpos-framework/pull/262) | Get an updated element reference when restoring focus |  |maxwellpettit|

## 0.4.3 
Released on: 2019-09-12 14:58:45

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#245](https://github.com/JumpMind/openpos-framework/pull/245) | Handle pre-formatted eu values in money formatter |  |maxwellpettit|
  | [#249](https://github.com/JumpMind/openpos-framework/pull/249) | Adding support for setting startAt for dynamic-date-form-field |  |stevencarley|
  | [#253](https://github.com/JumpMind/openpos-framework/pull/253) | Don&#39;t save last dialog if it is closable |  |maxwellpettit|
  | [#257](https://github.com/JumpMind/openpos-framework/pull/257) | Expose method to override validators |  |maxwellpettit|
  | [#259](https://github.com/JumpMind/openpos-framework/pull/259) | Add support for logging of uncaught exceptions. |  |Jason Mihalick|

## 0.5.5 
Released on: 2019-09-05 14:07:44

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#247](https://github.com/JumpMind/openpos-framework/pull/247) | Add feature to allow a uimessage to be disabled |  |Chris Henson|

## 0.5.4 
Released on: 2019-09-04 18:24:49

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#244](https://github.com/JumpMind/openpos-framework/pull/244) | Bugfix/selectable item list default select on disabled |  |Klementina S. Chirico|

## 0.4.2 
Released on: 2019-08-28 15:15:09

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#180](https://github.com/JumpMind/openpos-framework/pull/180) | Round to nearest second before splitting and add tests |  |Dan Kaste|
  | [#181](https://github.com/JumpMind/openpos-framework/pull/181) | Date Prompt Focus and Text Size |  |Dan Kaste|
  | [#182](https://github.com/JumpMind/openpos-framework/pull/182) | Feature/personalize app |  |maxwellpettit|
  | [#183](https://github.com/JumpMind/openpos-framework/pull/183) | ActionItem confirmation message constructor now sets title |  |Eric Amiralian|
  | [#185](https://github.com/JumpMind/openpos-framework/pull/185) | Adding 3 Icons for a client project |  |Eric Amiralian|
  | [#186](https://github.com/JumpMind/openpos-framework/pull/186) | Added Icons section &amp; available icons to docs |  |Eric Amiralian|
  | [#187](https://github.com/JumpMind/openpos-framework/pull/187) | Vertically center unlock icon in status bar |  |Eric Amiralian|
  | [#188](https://github.com/JumpMind/openpos-framework/pull/188) | The version is not show up in dev tools |  |Chris Henson|
  | [#190](https://github.com/JumpMind/openpos-framework/pull/190) | Fixed Improper Null check that missed empty strings |  |Eric Amiralian|
  | [#192](https://github.com/JumpMind/openpos-framework/pull/192) | Subscribe to the scanner service in the scan something component |  |maxwellpettit|
  | [#195](https://github.com/JumpMind/openpos-framework/pull/195) | Add @BeforeAction annotation in support of invoking behavior before an action is handled |  |Jason Mihalick|
  | [#197](https://github.com/JumpMind/openpos-framework/pull/197) | DynamicFormUIMessage |  |stevencarleyEric Amiralian|
  | [#198](https://github.com/JumpMind/openpos-framework/pull/198) | Add logging of the legacy dialog resource id for troubleshooting |  |Jason Mihalick|
  | [#200](https://github.com/JumpMind/openpos-framework/pull/200) | Add TRACE level logging before and after state injections are done |  |Jason Mihalick|
  | [#201](https://github.com/JumpMind/openpos-framework/pull/201) | Revert &quot;DynamicFormUIMessage&quot; |  |stevencarley|
  | [#204](https://github.com/JumpMind/openpos-framework/pull/204) | Run OnDepart before state transition, add more TRACE logging to injection |  |Jason Mihalick|
  | [#207](https://github.com/JumpMind/openpos-framework/pull/207) | Support EU currency formatting |  |maxwellpettit|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |maxwellpettit|
  | [#216](https://github.com/JumpMind/openpos-framework/pull/216) | Only add google autocomplete listener once to improve performance |  |maxwellpettit|
  | [#220](https://github.com/JumpMind/openpos-framework/pull/220) | Return the entire list when filtering with an empty string |  |maxwellpettit|
  | [#221](https://github.com/JumpMind/openpos-framework/pull/221) | Use searchable poptarts for auto complete address screens |  |maxwellpettitmaxwellpettit|
  | [#224](https://github.com/JumpMind/openpos-framework/pull/224) | Add validator for disallowing a field to contain all whitespace |  |Jason Mihalick|
  | [#225](https://github.com/JumpMind/openpos-framework/pull/225) | Preventing disabled links from submitting actions |  |stevencarley|
  | [#227](https://github.com/JumpMind/openpos-framework/pull/227) | Fix performance issue with Wedge Scanner |  |Dan Kaste|
  | [#229](https://github.com/JumpMind/openpos-framework/pull/229) | Use DefaultObjectMapper in ScreenService |  |Jason Mihalick|
  | [#230](https://github.com/JumpMind/openpos-framework/pull/230) | Change StateManager to make use of IErrorHandler if one is set |  |Jason Mihalick|

## 0.5.1 
Released on: 2019-08-20 19:23:04

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |Klementina S. Chirico|
  | [#215](https://github.com/JumpMind/openpos-framework/pull/215) | Build openpos-client-personalize via gradle |  |Klementina S. Chirico|
  | [#217](https://github.com/JumpMind/openpos-framework/pull/217) | Bugfix/scanner disabled on choose options destry |  |Klementina S. Chirico|

## 0.5.0 
Released on: 2019-08-19 12:33:09

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#131](https://github.com/JumpMind/openpos-framework/pull/131) | bugfix/selectable-item-multiselect-index | [bug, client] |Klementina S. Chirico|
  | [#136](https://github.com/JumpMind/openpos-framework/pull/136) | Enable annotation processing for intellij automatically | [build] |Chris Henson|
  | [#142](https://github.com/JumpMind/openpos-framework/pull/142) | Use build version as the module version |  |Chris Henson|
  | [#152](https://github.com/JumpMind/openpos-framework/pull/152) | Restyling Sausage Links |  |Eric Amiralian|
  | [#157](https://github.com/JumpMind/openpos-framework/pull/157) | Add new method to dbSession.  findFirstByFields |  |Chris Henson|
  | [#160](https://github.com/JumpMind/openpos-framework/pull/160) | Drop deprecated screens and refactor Actions |  |Dan Kaste|
  | [#161](https://github.com/JumpMind/openpos-framework/pull/161) | Feature/sell item style for loyalty |  |Klementina S. Chirico|
  | [#171](https://github.com/JumpMind/openpos-framework/pull/171) | Feature/dynamic receipt card |  |Klementina S. Chirico|
  | [#173](https://github.com/JumpMind/openpos-framework/pull/173) | JavaPOS driver and printer support for receipts |  |mmichalek|
  | [#174](https://github.com/JumpMind/openpos-framework/pull/174) | Add support for getting a map of objects out of ctx_config |  |Jared|
  | [#184](https://github.com/JumpMind/openpos-framework/pull/184) | Remove unused method from ConfigApplicator |  |Jared|
  | [#188](https://github.com/JumpMind/openpos-framework/pull/188) | The version is not show up in dev tools |  |Chris Henson|
  | [#189](https://github.com/JumpMind/openpos-framework/pull/189) | If no type is configured then don&#39;t send the type at all.  Provide keyedActionName to be able to delineate between scan and keyed |  |Chris Henson|
  | [#191](https://github.com/JumpMind/openpos-framework/pull/191) | Pass the raw type in scan data for debugging new scanners |  |Chris Henson|
  | [#193](https://github.com/JumpMind/openpos-framework/pull/193) | Feature/#24 convert to screen parts |  |Dan Kaste|
  | [#194](https://github.com/JumpMind/openpos-framework/pull/194) | Feature/remove config managers and configuration properties |  |Jared|
  | [#196](https://github.com/JumpMind/openpos-framework/pull/196) | Update StateManager so that a Cancel TransitionResult will send a &quot;Tr… |  |Klementina S. Chirico|
  | [#202](https://github.com/JumpMind/openpos-framework/pull/202) | Bugfix/client config selector permutations |  |Klementina S. Chirico|
  | [#203](https://github.com/JumpMind/openpos-framework/pull/203) | Feature/cleanup sausage link alignment |  |Klementina S. Chirico|
  | [#205](https://github.com/JumpMind/openpos-framework/pull/205) | add install to grunt so it creates version.ts |  |Dan Kaste|
  | [#206](https://github.com/JumpMind/openpos-framework/pull/206) | Remove transient from ActionItem autoAssignEnabled |  |Dan Kaste|
  | [#208](https://github.com/JumpMind/openpos-framework/pull/208) | Add a module api specific to modules backed by an RDBMS |  |Chris Henson|
  | [#209](https://github.com/JumpMind/openpos-framework/pull/209) | Feature/expose rdbms modules |  |Chris Henson|
  | [#212](https://github.com/JumpMind/openpos-framework/pull/212) | add @AssignKeyBindings to ItemDetailUIMessage so it will auto-create … |  |Klementina S. Chirico|
  | [#213](https://github.com/JumpMind/openpos-framework/pull/213) | add @AssignKeyBindings to TenderUIMessage so it will auto-create keyb… |  |Klementina S. Chirico|
  | [#24](https://github.com/JumpMind/openpos-framework/issues/24) | Make main floating action buttons the primary color |  |Dan Kaste|

## 0.4.1 
Released on: 2019-08-01 15:05:32

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#131](https://github.com/JumpMind/openpos-framework/pull/131) | bugfix/selectable-item-multiselect-index | [bug, client] |Klementina S. Chirico|
  | [#133](https://github.com/JumpMind/openpos-framework/pull/133) | Revert CloseDialogMessage as it allows double clicking dialog buttons |  |Chris Henson|
  | [#134](https://github.com/JumpMind/openpos-framework/pull/134) | Add UK date formatter |  |Chris Henson|
  | [#135](https://github.com/JumpMind/openpos-framework/pull/135) | Default no locale phone formatter to numeric |  |Chris Henson|
  | [#137](https://github.com/JumpMind/openpos-framework/pull/137) | Focus service and restore focus when closing kebab menu |  |stevencarley|
  | [#139](https://github.com/JumpMind/openpos-framework/pull/139) | Adding NavListComponent to entryComponents |  |stevencarley|
  | [#140](https://github.com/JumpMind/openpos-framework/pull/140) | Add device_unknown icon from material |  |Jason Mihalick|
  | [#141](https://github.com/JumpMind/openpos-framework/pull/141) | Price checker screens get slower over time.  Subscriptions were not being cleaned up.   | [bug, client] |Chris Henson|
  | [#143](https://github.com/JumpMind/openpos-framework/pull/143) | Making SystemStatusDialogComponent an entryComponent |  |stevencarley|
  | [#144](https://github.com/JumpMind/openpos-framework/pull/144) | Fix dialog screen dialog race condition |  |Chris Henson|
  | [#146](https://github.com/JumpMind/openpos-framework/pull/146) | Refresh status bar control when data changes |  |maxwellpettit|
  | [#147](https://github.com/JumpMind/openpos-framework/pull/147) | Only send selected indexes with select action to improve performance |  |maxwellpettit|
  | [#148](https://github.com/JumpMind/openpos-framework/pull/148) | Adding system status to home component |  |stevencarley|
  | [#149](https://github.com/JumpMind/openpos-framework/pull/149) | Using ViewChildren decorator in FormComponent results in empty list of children DynamicFormFieldComponents | [bug] |Jason Mihalick|
  | [#152](https://github.com/JumpMind/openpos-framework/pull/152) | Restyling Sausage Links |  |Eric Amiralian|
  | [#154](https://github.com/JumpMind/openpos-framework/pull/154) | Add method to convert a string to a FieldInputType |  |Jason Mihalick|
  | [#155](https://github.com/JumpMind/openpos-framework/pull/155) | Add logging of component name being placed in dialog |  |Jason Mihalick|
  | [#156](https://github.com/JumpMind/openpos-framework/pull/156) | Allow for subclasses to override translation of dialog titles |  |Jason Mihalick|
  | [#158](https://github.com/JumpMind/openpos-framework/pull/158) | Sausage Link Spacing in Sell Component |  |Eric Amiralian|
  | [#159](https://github.com/JumpMind/openpos-framework/pull/159) | Add formatter support for fields that allow any character but numerics |  |Jason Mihalick|
  | [#163](https://github.com/JumpMind/openpos-framework/pull/163) | Moving Wait Component to temporarily-shared-screens.module |  |Eric Amiralian|
  | [#167](https://github.com/JumpMind/openpos-framework/pull/167) | Switch Sausage Links to secondary-button for style overrides |  |Eric Amiralian|
  | [#168](https://github.com/JumpMind/openpos-framework/pull/168) | Add support for controlling formatting of password field |  |Jason Mihalick|
  | [#169](https://github.com/JumpMind/openpos-framework/pull/169) | Convert PromptWithInfo to screen-part and UIMessage |  |stevencarley|
  | [#172](https://github.com/JumpMind/openpos-framework/pull/172) | Translated Screen Id |  |Dan Kaste|
  | [#175](https://github.com/JumpMind/openpos-framework/pull/175) | Remote Content |  |Dan Kaste|
  | [#177](https://github.com/JumpMind/openpos-framework/pull/177) | Bugfix/currency text |  |Dan Kaste|
  | [#178](https://github.com/JumpMind/openpos-framework/pull/178) | status-bar &amp; bacon-strip now using the same format |  |Eric Amiralian|

## 0.4.0 
Released on: 2019-07-12 18:47:38

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
  | [#100](https://github.com/JumpMind/openpos-framework/pull/100) | Fix for Bug #99 |  |Klementina Stojanovska Chirico|
  | [#103](https://github.com/JumpMind/openpos-framework/pull/103) | Separate formatter locale and display locale | [enhancement] |maxwellpettit|
  | [#104](https://github.com/JumpMind/openpos-framework/pull/104) | Separate formatter locale and display locale |  |maxwellpettit|
  | [#105](https://github.com/JumpMind/openpos-framework/pull/105) | Generate a change log with each release | [enhancement] |Chris Henson|
  | [#107](https://github.com/JumpMind/openpos-framework/pull/107) | Feature/106 differentiate web orders | [enhancement] |Klementina Stojanovska Chirico|
  | [#110](https://github.com/JumpMind/openpos-framework/pull/110) | Add client documentation | [architecture] |Dan Kaste|
  | [#111](https://github.com/JumpMind/openpos-framework/pull/111) | bugfix/PriceChecker price per unit label | [bug, client] |jliao300|
  | [#114](https://github.com/JumpMind/openpos-framework/issues/114) | Aila Scanner passes Null for UPCA barcode types | [bug] |Dan Kaste|
  | [#115](https://github.com/JumpMind/openpos-framework/pull/115) | 114: Correctly map UPCA barcode types for Aila Scanner | [bug, client] |Dan Kaste|
  | [#116](https://github.com/JumpMind/openpos-framework/pull/116) | added maxResults to the persist api | [enhancement, server] |Chris Henson|
  | [#119](https://github.com/JumpMind/openpos-framework/pull/119) | IValidatorSpec implementations fail deserialization with jackson  | [bug, server] |Jason Mihalick|
  | [#120](https://github.com/JumpMind/openpos-framework/pull/120) | 120: Ensure MaxValueValidator can be serialized, fix typo in error msg | [bug, client, server] |Jason Mihalick|
  | [#122](https://github.com/JumpMind/openpos-framework/pull/122) | bugfix/PromoColor to BL orange | [bug] |jliao300|
  | [#123](https://github.com/JumpMind/openpos-framework/pull/123) | Add test module to make sure we catch and sass errors in themes | [build, client] |Dan Kaste|
  | [#124](https://github.com/JumpMind/openpos-framework/pull/124) | Don&#39;t deploy if the branch name is merge | [bug, build] |Chris Henson|
  | [#126](https://github.com/JumpMind/openpos-framework/pull/126) | Cleanup of client build and documentation update | [build, client] |Dan Kaste|
  | [#87](https://github.com/JumpMind/openpos-framework/pull/87) | Feature/selection list paging | [client, enhancement, server] |Klementina Stojanovska Chirico|
  | [#90](https://github.com/JumpMind/openpos-framework/pull/90) | Feature/location service |  |Klementina Stojanovska Chirico|
  | [#91](https://github.com/JumpMind/openpos-framework/pull/91) | Feature/e2e tests |  |Dan Kaste|
  | [#93](https://github.com/JumpMind/openpos-framework/pull/93) | Bugfix/92 refresh screen |  |Dan Kaste|
  | [#94](https://github.com/JumpMind/openpos-framework/pull/94) | add openpos-translate to maven |  |Dan Kaste|
  | [#95](https://github.com/JumpMind/openpos-framework/pull/95) | Feature/location service | [enhancement] |Klementina Stojanovska Chirico|
  | [#97](https://github.com/JumpMind/openpos-framework/pull/97) | fix npm publish |  |Dan Kaste|

## 0.3.6 
Released on: 2019-07-03 17:05:14

|       | Name   | Labels | Author |
|-------|--------|--------|--------|
