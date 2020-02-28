# Change Log

## Unreleased 

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#570](https://github.com/JumpMind/openpos-framework/pull/570) | Feature/personalization endpoint |  |Dan Kaste|2020-02-18 15:22:09|
  | [#600](https://github.com/JumpMind/openpos-framework/pull/600) | Feature/single sign on |  |Dan Kaste|2020-02-21 21:28:49|
  | [#601](https://github.com/JumpMind/openpos-framework/pull/601) | add version and certification to BaconStripPart &amp; display the version… |  |Klementina S. Chirico|2020-02-20 17:37:14|
  | [#602](https://github.com/JumpMind/openpos-framework/pull/602) | Feature/fingerprint in icon service |  |Klementina S. Chirico|2020-02-20 21:03:52|
  | [#603](https://github.com/JumpMind/openpos-framework/pull/603) | Bugfix issues with json serialization/deserialization   |  |stevencarley|2020-02-21 19:36:16|
  | [#604](https://github.com/JumpMind/openpos-framework/pull/604) | Export single sign on message |  |Dan Kaste|2020-02-24 14:45:06|
  | [#605](https://github.com/JumpMind/openpos-framework/pull/605) | Upgrade to gradle 5 |  |Chris Henson|2020-02-24 21:33:04|
  | [#606](https://github.com/JumpMind/openpos-framework/pull/606) | Upgrade to gradle 6 |  |Chris Henson|2020-02-25 17:25:50|

## 0.7.0 
Released on: 2020-02-18 13:15:09

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#339](https://github.com/JumpMind/openpos-framework/pull/339) | Updating sale interface and exporting selectable-list-data interface … |  |stevencarley|2019-10-09 13:56:54|
  | [#351](https://github.com/JumpMind/openpos-framework/pull/351) | Add configuration for setting websocket limit and handle errors gracefully |  |Dan Kaste|2019-10-17 18:01:33|
  | [#354](https://github.com/JumpMind/openpos-framework/pull/354) | Add OpenPOS Management Server |  |Jason Mihalick|2019-10-23 15:14:49|
  | [#364](https://github.com/JumpMind/openpos-framework/issues/364) | Use trackby on the sell screen so rendering of lots of items is still fast |  |Klementina S. Chirico|2019-11-27 21:18:20|
  | [#366](https://github.com/JumpMind/openpos-framework/pull/366) | Block disabled actions in session service |  |maxwellpettit|2019-10-24 16:29:00|
  | [#367](https://github.com/JumpMind/openpos-framework/pull/367) | Populate skip button from actionItem to translate text |  |maxwellpettit|2019-10-24 17:28:08|
  | [#368](https://github.com/JumpMind/openpos-framework/pull/368) | Backport changes to support nu-commerce 0.4.  These change do not affect pure openpos applications |  |Chris Henson|2019-10-24 20:24:31|
  | [#369](https://github.com/JumpMind/openpos-framework/pull/369) | Expanding clickable area of transaction menu when items are selected |  |stevencarley|2019-10-25 13:37:32|
  | [#370](https://github.com/JumpMind/openpos-framework/pull/370) | Fix misaligned attribute name scanActionName -&gt; scanAction |  |Jason Mihalick|2019-10-25 16:12:19|
  | [#372](https://github.com/JumpMind/openpos-framework/pull/372) | Support use of DevTools Scan tool when running as non-legacy deployment |  |Jason Mihalick|2019-10-25 16:51:31|
  | [#373](https://github.com/JumpMind/openpos-framework/pull/373) | When downloadNode=true allow download location to be modified |  |Chris Henson|2019-10-25 18:33:09|
  | [#374](https://github.com/JumpMind/openpos-framework/pull/374) | Account for -SNAPSHOT in version.  Cherry picked from master |  |Chris Henson|2019-10-28 13:29:15|
  | [#377](https://github.com/JumpMind/openpos-framework/pull/377) | Set choose options submit button enabled so action doesn&#39;t get blocked |  |maxwellpettit|2019-10-29 13:49:34maxwellpettit|2019-10-29 13:48:32|
  | [#378](https://github.com/JumpMind/openpos-framework/pull/378) | Always expand wildcard classpath variables and order classpath alphab… | [bug] |Chris Henson|2019-10-29 18:02:31|
  | [#379](https://github.com/JumpMind/openpos-framework/pull/379) | Fix failure from retailer build complaining about version being null |  |Jason Mihalick|2019-10-29 18:51:42|
  | [#382](https://github.com/JumpMind/openpos-framework/pull/382) | Allow there to be an empty classpath and remove the -cp switch |  |Jason Mihalick|2019-10-31 13:50:31|
  | [#385](https://github.com/JumpMind/openpos-framework/pull/385) | Add tests to ensure assumed behavior of spring.include.profiles |  |Jason Mihalick|2019-10-31 13:51:15|
  | [#391](https://github.com/JumpMind/openpos-framework/pull/391) | Do not register symds in static map because that causes symmetric-jmx to be loaded which is incompatible with the current version of spring |  |Chris Henson|2019-11-01 13:54:38|
  | [#394](https://github.com/JumpMind/openpos-framework/pull/394) | Feature/help text |  |stevencarley|2019-11-04 15:24:03|
  | [#399](https://github.com/JumpMind/openpos-framework/pull/399) | Allow retailer to customize how SymmetricDS gets initialized |  |Chris Henson|2019-11-05 19:44:18|
  | [#400](https://github.com/JumpMind/openpos-framework/pull/400) | Allow the symds web context to be overridden |  |Chris Henson|2019-11-05 21:19:42Chris Henson|2019-11-05 21:19:17|
  | [#402](https://github.com/JumpMind/openpos-framework/pull/402) | Allow a module&#39;s database connection to be overridden to a different datasource than the default | [architecture, enhancement] |Chris Henson|2019-11-06 17:23:12|
  | [#407](https://github.com/JumpMind/openpos-framework/pull/407) | Add a setting to ignore @TableDef annotations in the super classes |  |Chris Henson|2019-11-06 21:01:33Chris Henson|2019-11-06 21:01:13|
  | [#409](https://github.com/JumpMind/openpos-framework/pull/409) | Allow an optional startup class to be executed before app is started |  |Jason Mihalick|2019-11-07 21:59:29|
  | [#411](https://github.com/JumpMind/openpos-framework/pull/411) | @TableDef.ignoreSuperTableDef still needs to include super @ColumnDefs |  |Chris Henson|2019-11-08 13:13:26|
  | [#412](https://github.com/JumpMind/openpos-framework/pull/412) | Add support for using the &quot;in&quot; clause in sql queries |  |Chris Henson|2019-11-08 15:18:29|
  | [#413](https://github.com/JumpMind/openpos-framework/pull/413) | Updates required after Openpos Management Server merge to master |  |Jason Mihalick|2019-11-13 18:49:46|
  | [#415](https://github.com/JumpMind/openpos-framework/pull/415) | Add defaults for create and update fields |  |Chris Henson|2019-11-09 15:02:38|
  | [#416](https://github.com/JumpMind/openpos-framework/pull/416) | Fix bug with ignoreSuperTableDef during query |  |Chris Henson|2019-11-12 13:30:10|
  | [#417](https://github.com/JumpMind/openpos-framework/pull/417) | bug fix and test for actionhandler not working if it is also a terminating action |  |Dan Kaste|2019-11-12 18:41:28|
  | [#418](https://github.com/JumpMind/openpos-framework/pull/418) | Feature/reusable scan component |  |Klementina S. Chirico|2019-11-12 21:48:18|
  | [#419](https://github.com/JumpMind/openpos-framework/pull/419) | Feature/mobile tender |  |maxwellpettit|2019-11-13 15:28:30|
  | [#421](https://github.com/JumpMind/openpos-framework/pull/421) | Fix Openpos Management Server initial connection timeout and startup failed retry |  |Jason Mihalick|2019-11-14 21:04:52Jason Mihalick|2019-11-13 19:22:00|
  | [#423](https://github.com/JumpMind/openpos-framework/pull/423) | Feature/map varchar db dates to java.util.date automagically |  |Chris Henson|2019-11-14 21:05:37Chris Henson|2019-11-14 13:28:11|
  | [#424](https://github.com/JumpMind/openpos-framework/pull/424) | Disallow two actions from running concurrently on different threads |  |Chris Henson|2019-11-14 21:07:37Chris Henson|2019-11-14 20:05:18|
  | [#425](https://github.com/JumpMind/openpos-framework/pull/425) | Bugfix/search expand screen part |  |maxwellpettit|2019-11-14 20:18:29|
  | [#426](https://github.com/JumpMind/openpos-framework/pull/426) | Feature/item detail |  |Dan Kaste|2019-11-18 16:38:34|
  | [#427](https://github.com/JumpMind/openpos-framework/pull/427) | The not busy check breaks transitions |  |Chris Henson|2019-11-19 15:22:40Chris Henson|2019-11-19 15:13:23|
  | [#428](https://github.com/JumpMind/openpos-framework/pull/428) | Do not depend on symds persistence code.  The api changed so i&#39;m just pulling the code local |  |Chris Henson|2019-11-19 18:35:43|
  | [#429](https://github.com/JumpMind/openpos-framework/pull/429) | Point to the latest version of SymmetricDS =&gt; 3.11.0 |  |Chris Henson|2019-11-19 18:46:02|
  | [#430](https://github.com/JumpMind/openpos-framework/pull/430) | If a screen is shown right after an action the next action can be rejected | [bug] |Chris Henson|2019-11-20 13:55:42|
  | [#431](https://github.com/JumpMind/openpos-framework/issues/431) | Allow for composition in model classes | [enhancement] |gwilmer|2019-12-05 14:29:12|
  | [#432](https://github.com/JumpMind/openpos-framework/pull/432) | Feature/ux updates |  |Klementina S. Chirico|2019-11-21 21:05:22|
  | [#436](https://github.com/JumpMind/openpos-framework/pull/436) | Feature/item search no results |  |Dan Kaste|2019-11-22 14:56:20|
  | [#437](https://github.com/JumpMind/openpos-framework/pull/437) | Ellipse long text in bacon strip title |  |maxwellpettit|2019-11-22 17:29:53|
  | [#438](https://github.com/JumpMind/openpos-framework/pull/438) | Feature/scan or search focus |  |Dan Kaste|2019-11-25 14:21:25|
  | [#439](https://github.com/JumpMind/openpos-framework/pull/439) | SymDS database was being initialized twice |  |Chris Henson|2019-11-22 19:10:10|
  | [#440](https://github.com/JumpMind/openpos-framework/pull/440) | Use mgmt server as a library |  |Chris Henson|2019-11-22 19:10:53|
  | [#441](https://github.com/JumpMind/openpos-framework/pull/441) | Log that an insert fails as it is not the expected path |  |Chris Henson|2019-11-25 17:53:33|
  | [#442](https://github.com/JumpMind/openpos-framework/pull/442) | Bugfix/self checkout cleanup |  |maxwellpettit|2019-11-25 19:20:01|
  | [#443](https://github.com/JumpMind/openpos-framework/pull/443) | Pass doNotBlockForResponse back to the server so it knows to not block as well |  |Chris Henson|2019-11-25 20:01:42|
  | [#444](https://github.com/JumpMind/openpos-framework/pull/444) | Fix overflowing images and text on sale screen buttons |  |maxwellpettit|2019-11-26 12:50:51|
  | [#445](https://github.com/JumpMind/openpos-framework/pull/445) | only color immediate child containers |  |Dan Kaste|2019-11-26 13:37:34|
  | [#446](https://github.com/JumpMind/openpos-framework/issues/446) | State Manager blocks customer display action from on onEvent handler. | [bug] |Chris Henson|2019-11-27 20:43:14Chris Henson|2019-11-27 14:40:04|
  | [#447](https://github.com/JumpMind/openpos-framework/pull/447) | Add batch insert method |  |Chris Henson|2019-11-26 20:38:33|
  | [#450](https://github.com/JumpMind/openpos-framework/issues/450) | Prompt with options sends the first option when you  press the Enter key.   We should probably make the cashier choose an option and disallow the Enter key |  |Klementina S. Chirico|2019-12-02 18:18:00|
  | [#452](https://github.com/JumpMind/openpos-framework/pull/452) | State Manager blocks customer display action from on onEvent handler | [bug] |Chris Henson|2019-11-27 14:40:04|
  | [#453](https://github.com/JumpMind/openpos-framework/pull/453) | Enhancements for Toggle Group and Toggle Button |  |stevencarley|2019-11-27 16:46:55|
  | [#454](https://github.com/JumpMind/openpos-framework/pull/454) | Feature/data message provider for sale and return |  |Klementina S. Chirico|2019-11-29 13:54:21|
  | [#455](https://github.com/JumpMind/openpos-framework/pull/455) | Only publish app events from others |  |Chris Henson|2019-11-27 20:43:14|
  | [#456](https://github.com/JumpMind/openpos-framework/pull/456) | Feature/trackby on sale item list |  |Klementina S. Chirico|2019-11-27 21:18:20|
  | [#457](https://github.com/JumpMind/openpos-framework/pull/457) | Add support for runtime discovery of translators using annotations |  |Jason Mihalick|2019-11-29 13:28:39|
  | [#458](https://github.com/JumpMind/openpos-framework/pull/458) | Prompt with options sends the first option when you press the Enter key. We should probably make the cashier choose an option and disallow the Enter key. Fixed #450 |  |Klementina S. Chirico|2019-12-02 18:18:00|
  | [#460](https://github.com/JumpMind/openpos-framework/pull/460) | Feature/order services - Allow for Composition in Model Classes |  |gwilmer|2019-12-05 14:29:12|
  | [#462](https://github.com/JumpMind/openpos-framework/pull/462) | Cleanup old translate classes |  |Chris Henson|2019-12-05 18:50:23|
  | [#463](https://github.com/JumpMind/openpos-framework/pull/463) | Feature/support ui data message callbacks in legacy layer |  |Jason Mihalick|2019-12-05 23:05:14|
  | [#464](https://github.com/JumpMind/openpos-framework/pull/464) | Re-expose DatabaseSchema.createMetaData as a static method | [bug] |Chris Henson|2019-12-06 14:04:04|
  | [#465](https://github.com/JumpMind/openpos-framework/pull/465) | Feature/keybinding support |  |Klementina S. Chirico|2019-12-09 16:38:10|
  | [#466](https://github.com/JumpMind/openpos-framework/pull/466) | Add support for indexed arguments in addition to existing named params |  |Jason Mihalick|2019-12-06 20:08:05|
  | [#467](https://github.com/JumpMind/openpos-framework/pull/467) | Add ActionHandler support to translators |  |Jason Mihalick|2019-12-09 12:07:17|
  | [#468](https://github.com/JumpMind/openpos-framework/pull/468) | upgrade jna version and prevent symds from pulling in newer versions on accident | [bug] |Chris Henson|2019-12-09 21:48:13|
  | [#469](https://github.com/JumpMind/openpos-framework/issues/469) | Service wrapper NPE on non-existant classpath entry |  |gwilmer|2019-12-10 15:10:03|
  | [#470](https://github.com/JumpMind/openpos-framework/pull/470) | Fixed #469.  Service wrapper NPE on non-existent directory |  |gwilmer|2019-12-10 15:10:03|
  | [#472](https://github.com/JumpMind/openpos-framework/pull/472) | Feature/self checkout docs |  |maxwellpettit|2019-12-10 18:25:07|
  | [#473](https://github.com/JumpMind/openpos-framework/pull/473) | Add media sizing documentation |  |maxwellpettit|2019-12-10 18:34:32|
  | [#474](https://github.com/JumpMind/openpos-framework/pull/474) | App locks up after an unexpected error | [bug] |Chris Henson|2019-12-15 21:31:03|
  | [#475](https://github.com/JumpMind/openpos-framework/issues/475) | When using arrow keys to give focus to the first item the expanded item is only half shown | [bug] |Klementina S. Chirico|2019-12-16 19:28:02|
  | [#478](https://github.com/JumpMind/openpos-framework/pull/478) | update sale-item-card-list so that when the arrow keys are clicked, e… |  |Klementina S. Chirico|2019-12-16 19:28:02|
  | [#479](https://github.com/JumpMind/openpos-framework/pull/479) | Customer display locks up |  |Chris Henson|2019-12-16 19:48:14|
  | [#480](https://github.com/JumpMind/openpos-framework/pull/480) | add instructions to selfCheckoutFormUIMessage and component |  |Klementina S. Chirico|2019-12-17 15:12:35|
  | [#481](https://github.com/JumpMind/openpos-framework/issues/481) | Spacebar keybinding on item cards getting triggered when space bar is clicked inside of self checkout add customer form | [bug] |Klementina S. Chirico|2019-12-17 20:12:19|
  | [#482](https://github.com/JumpMind/openpos-framework/pull/482) | buttonSubscription never getting unsubscribed onDestroy. Fix for issu… |  |Klementina S. Chirico|2019-12-17 20:12:19|
  | [#484](https://github.com/JumpMind/openpos-framework/pull/484) | LocationChanged action needs doNotBlockForResponse set to true |  |Chris Henson|2019-12-17 21:06:05|
  | [#485](https://github.com/JumpMind/openpos-framework/pull/485) | Add support for an optional Help button on the self checkout sale screen |  |Jason Mihalick|2019-12-18 01:14:22|
  | [#486](https://github.com/JumpMind/openpos-framework/pull/486) | Bug/fix array error on sale-item-card-list.component.ts |  |Jason Mihalick|2019-12-18 01:14:53|
  | [#487](https://github.com/JumpMind/openpos-framework/pull/487) | Additional fixes for preventing screen lockup due to location changed messages |  |Chris Henson|2019-12-17 22:01:48|
  | [#488](https://github.com/JumpMind/openpos-framework/pull/488) | Feature/update mobile home look |  |Klementina S. Chirico|2019-12-18 20:21:43|
  | [#489](https://github.com/JumpMind/openpos-framework/pull/489) | Translate booleans to 1s and 0s for jdbc drivers that do not support boolean types |  |Chris Henson|2019-12-18 22:23:59|
  | [#490](https://github.com/JumpMind/openpos-framework/pull/490) | Change default keyboard layout on Scan or Search Component from &#39;Numeric&#39; to &#39;US Standard&#39; |  |Jason Mihalick|2019-12-18 22:32:29|
  | [#492](https://github.com/JumpMind/openpos-framework/pull/492) | Missing columns during update/insert |  |Chris Henson|2019-12-19 15:21:17|
  | [#493](https://github.com/JumpMind/openpos-framework/pull/493) | A failed insert in postgres results in 0 rows being updated versus throwing an error |  |Chris Henson|2019-12-19 15:21:34|
  | [#494](https://github.com/JumpMind/openpos-framework/pull/494) | Don&#39;t sync triggers at startup.  just wanted to save on startup time. |  |Chris Henson|2019-12-19 15:21:06|
  | [#495](https://github.com/JumpMind/openpos-framework/pull/495) | Allow Toasts leave the UI in a disabled mode |  |Chris Henson|2019-12-19 16:29:54|
  | [#496](https://github.com/JumpMind/openpos-framework/pull/496) | Bugfix/unsubscribe action service |  |Dan Kaste|2019-12-19 20:04:33|
  | [#497](https://github.com/JumpMind/openpos-framework/pull/497) | Upgrading Aila plugin to use promises instead of callbacks and new setConfig function |  |stevencarley|2019-12-19 20:19:16|
  | [#498](https://github.com/JumpMind/openpos-framework/pull/498) | default the device id to the installation id |  |Dan Kaste|2019-12-20 13:20:43|
  | [#499](https://github.com/JumpMind/openpos-framework/pull/499) | Add support for adding an icon to the self checkout skip button |  |Jason Mihalick|2019-12-20 02:50:41|
  | [#500](https://github.com/JumpMind/openpos-framework/pull/500) | Add support for outjection on translators |  |Jason Mihalick|2019-12-20 02:51:00|
  | [#502](https://github.com/JumpMind/openpos-framework/pull/502) | feature/improve error message when flow attempts to transition back t… |  |mmichalek|2019-12-23 13:11:20|
  | [#503](https://github.com/JumpMind/openpos-framework/pull/503) | Fix undefined error when there is no enabled property on the action |  |Chris Henson|2019-12-26 14:16:08|
  | [#504](https://github.com/JumpMind/openpos-framework/pull/504) | Add selectedAction to ActionItemGroup |  |Chris Henson|2019-12-26 14:17:40|
  | [#505](https://github.com/JumpMind/openpos-framework/pull/505) | The on arrive method gets called twice when it is in a super class |  |Chris Henson|2019-12-27 16:52:56|
  | [#506](https://github.com/JumpMind/openpos-framework/pull/506) | Bugfix/mobile bug fixes |  |maxwellpettit|2019-12-27 20:02:02|
  | [#507](https://github.com/JumpMind/openpos-framework/pull/507) | Feature/sale item card order indicator |  |Klementina S. Chirico|2020-12-31 13:07:40|
  | [#508](https://github.com/JumpMind/openpos-framework/pull/508) | ElectronLogger has hard-coded &#39;nu-client.log&#39; for client log filename |  |Jason Mihalick|2020-12-30 21:49:30|
  | [#509](https://github.com/JumpMind/openpos-framework/pull/509) | Added helper method to action to recusively check for a specific action |  |Chris Henson|2020-12-30 21:51:22|
  | [#510](https://github.com/JumpMind/openpos-framework/pull/510) | Redo of my prior commits to make electron logging configurable |  |Jason Mihalick|2020-01-02 11:09:03|
  | [#511](https://github.com/JumpMind/openpos-framework/pull/511) | Use lombok on dynamic form ui message |  |Chris Henson|2020-01-01 22:00:19|
  | [#512](https://github.com/JumpMind/openpos-framework/pull/512) | Support progress bar on the options screen |  |Chris Henson|2020-01-01 22:01:29|
  | [#513](https://github.com/JumpMind/openpos-framework/pull/513) | Fix item detail and overflow on mobile |  |maxwellpettit|2020-01-02 17:18:02|
  | [#514](https://github.com/JumpMind/openpos-framework/pull/514) | Allow override of the default icon for self checkout menu back button |  |Jason Mihalick|2020-01-02 19:17:14|
  | [#515](https://github.com/JumpMind/openpos-framework/pull/515) | Add basic bacon strip to use on home screen |  |maxwellpettit|2020-01-02 21:07:48|
  | [#516](https://github.com/JumpMind/openpos-framework/pull/516) | bugfix/fix-wedge-scanning-on-ios |  |mmichalek|2020-01-03 14:39:34|
  | [#517](https://github.com/JumpMind/openpos-framework/pull/517) | Feature/show linked orders on sale screen |  |Klementina S. Chirico|2020-01-05 15:11:00|
  | [#518](https://github.com/JumpMind/openpos-framework/pull/518) | Fix styling issues on sale screen for nrf |  |maxwellpettit|2020-01-03 18:42:10|
  | [#519](https://github.com/JumpMind/openpos-framework/pull/519) | Work around bug with Jackson that allows errant convert of map to Form |  |Jason Mihalick|2020-01-05 00:20:35|
  | [#521](https://github.com/JumpMind/openpos-framework/pull/521) | set focusInitial to false in the sale component so that keyboard does… |  |Klementina S. Chirico|2020-01-07 13:44:30|
  | [#522](https://github.com/JumpMind/openpos-framework/pull/522) | add dialog version of auto-complete-address |  |Dan Kaste|2020-01-07 13:43:39|
  | [#523](https://github.com/JumpMind/openpos-framework/pull/523) | add app-scan-part to self-checkout-opotions component and self-checko… |  |Klementina S. Chirico|2020-01-07 17:39:35|
  | [#524](https://github.com/JumpMind/openpos-framework/pull/524) | Make sell screen prompt and image a screen part |  |Jason Mihalick|2020-01-14 16:23:25Jason Mihalick|2020-01-09 16:17:57|
  | [#525](https://github.com/JumpMind/openpos-framework/pull/525) | Bugfix/prompt and icon fixes |  |Klementina S. Chirico|2020-01-08 15:19:24|
  | [#526](https://github.com/JumpMind/openpos-framework/pull/526) | Bugfix/add item card gift receipt indicator |  |Klementina S. Chirico|2020-01-08 16:26:25|
  | [#527](https://github.com/JumpMind/openpos-framework/pull/527) | include AddNote in IconType and map it in icon.service.ts |  |Klementina S. Chirico|2020-01-09 15:23:53|
  | [#528](https://github.com/JumpMind/openpos-framework/pull/528) | Adapt returns screen to use ImageTextPanel and item count |  |Jason Mihalick|2020-01-09 19:55:20|
  | [#529](https://github.com/JumpMind/openpos-framework/pull/529) | event.path is undefined when using selectable item list in ios and ca… |  |Klementina S. Chirico|2020-01-09 21:02:55|
  | [#530](https://github.com/JumpMind/openpos-framework/pull/530) | Rearrange numeric keyboard to standard layout like on phones |  |Jason Mihalick|2020-01-10 16:54:42|
  | [#531](https://github.com/JumpMind/openpos-framework/pull/531) | Adding delete, batchUpdate and batchDelete methods to DBSession. |  |stevencarley|2020-01-30 14:43:25|
  | [#532](https://github.com/JumpMind/openpos-framework/pull/532) | Bugfix/primary button dont emit click if disabled |  |Klementina S. Chirico|2020-01-10 21:04:01|
  | [#533](https://github.com/JumpMind/openpos-framework/pull/533) | Add better gift icon and add spacing between title and icon |  |Jason Mihalick|2020-01-13 13:07:08Jason Mihalick|2020-01-10 21:19:49|
  | [#534](https://github.com/JumpMind/openpos-framework/pull/534) | Bugfix/mobile loyalty adjustments |  |Klementina S. Chirico|2020-01-10 21:42:37|
  | [#535](https://github.com/JumpMind/openpos-framework/pull/535) | Customer display shows events from other devices |  |Chris Henson|2020-01-13 13:39:32|
  | [#536](https://github.com/JumpMind/openpos-framework/pull/536) | Bugfix/memory leaks |  |Dan Kaste|2020-01-13 18:04:24|
  | [#537](https://github.com/JumpMind/openpos-framework/pull/537) | Add in responsive-class directives to item total section |  |Jason Mihalick|2020-01-14 16:23:25|
  | [#538](https://github.com/JumpMind/openpos-framework/pull/538) | Add order icons to icon service |  |maxwellpettit|2020-01-14 17:22:51|
  | [#539](https://github.com/JumpMind/openpos-framework/pull/539) | Feature/core unit tests |  |Dan Kaste|2020-01-16 15:44:11|
  | [#540](https://github.com/JumpMind/openpos-framework/pull/540) | Reset the state manager when an error occurs |  |Chris Henson|2020-01-16 21:08:55|
  | [#542](https://github.com/JumpMind/openpos-framework/pull/542) | Feature/use scan part across screens |  |Klementina S. Chirico|2020-01-17 20:12:08|
  | [#543](https://github.com/JumpMind/openpos-framework/pull/543) | feature/make-item-count-less-prominent |  |mmichalek|2020-01-17 22:15:56|
  | [#544](https://github.com/JumpMind/openpos-framework/pull/544) | Added a more stylish error dialog |  |Chris Henson|2020-01-20 14:20:56|
  | [#545](https://github.com/JumpMind/openpos-framework/pull/545) | Feature/update ncr client |  |gwilmer|2020-01-20 04:24:14|
  | [#547](https://github.com/JumpMind/openpos-framework/pull/547) | Hide the initializing screen and bump the connection timeout to something very big |  |Chris Henson|2020-01-20 17:16:19|
  | [#548](https://github.com/JumpMind/openpos-framework/pull/548) | update mobile-sale-item-list to show instructions and image using app… |  |Klementina S. Chirico|2020-01-20 20:53:56|
  | [#549](https://github.com/JumpMind/openpos-framework/pull/549) | Queue scan events on the client |  |Chris Henson|2020-01-21 14:01:51|
  | [#550](https://github.com/JumpMind/openpos-framework/issues/550) | Add a check in the SaveDeviceEndpoint to check that 2 appIds are allowed to share the same deviceID. IE a pos shouldn&#39;t share device Id&#39;s with a self-checkout |  |gwilmer|2020-01-20 04:24:14|
  | [#551](https://github.com/JumpMind/openpos-framework/pull/551) | Add support to wrapper for optional Java process working directory |  |Jason Mihalick|2020-01-21 21:50:42|
  | [#552](https://github.com/JumpMind/openpos-framework/pull/552) | Use smaller buttons on home screen for tablets |  |maxwellpettit|2020-01-23 15:15:56|
  | [#553](https://github.com/JumpMind/openpos-framework/pull/553) | Adding support for display of a timer countdown on dialogs |  |Jason Mihalick|2020-01-24 19:38:45|
  | [#555](https://github.com/JumpMind/openpos-framework/pull/555) | Improve mobile layouts on landscape for small tablets |  |maxwellpettit|2020-01-27 17:24:59|
  | [#556](https://github.com/JumpMind/openpos-framework/pull/556) | Add ability to override the keyboard css classes based on keyboard layout name |  |Jason Mihalick|2020-01-27 15:01:48|
  | [#558](https://github.com/JumpMind/openpos-framework/pull/558) | Screen data not getting updated for back to back displays of two different screens of the same type |  |Jason Mihalick|2020-01-27 20:01:37|
  | [#561](https://github.com/JumpMind/openpos-framework/pull/561) | Attempt to start all modules even if one module fails |  |Chris Henson|2020-01-29 18:21:15|
  | [#562](https://github.com/JumpMind/openpos-framework/pull/562) | Fixing delete unit test |  |stevencarley|2020-01-30 16:10:53|
  | [#565](https://github.com/JumpMind/openpos-framework/pull/565) | Pop Tart options laying out incorrectly |  |Jason Mihalick|2020-02-03 18:41:25|
  | [#566](https://github.com/JumpMind/openpos-framework/issues/566) | Fix DbSession to handle sql in statements that contain more than 1000 elements in an &quot;in&quot; expression |  |stevencarley|2020-02-04 13:29:38|
  | [#567](https://github.com/JumpMind/openpos-framework/pull/567) | Use LONGVARCHAR on Oracle versus CLOB |  |Chris Henson|2020-01-31 15:07:49|
  | [#568](https://github.com/JumpMind/openpos-framework/pull/568) | Platform is null during documentation build | [bug] |Chris Henson|2020-02-03 13:29:24|
  | [#569](https://github.com/JumpMind/openpos-framework/pull/569) | Split in/not in clauses in prepared statement with over 1000 elements |  |stevencarley|2020-02-04 13:29:38|
  | [#571](https://github.com/JumpMind/openpos-framework/pull/571) | Split in/not in clauses in prepared statement with over 1000 elements… |  |stevencarley|2020-02-04 13:29:38|
  | [#572](https://github.com/JumpMind/openpos-framework/pull/572) | Bugfix/gift card indicator alignment and add on mobile |  |Klementina S. Chirico|2020-02-05 15:18:22|
  | [#573](https://github.com/JumpMind/openpos-framework/pull/573) | Fix when expanding in clause copies parenthesis before column name |  |stevencarley|2020-02-05 20:49:51|
  | [#574](https://github.com/JumpMind/openpos-framework/pull/574) | add function to ReflectUtils to return the PropertyDescriptor |  |Klementina S. Chirico|2020-02-05 22:04:18|
  | [#575](https://github.com/JumpMind/openpos-framework/pull/575) | Support Enter and Esc with the confirmation dialog |  |Chris Henson|2020-02-05 22:23:15|
  | [#576](https://github.com/JumpMind/openpos-framework/pull/576) | update to handle on keydown.enter instead of keyup.enter &amp; pass event… |  |Klementina S. Chirico|2020-02-06 17:01:05|
  | [#577](https://github.com/JumpMind/openpos-framework/issues/577) | In Electron the scroll bar scrolls for entire window instead of just item list (happens both for sale and returns)  Add items until screen is full, screen will add scroll bar on far right and &#39;Next&#39; will move off screen. |  |Klementina S. Chirico|2020-02-07 17:17:07|
  | [#578](https://github.com/JumpMind/openpos-framework/pull/578) | fix issue #577 |  |Klementina S. Chirico|2020-02-07 17:17:07|
  | [#580](https://github.com/JumpMind/openpos-framework/pull/580) | Bugfix/focus fixes |  |Klementina S. Chirico|2020-02-10 14:54:56|
  | [#581](https://github.com/JumpMind/openpos-framework/pull/581) | Feature/custom validation messages for prompts |  |Klementina S. Chirico|2020-02-10 14:47:23|
  | [#584](https://github.com/JumpMind/openpos-framework/pull/584) | add a till icon and a device icon. Update IconType to add device &amp; up… |  |Klementina S. Chirico|2020-02-10 21:53:00|
  | [#586](https://github.com/JumpMind/openpos-framework/pull/586) | add an additional xl size for app-icon |  |Klementina S. Chirico|2020-02-12 19:52:07|
  | [#587](https://github.com/JumpMind/openpos-framework/pull/587) | update the till icon to have some extra padding like all other icons.… |  |Klementina S. Chirico|2020-02-12 20:39:34|
  | [#588](https://github.com/JumpMind/openpos-framework/pull/588) | Add feature to publish application events to hazelcast so they can shared across multiple vms |  |Chris Henson|2020-02-13 19:15:35|
  | [#589](https://github.com/JumpMind/openpos-framework/pull/589) | update bacon-drawer.component.html to handle keydown enter as a butto… |  |Klementina S. Chirico|2020-02-14 15:07:30|
  | [#593](https://github.com/JumpMind/openpos-framework/pull/593) | Attempt to work around gateway timeouts be increasing timeouts |  |Chris Henson|2020-02-14 17:44:24|
  | [#595](https://github.com/JumpMind/openpos-framework/pull/595) | Make sure we show the alt text if the not found image also doesn&#39;t show. |  |Dan Kaste|2020-02-14 19:37:13|
  | [#596](https://github.com/JumpMind/openpos-framework/pull/596) | Move Sample.java to openpos-util so it can be used by api projects |  |Chris Henson|2020-02-16 23:12:38|
  | [#597](https://github.com/JumpMind/openpos-framework/pull/597) | bugfix/peristence layer not properly tracking if an object is new |  |mmichalek|2020-02-17 20:27:30|

## 0.4.12 
Released on: 2019-11-22 18:14:53

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#434](https://github.com/JumpMind/openpos-framework/pull/434) | Exclude slf4j from symds dependency |  |Chris Henson|2019-11-20 19:59:13|

## 0.6.1 
Released on: 2019-11-12 18:10:22

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#211](https://github.com/JumpMind/openpos-framework/pull/211) | Introduce a pluggable logging framework uses console methods as it&#39;s api |  |Dan Kaste|2019-08-26 14:44:05|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |maxwellpettit|2019-08-19 17:21:56|
  | [#215](https://github.com/JumpMind/openpos-framework/pull/215) | Build openpos-client-personalize via gradle |  |Chris Henson|2019-08-19 17:40:27|
  | [#218](https://github.com/JumpMind/openpos-framework/issues/218) | Layout with bottom components (total, tax, etc) and scrolling is funky on sale screen and return screen when there are &gt; 7 or 8 items |  |Dan Kaste|2019-08-29 18:48:18|
  | [#228](https://github.com/JumpMind/openpos-framework/pull/228) | add UIMessage version of dynamic form screen and tests |  |Dan Kaste|2019-08-23 11:21:12|
  | [#231](https://github.com/JumpMind/openpos-framework/pull/231) | Feature/client context |  |Dan Kaste|2019-08-28 17:01:37|
  | [#233](https://github.com/JumpMind/openpos-framework/pull/233) | Bugfix/cleanup form functions |  |Klementina S. Chirico|2019-08-28 19:04:52|
  | [#235](https://github.com/JumpMind/openpos-framework/pull/235) | Feature/incident reporting |  |Dan Kaste|2019-08-29 17:42:04|
  | [#239](https://github.com/JumpMind/openpos-framework/pull/239) | Client-side ActionItem implementation |  |Eric Amiralian|2019-08-30 20:01:05|
  | [#240](https://github.com/JumpMind/openpos-framework/pull/240) | Baconator builds BaconStrips |  |Eric Amiralian|2019-09-03 13:41:06|
  | [#241](https://github.com/JumpMind/openpos-framework/pull/241) | Add support for property crawling of top level String[] property |  |Jason Mihalick|2019-09-04 14:47:39|
  | [#246](https://github.com/JumpMind/openpos-framework/pull/246) | Change to printing to support configuration via yml.  Also removed deprecated fields from ChooseOptionsUIMessage |  |Chris Henson|2019-09-05 15:16:01|
  | [#248](https://github.com/JumpMind/openpos-framework/pull/248) | Convert boolean to integer to play nicely with databases |  |Chris Henson|2019-09-05 19:38:52|
  | [#251](https://github.com/JumpMind/openpos-framework/pull/251) | Feature/mobile fixes |  |maxwellpettit|2019-09-09 15:37:49|
  | [#252](https://github.com/JumpMind/openpos-framework/pull/252) | Feature/add instructions to choose options message |  |Klementina S. Chirico|2019-09-09 17:47:11|
  | [#254](https://github.com/JumpMind/openpos-framework/pull/254) | Added some Time helpers |  |Dan Kaste|2019-09-10 19:18:15|
  | [#255](https://github.com/JumpMind/openpos-framework/pull/255) | Don&#39;t save last dialog if the dialog is closable |  |Chris Henson|2019-09-10 19:41:31|
  | [#256](https://github.com/JumpMind/openpos-framework/pull/256) | Remove deprecated projects that aren&#39;t really being used |  |Chris Henson|2019-09-11 13:57:37|
  | [#258](https://github.com/JumpMind/openpos-framework/pull/258) | Make the session timer more robust by using spring&#39;s infrastructure |  |Chris Henson|2019-09-11 16:40:16|
  | [#261](https://github.com/JumpMind/openpos-framework/pull/261) | Trim sql to avoid blank lines in the log file |  |Chris Henson|2019-09-12 15:51:49|
  | [#263](https://github.com/JumpMind/openpos-framework/pull/263) | Convert AbstractTypeCode to String to place nicely with dbSession.fin… |  |Chris Henson|2019-09-12 20:01:05|
  | [#267](https://github.com/JumpMind/openpos-framework/pull/267) | Log a UUID called sessionId in the client context so client and server can be correlated |  |Chris Henson|2019-09-13 19:49:10|
  | [#268](https://github.com/JumpMind/openpos-framework/pull/268) | Make sure to check for selectionChangedAction in onItemListChange not… |  |Klementina S. Chirico|2019-09-13 19:49:35|
  | [#271](https://github.com/JumpMind/openpos-framework/pull/271) | Fix keybinding on sale and return screen |  |Chris Henson|2019-09-16 15:44:08|
  | [#272](https://github.com/JumpMind/openpos-framework/pull/272) | adjust minimum button padding |  |Dan Kaste|2019-09-16 15:45:21|
  | [#273](https://github.com/JumpMind/openpos-framework/pull/273) | Add the focus conditionally. If the back button was disabled then the focus was going to the hamburger which was awkward. |  |Chris Henson|2019-09-16 16:49:05|
  | [#274](https://github.com/JumpMind/openpos-framework/pull/274) | Feature/task list |  |Dan Kaste|2019-09-17 12:19:44|
  | [#275](https://github.com/JumpMind/openpos-framework/pull/275) | Bugfix/allow pre selected items on selection list screen |  |Klementina S. Chirico|2019-09-17 12:24:14|
  | [#280](https://github.com/JumpMind/openpos-framework/pull/280) | selection-list should only set the selectedItems or selectedItem if t… |  |Klementina S. Chirico|2019-09-18 16:08:02|
  | [#281](https://github.com/JumpMind/openpos-framework/pull/281) | Flushing out payment gateway api |  |Chris Henson|2019-09-19 14:01:57|
  | [#282](https://github.com/JumpMind/openpos-framework/pull/282) | move serrated edge to theme and create a mixin version |  |Dan Kaste|2019-09-19 18:38:42|
  | [#283](https://github.com/JumpMind/openpos-framework/pull/283) | icon didn&#39;t size properly for smaller that 24x24 |  |Dan Kaste|2019-09-25 12:31:49|
  | [#284](https://github.com/JumpMind/openpos-framework/pull/284) | Feature/task list style |  |Dan Kaste|2019-09-25 12:31:17|
  | [#289](https://github.com/JumpMind/openpos-framework/issues/289) | Should the header icon be to the left of the header title for a dialog header? |  |Klementina S. Chirico|2019-09-25 17:19:20|
  | [#291](https://github.com/JumpMind/openpos-framework/pull/291) | Add TimeoutException to openpos-util |  |Klementina S. Chirico|2019-09-24 19:46:29|
  | [#292](https://github.com/JumpMind/openpos-framework/pull/292) | Add event publishing and subscribing to the architecture |  |Chris Henson|2019-09-26 16:09:13|
  | [#294](https://github.com/JumpMind/openpos-framework/pull/294) | Handle multiple theme classes |  |Dan Kaste|2019-09-25 17:10:26|
  | [#295](https://github.com/JumpMind/openpos-framework/pull/295) | Feature/after transition |  |Dan Kaste|2019-09-25 16:35:32|
  | [#296](https://github.com/JumpMind/openpos-framework/pull/296) | Bugfix/dialog header icon alignment fix |  |Klementina S. Chirico|2019-09-25 17:19:20|
  | [#298](https://github.com/JumpMind/openpos-framework/pull/298) | feature/usb-printing-support |  |mmichalek|2019-09-26 19:38:37|
  | [#299](https://github.com/JumpMind/openpos-framework/pull/299) | add console scanner for testing |  |Dan Kaste|2019-09-26 14:45:22|
  | [#300](https://github.com/JumpMind/openpos-framework/pull/300) | Feature/override material theme |  |Dan Kaste|2019-09-26 19:50:27|
  | [#301](https://github.com/JumpMind/openpos-framework/pull/301) | Feature/price checker |  |Dan Kaste|2019-09-26 21:13:14|
  | [#303](https://github.com/JumpMind/openpos-framework/pull/303) | Feature/cash drawer support |  |mmichalek|2019-09-27 12:25:17|
  | [#304](https://github.com/JumpMind/openpos-framework/pull/304) | Prompt button doesn&#39;t work |  |Chris Henson|2019-09-27 17:30:10|
  | [#305](https://github.com/JumpMind/openpos-framework/pull/305) | Use imageUrl branch in selection list |  |Chris Henson|2019-09-27 17:29:52|
  | [#306](https://github.com/JumpMind/openpos-framework/pull/306) | Change the disconnected text from &quot;Reconnecting&quot; to &quot;Connecting&quot; |  |Chris Henson|2019-09-30 13:47:46|
  | [#307](https://github.com/JumpMind/openpos-framework/pull/307) | make icons resize with different media sizes |  |Dan Kaste|2019-09-30 18:49:20|
  | [#308](https://github.com/JumpMind/openpos-framework/pull/308) | update font sizes and color |  |Dan Kaste|2019-09-30 18:50:55|
  | [#309](https://github.com/JumpMind/openpos-framework/pull/309) | fix materials toggle button to be responsive to the font size |  |Dan Kaste|2019-09-30 18:50:37|
  | [#310](https://github.com/JumpMind/openpos-framework/pull/310) | make checkboxes use responsive icons |  |Dan Kaste|2019-10-01 12:39:38|
  | [#311](https://github.com/JumpMind/openpos-framework/pull/311) | fix a bug with the bottom of the serrated edge |  |Dan Kaste|2019-10-01 12:39:51|
  | [#312](https://github.com/JumpMind/openpos-framework/pull/312) | Need to set the current state manager when processing an event |  |Chris Henson|2019-10-01 16:27:00|
  | [#313](https://github.com/JumpMind/openpos-framework/pull/313) | Feature/mobile footer |  |Dan Kaste|2019-10-01 16:40:05|
  | [#314](https://github.com/JumpMind/openpos-framework/pull/314) | transition steps should not be singletons.  this probably isn&#39;t the final solution |  |Chris Henson|2019-10-01 19:12:14|
  | [#315](https://github.com/JumpMind/openpos-framework/pull/315) | add rounded input component |  |Dan Kaste|2019-10-01 16:47:09|
  | [#316](https://github.com/JumpMind/openpos-framework/pull/316) | add notifications panel |  |Dan Kaste|2019-10-01 17:27:33|
  | [#317](https://github.com/JumpMind/openpos-framework/pull/317) | Feature/success color |  |Dan Kaste|2019-10-01 17:37:47|
  | [#318](https://github.com/JumpMind/openpos-framework/pull/318) | add Serializable |  |Dan Kaste|2019-10-01 17:46:02|
  | [#319](https://github.com/JumpMind/openpos-framework/pull/319) | fix import path in LocationOverrideDialogComponent |  |Dan Kaste|2019-10-01 17:52:38|
  | [#320](https://github.com/JumpMind/openpos-framework/pull/320) | style up the mat toggle button |  |Dan Kaste|2019-10-01 18:34:16|
  | [#321](https://github.com/JumpMind/openpos-framework/pull/321) | Don&#39;t throw an error when a keybind is null |  |maxwellpettit|2019-10-01 18:25:39|
  | [#322](https://github.com/JumpMind/openpos-framework/pull/322) | add icons for orders |  |Dan Kaste|2019-10-01 18:40:33|
  | [#323](https://github.com/JumpMind/openpos-framework/pull/323) | Fix buttons too small in some cases |  |Dan Kaste|2019-10-01 21:06:05|
  | [#324](https://github.com/JumpMind/openpos-framework/pull/324) | Feature/self checkout |  |maxwellpettit|2019-10-02 20:04:48|
  | [#325](https://github.com/JumpMind/openpos-framework/pull/325) | Core support for better responsive styling |  |Dan Kaste|2019-10-03 20:26:09|
  | [#327](https://github.com/JumpMind/openpos-framework/pull/327) | Include src/test/groovy as an intellij test folder so you can run groovy tests |  |Chris Henson|2019-10-03 16:11:47|
  | [#329](https://github.com/JumpMind/openpos-framework/pull/329) | put logo on bacon-strip component |  |Klementina S. Chirico|2019-10-03 19:47:32|
  | [#330](https://github.com/JumpMind/openpos-framework/pull/330) | Make rest request/response logging configurable in application.yml |  |Chris Henson|2019-10-04 14:26:18|
  | [#331](https://github.com/JumpMind/openpos-framework/pull/331) | add some more common styles and the bottom sheet: |  |Dan Kaste|2019-10-04 21:03:24|
  | [#334](https://github.com/JumpMind/openpos-framework/pull/334) | Change the default timeout to 2 seconds.  Application become unusable with a default of 30. |  |Chris Henson|2019-10-07 13:28:36|
  | [#335](https://github.com/JumpMind/openpos-framework/pull/335) | Add setting to cache remote content for performance |  |Chris Henson|2019-10-07 13:28:21|
  | [#336](https://github.com/JumpMind/openpos-framework/pull/336) | Added a new openpos.general category to group parameters that didn&#39;t previously have a home |  |Chris Henson|2019-10-07 21:34:59|
  | [#337](https://github.com/JumpMind/openpos-framework/pull/337) | Move jsonIncludeNulls to a different section of configuration |  |Chris Henson|2019-10-08 12:09:18|
  | [#338](https://github.com/JumpMind/openpos-framework/pull/338) | Feature/customer display |  |Klementina S. Chirico|2019-10-08 16:53:52|
  | [#340](https://github.com/JumpMind/openpos-framework/pull/340) | Since the selectedItem/selectedItems is being updated every time scre… |  |Klementina S. Chirico|2019-10-09 21:10:45|
  | [#341](https://github.com/JumpMind/openpos-framework/issues/341) | If a message comes in right after a dialog message the dialog&#39;s screen parts fail to initialize with screenData because the dialog content replays the last message and not the dialog message it was expecting to repay.  Results in an undefined screenData. |  |Dan Kaste|2019-10-15 12:28:05|
  | [#342](https://github.com/JumpMind/openpos-framework/pull/342) | Feature/responsive class on self checkout options part |  |Klementina S. Chirico|2019-10-10 14:49:53|
  | [#343](https://github.com/JumpMind/openpos-framework/pull/343) | Feature/bacon drawer |  |Dan Kaste|2019-10-10 20:29:08|
  | [#344](https://github.com/JumpMind/openpos-framework/pull/344) | Use a regular dynamic form part if auto complete not enabled |  |maxwellpettit|2019-10-10 19:17:21|
  | [#346](https://github.com/JumpMind/openpos-framework/pull/346) | allow swipe gesture to open the bacon drawer |  |Dan Kaste|2019-10-11 13:42:19|
  | [#347](https://github.com/JumpMind/openpos-framework/pull/347) | Feature/customer display updates |  |Klementina S. Chirico|2019-10-12 01:27:05|
  | [#348](https://github.com/JumpMind/openpos-framework/pull/348) | Fix bug #341 |  |Dan Kaste|2019-10-15 12:28:05|
  | [#349](https://github.com/JumpMind/openpos-framework/pull/349) | Feature/personalization classes |  |Dan Kaste|2019-10-15 19:44:31|
  | [#352](https://github.com/JumpMind/openpos-framework/pull/352) | Feature/new sale screen |  |maxwellpettit|2019-10-17 20:28:25|
  | [#355](https://github.com/JumpMind/openpos-framework/issues/355) | allow the jdbc query timeout to be configured in the persistence layer.  might need a couple different categories of timeouts (ie user facing versus back end processes) |  |Chris Henson|2019-10-22 18:24:45|
  | [#356](https://github.com/JumpMind/openpos-framework/pull/356) | Bacon Strip updates |  |Dan Kaste|2019-10-21 19:15:45|
  | [#359](https://github.com/JumpMind/openpos-framework/pull/359) | Make the query timeout and the fetch size configurable via yaml or properties.  Fix #355 |  |Chris Henson|2019-10-22 18:24:45|
  | [#360](https://github.com/JumpMind/openpos-framework/pull/360) | Feature/media breakpoints |  |maxwellpettit|2019-10-23 14:17:10|
  | [#361](https://github.com/JumpMind/openpos-framework/pull/361) | Add refresh annotation for states |  |Dan Kaste|2019-10-23 17:33:12|
  | [#362](https://github.com/JumpMind/openpos-framework/pull/362) | If a superclass with @TableDef defined has an @IndexDef defined we attempt to create the index on the subclass |  |Chris Henson|2019-10-23 14:09:52|
  | [#363](https://github.com/JumpMind/openpos-framework/pull/363) | Feature/item carousel |  |Klementina S. Chirico|2019-10-23 21:06:46|
  | [#365](https://github.com/JumpMind/openpos-framework/pull/365) | check that customer.icon, customer.label, customer.id exists before d… |  |Klementina S. Chirico|2019-10-24 14:52:49|
  | [#371](https://github.com/JumpMind/openpos-framework/pull/371) | add animation to carousel component to slide items left and right |  |Klementina S. Chirico|2019-10-25 16:50:05|
  | [#380](https://github.com/JumpMind/openpos-framework/pull/380) | Add support for sending data to the client in chucks apart from the screen |  |Dan Kaste|2019-10-29 20:06:34|
  | [#381](https://github.com/JumpMind/openpos-framework/pull/381) | Feature/update return component |  |Klementina S. Chirico|2019-10-30 15:00:25|
  | [#383](https://github.com/JumpMind/openpos-framework/pull/383) | Feature/collapsable sale item cards |  |Klementina S. Chirico|2019-10-31 14:02:58|
  | [#384](https://github.com/JumpMind/openpos-framework/pull/384) | Feature/mobile sale |  |maxwellpettit|2019-11-04 16:41:08|
  | [#386](https://github.com/JumpMind/openpos-framework/pull/386) | Feature/document carousel |  |Klementina S. Chirico|2019-10-31 15:36:58|
  | [#387](https://github.com/JumpMind/openpos-framework/pull/387) | Feature/infinite scroll |  |Dan Kaste|2019-10-31 19:08:51|
  | [#388](https://github.com/JumpMind/openpos-framework/pull/388) | Move the SymDS module to openpos-framework and upgrade SymmetricDS to the latest 3.9 version |  |Chris Henson|2019-10-31 18:36:33|
  | [#389](https://github.com/JumpMind/openpos-framework/pull/389) | Feature/image |  |Dan Kaste|2019-11-01 20:12:41|
  | [#390](https://github.com/JumpMind/openpos-framework/pull/390) | Feature/item card more subtle hover |  |Klementina S. Chirico|2019-11-01 13:20:28|
  | [#392](https://github.com/JumpMind/openpos-framework/pull/392) | fix bug with using templates inside of a virtual scroll |  |Dan Kaste|2019-11-01 20:12:23|
  | [#393](https://github.com/JumpMind/openpos-framework/pull/393) | For some reason the injection of the responsive map doesn&#39;t happen soon enough in the media service and results in errors | [bug] |Chris Henson|2019-11-04 14:21:12|
  | [#395](https://github.com/JumpMind/openpos-framework/pull/395) | Feature/update return component |  |Klementina S. Chirico|2019-11-04 17:30:52|
  | [#397](https://github.com/JumpMind/openpos-framework/pull/397) | Feature/updates and include returns info mobile items |  |Klementina S. Chirico|2019-11-05 13:07:32|
  | [#400](https://github.com/JumpMind/openpos-framework/pull/400) | Allow the symds web context to be overridden |  |Chris Henson|2019-11-05 21:17:00|
  | [#401](https://github.com/JumpMind/openpos-framework/pull/401) | Feature/expanding search |  |maxwellpettit|2019-11-06 15:20:02|
  | [#403](https://github.com/JumpMind/openpos-framework/pull/403) | Fix ServerLogger to allow logging of objects with circular refs |  |Jason Mihalick|2019-11-06 19:37:40|
  | [#404](https://github.com/JumpMind/openpos-framework/pull/404) | Provide a way to configure the timestamp format used in client logging |  |Jason Mihalick|2019-11-06 19:38:07|
  | [#405](https://github.com/JumpMind/openpos-framework/pull/405) | Feature/returns mobile |  |Klementina S. Chirico|2019-11-06 20:15:46|
  | [#406](https://github.com/JumpMind/openpos-framework/pull/406) | Bring back inverse-text which went missing sometime after release/0.4 |  |Jason Mihalick|2019-11-06 19:39:24|
  | [#407](https://github.com/JumpMind/openpos-framework/pull/407) | Add a setting to ignore @TableDef annotations in the super classes |  |Chris Henson|2019-11-06 20:58:17|
  | [#408](https://github.com/JumpMind/openpos-framework/pull/408) | Feature/cleanup bacon strip |  |Klementina S. Chirico|2019-11-07 16:20:48|
  | [#410](https://github.com/JumpMind/openpos-framework/pull/410) | Feature/cleanup sale messages |  |Klementina S. Chirico|2019-11-07 21:52:28|

## 0.4.10 
Released on: 2019-11-11 13:54:11

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|

## 0.4.6-urbn 
Released on: 2019-10-28 15:37:53

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#366](https://github.com/JumpMind/openpos-framework/pull/366) | Block disabled actions in session service |  |maxwellpettit|2019-10-28 15:15:36|
  | [#367](https://github.com/JumpMind/openpos-framework/pull/367) | Populate skip button from actionItem to translate text |  |maxwellpettit|2019-10-28 15:15:54|

## 0.4.9 
Released on: 2019-10-23 18:09:50

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#351](https://github.com/JumpMind/openpos-framework/pull/351) | Add configuration for setting websocket limit and handle errors gracefully |  |Dan Kaste|2019-10-23 16:31:23|

## 0.4.7 
Released on: 2019-10-07 14:20:23

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#334](https://github.com/JumpMind/openpos-framework/pull/334) | Change the default timeout to 2 seconds.  Application become unusable with a default of 30. |  |Chris Henson|2019-10-07 14:17:28|
  | [#335](https://github.com/JumpMind/openpos-framework/pull/335) | Add setting to cache remote content for performance |  |Chris Henson|2019-10-07 14:17:15|

## 0.4.6 
Released on: 2019-10-07 13:53:26

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|

## 0.5.7 
Released on: 2019-10-05 22:35:28

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#267](https://github.com/JumpMind/openpos-framework/pull/267) | Log a UUID called sessionId in the client context so client and server can be correlated |  |Chris Henson|2019-09-30 18:47:14|
  | [#288](https://github.com/JumpMind/openpos-framework/pull/288) | Set the max rows to the max results and set auto commit to false so that the entire result isn&#39;t returned |  |Chris Henson|2019-09-23 18:04:09|
  | [#306](https://github.com/JumpMind/openpos-framework/pull/306) | Change the disconnected text from &quot;Reconnecting&quot; to &quot;Connecting&quot; |  |Chris Henson|2019-09-30 18:46:44|

## 0.4.5 
Released on: 2019-09-30 12:58:53

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#241](https://github.com/JumpMind/openpos-framework/pull/241) | Add support for property crawling of top level String[] property |  |Jason Mihalick|2019-09-20 16:26:17|
  | [#250](https://github.com/JumpMind/openpos-framework/pull/250) | Added missing AlphanumericText formatter, removed old onBarcodePaste |  |Eric Amiralian|2019-09-26 12:15:49|
  | [#264](https://github.com/JumpMind/openpos-framework/pull/264) | Adding support for status strip on Home Page Component |  |stevencarley|2019-09-13 14:05:26|
  | [#265](https://github.com/JumpMind/openpos-framework/pull/265) | Add comment for restore focus |  |maxwellpettit|2019-09-13 14:52:50|
  | [#269](https://github.com/JumpMind/openpos-framework/pull/269) | Check if dialog is closeable on screen refresh |  |maxwellpettit|2019-09-13 19:36:07|
  | [#270](https://github.com/JumpMind/openpos-framework/pull/270) | Making kebab menu selections full line clickable instead of text only |  |stevencarley|2019-09-16 15:24:54|
  | [#277](https://github.com/JumpMind/openpos-framework/pull/277) | Adding min-height to drawer-button, older version of chrome rendering… |  |stevencarley|2019-09-17 16:29:22|
  | [#278](https://github.com/JumpMind/openpos-framework/pull/278) | Expanded clickable area for Transaction Menu in Sale Component |  |Eric Amiralian|2019-09-17 20:19:59|
  | [#285](https://github.com/JumpMind/openpos-framework/pull/285) | Paginate &gt; 15 homescreen menu items |  |Eric Amiralian|2019-09-27 14:27:39|
  | [#286](https://github.com/JumpMind/openpos-framework/pull/286) | Add loading dialog delay to client configuration |  |maxwellpettit|2019-09-20 15:20:17|
  | [#287](https://github.com/JumpMind/openpos-framework/pull/287) | Feature/banner screen part |  |stevencarley|2019-09-23 15:50:30|
  | [#290](https://github.com/JumpMind/openpos-framework/pull/290) | Test occasionally failing on CI with multiple invocations of error handler |  |stevencarley|2019-09-24 15:32:41|

## 0.5.6 
Released on: 2019-09-17 17:18:54

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#279](https://github.com/JumpMind/openpos-framework/pull/279) | Unblock actions when a toast message is received |  |Chris Henson|2019-09-17 16:54:20|

## 0.4.4 
Released on: 2019-09-12 19:53:00

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#260](https://github.com/JumpMind/openpos-framework/pull/260) | Feature/bacon strip back button |  |stevencarley|2019-09-12 15:58:49|
  | [#262](https://github.com/JumpMind/openpos-framework/pull/262) | Get an updated element reference when restoring focus |  |maxwellpettit|2019-09-12 17:25:14|

## 0.4.3 
Released on: 2019-09-12 14:58:45

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#245](https://github.com/JumpMind/openpos-framework/pull/245) | Handle pre-formatted eu values in money formatter |  |maxwellpettit|2019-09-04 18:51:46|
  | [#249](https://github.com/JumpMind/openpos-framework/pull/249) | Adding support for setting startAt for dynamic-date-form-field |  |stevencarley|2019-09-06 12:58:14|
  | [#253](https://github.com/JumpMind/openpos-framework/pull/253) | Don&#39;t save last dialog if it is closable |  |maxwellpettit|2019-09-10 16:02:55|
  | [#257](https://github.com/JumpMind/openpos-framework/pull/257) | Expose method to override validators |  |maxwellpettit|2019-09-11 16:30:56|
  | [#259](https://github.com/JumpMind/openpos-framework/pull/259) | Add support for logging of uncaught exceptions. |  |Jason Mihalick|2019-09-12 13:56:50|

## 0.5.5 
Released on: 2019-09-05 14:07:44

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#247](https://github.com/JumpMind/openpos-framework/pull/247) | Add feature to allow a uimessage to be disabled |  |Chris Henson|2019-09-05 14:04:25|

## 0.5.4 
Released on: 2019-09-04 18:24:49

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#211](https://github.com/JumpMind/openpos-framework/pull/211) | Introduce a pluggable logging framework uses console methods as it&#39;s api |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#215](https://github.com/JumpMind/openpos-framework/pull/215) | Build openpos-client-personalize via gradle |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#218](https://github.com/JumpMind/openpos-framework/issues/218) | Layout with bottom components (total, tax, etc) and scrolling is funky on sale screen and return screen when there are &gt; 7 or 8 items |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#228](https://github.com/JumpMind/openpos-framework/pull/228) | add UIMessage version of dynamic form screen and tests |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#231](https://github.com/JumpMind/openpos-framework/pull/231) | Feature/client context |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#233](https://github.com/JumpMind/openpos-framework/pull/233) | Bugfix/cleanup form functions |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#235](https://github.com/JumpMind/openpos-framework/pull/235) | Feature/incident reporting |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#239](https://github.com/JumpMind/openpos-framework/pull/239) | Client-side ActionItem implementation |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#240](https://github.com/JumpMind/openpos-framework/pull/240) | Baconator builds BaconStrips |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#243](https://github.com/JumpMind/openpos-framework/pull/243) | Bugfix/selection list disabled only item still selected |  |Klementina S. Chirico|2019-09-04 16:03:20|
  | [#244](https://github.com/JumpMind/openpos-framework/pull/244) | Bugfix/selectable item list default select on disabled |  |Klementina S. Chirico|2019-09-04 18:21:48|

## 0.5.2 
Released on: 2019-08-28 15:41:59

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|

## 0.4.2 
Released on: 2019-08-28 15:15:09

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#180](https://github.com/JumpMind/openpos-framework/pull/180) | Round to nearest second before splitting and add tests |  |Dan Kaste|2019-08-01 15:29:41|
  | [#181](https://github.com/JumpMind/openpos-framework/pull/181) | Date Prompt Focus and Text Size |  |Dan Kaste|2019-08-01 17:25:41|
  | [#182](https://github.com/JumpMind/openpos-framework/pull/182) | Feature/personalize app |  |maxwellpettit|2019-08-02 14:49:53|
  | [#183](https://github.com/JumpMind/openpos-framework/pull/183) | ActionItem confirmation message constructor now sets title |  |Eric Amiralian|2019-08-02 12:42:31|
  | [#185](https://github.com/JumpMind/openpos-framework/pull/185) | Adding 3 Icons for a client project |  |Eric Amiralian|2019-08-05 20:50:32|
  | [#186](https://github.com/JumpMind/openpos-framework/pull/186) | Added Icons section &amp; available icons to docs |  |Eric Amiralian|2019-08-06 12:24:29|
  | [#187](https://github.com/JumpMind/openpos-framework/pull/187) | Vertically center unlock icon in status bar |  |Eric Amiralian|2019-08-06 12:24:40|
  | [#188](https://github.com/JumpMind/openpos-framework/pull/188) | The version is not show up in dev tools |  |Chris Henson|2019-08-06 14:24:49|
  | [#190](https://github.com/JumpMind/openpos-framework/pull/190) | Fixed Improper Null check that missed empty strings |  |Eric Amiralian|2019-08-06 18:32:50|
  | [#192](https://github.com/JumpMind/openpos-framework/pull/192) | Subscribe to the scanner service in the scan something component |  |maxwellpettit|2019-08-07 13:54:13|
  | [#195](https://github.com/JumpMind/openpos-framework/pull/195) | Add @BeforeAction annotation in support of invoking behavior before an action is handled |  |Jason Mihalick|2019-08-08 19:52:39|
  | [#197](https://github.com/JumpMind/openpos-framework/pull/197) | DynamicFormUIMessage |  |stevencarley|2019-08-12 16:40:23Eric Amiralian|2019-08-09 16:46:06|
  | [#198](https://github.com/JumpMind/openpos-framework/pull/198) | Add logging of the legacy dialog resource id for troubleshooting |  |Jason Mihalick|2019-08-09 15:04:36|
  | [#200](https://github.com/JumpMind/openpos-framework/pull/200) | Add TRACE level logging before and after state injections are done |  |Jason Mihalick|2019-08-12 01:22:55|
  | [#201](https://github.com/JumpMind/openpos-framework/pull/201) | Revert &quot;DynamicFormUIMessage&quot; |  |stevencarley|2019-08-12 16:40:23|
  | [#204](https://github.com/JumpMind/openpos-framework/pull/204) | Run OnDepart before state transition, add more TRACE logging to injection |  |Jason Mihalick|2019-08-12 21:52:00|
  | [#207](https://github.com/JumpMind/openpos-framework/pull/207) | Support EU currency formatting |  |maxwellpettit|2019-08-13 15:54:08|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |maxwellpettit|2019-08-19 17:23:56|
  | [#216](https://github.com/JumpMind/openpos-framework/pull/216) | Only add google autocomplete listener once to improve performance |  |maxwellpettit|2019-08-19 21:17:21|
  | [#220](https://github.com/JumpMind/openpos-framework/pull/220) | Return the entire list when filtering with an empty string |  |maxwellpettit|2019-08-21 15:57:52|
  | [#221](https://github.com/JumpMind/openpos-framework/pull/221) | Use searchable poptarts for auto complete address screens |  |maxwellpettit|2019-08-23 13:23:21maxwellpettit|2019-08-21 17:35:19|
  | [#224](https://github.com/JumpMind/openpos-framework/pull/224) | Add validator for disallowing a field to contain all whitespace |  |Jason Mihalick|2019-08-22 19:45:42|
  | [#225](https://github.com/JumpMind/openpos-framework/pull/225) | Preventing disabled links from submitting actions |  |stevencarley|2019-08-22 18:44:20|
  | [#227](https://github.com/JumpMind/openpos-framework/pull/227) | Fix performance issue with Wedge Scanner |  |Dan Kaste|2019-08-23 11:22:15|
  | [#229](https://github.com/JumpMind/openpos-framework/pull/229) | Use DefaultObjectMapper in ScreenService |  |Jason Mihalick|2019-08-23 14:02:13|
  | [#230](https://github.com/JumpMind/openpos-framework/pull/230) | Change StateManager to make use of IErrorHandler if one is set |  |Jason Mihalick|2019-08-26 14:55:53|

## 0.5.1 
Released on: 2019-08-20 19:23:04

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#214](https://github.com/JumpMind/openpos-framework/pull/214) | Add support for disabling the language icons on language selector |  |Klementina S. Chirico|2019-08-20 18:10:03|
  | [#215](https://github.com/JumpMind/openpos-framework/pull/215) | Build openpos-client-personalize via gradle |  |Klementina S. Chirico|2019-08-20 18:10:03|
  | [#217](https://github.com/JumpMind/openpos-framework/pull/217) | Bugfix/scanner disabled on choose options destry |  |Klementina S. Chirico|2019-08-20 18:10:03|

## 0.5.0 
Released on: 2019-08-19 12:33:09

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#131](https://github.com/JumpMind/openpos-framework/pull/131) | bugfix/selectable-item-multiselect-index | [bug, client] |Klementina S. Chirico|2019-07-15 17:36:21|
  | [#136](https://github.com/JumpMind/openpos-framework/pull/136) | Enable annotation processing for intellij automatically | [build] |Chris Henson|2019-07-17 13:33:09|
  | [#142](https://github.com/JumpMind/openpos-framework/pull/142) | Use build version as the module version |  |Chris Henson|2019-07-18 18:36:22|
  | [#152](https://github.com/JumpMind/openpos-framework/pull/152) | Restyling Sausage Links |  |Eric Amiralian|2019-07-24 14:12:13|
  | [#157](https://github.com/JumpMind/openpos-framework/pull/157) | Add new method to dbSession.  findFirstByFields |  |Chris Henson|2019-07-24 19:24:16|
  | [#160](https://github.com/JumpMind/openpos-framework/pull/160) | Drop deprecated screens and refactor Actions |  |Dan Kaste|2019-08-08 22:29:13|
  | [#161](https://github.com/JumpMind/openpos-framework/pull/161) | Feature/sell item style for loyalty |  |Klementina S. Chirico|2019-07-29 13:28:44|
  | [#171](https://github.com/JumpMind/openpos-framework/pull/171) | Feature/dynamic receipt card |  |Klementina S. Chirico|2019-07-30 16:53:42|
  | [#173](https://github.com/JumpMind/openpos-framework/pull/173) | JavaPOS driver and printer support for receipts |  |mmichalek|2019-07-30 20:49:04|
  | [#174](https://github.com/JumpMind/openpos-framework/pull/174) | Add support for getting a map of objects out of ctx_config |  |Jared|2019-07-31 14:05:56|
  | [#184](https://github.com/JumpMind/openpos-framework/pull/184) | Remove unused method from ConfigApplicator |  |Jared|2019-08-05 13:01:11|
  | [#188](https://github.com/JumpMind/openpos-framework/pull/188) | The version is not show up in dev tools |  |Chris Henson|2019-08-06 14:22:20|
  | [#189](https://github.com/JumpMind/openpos-framework/pull/189) | If no type is configured then don&#39;t send the type at all.  Provide keyedActionName to be able to delineate between scan and keyed |  |Chris Henson|2019-08-06 17:24:20|
  | [#191](https://github.com/JumpMind/openpos-framework/pull/191) | Pass the raw type in scan data for debugging new scanners |  |Chris Henson|2019-08-06 19:14:06|
  | [#193](https://github.com/JumpMind/openpos-framework/pull/193) | Feature/#24 convert to screen parts |  |Dan Kaste|2019-08-08 13:14:24|
  | [#194](https://github.com/JumpMind/openpos-framework/pull/194) | Feature/remove config managers and configuration properties |  |Jared|2019-08-08 13:14:03|
  | [#196](https://github.com/JumpMind/openpos-framework/pull/196) | Update StateManager so that a Cancel TransitionResult will send a &quot;Tr… |  |Klementina S. Chirico|2019-08-08 20:12:50|
  | [#202](https://github.com/JumpMind/openpos-framework/pull/202) | Bugfix/client config selector permutations |  |Klementina S. Chirico|2019-08-12 17:14:35|
  | [#203](https://github.com/JumpMind/openpos-framework/pull/203) | Feature/cleanup sausage link alignment |  |Klementina S. Chirico|2019-08-13 13:49:30|
  | [#205](https://github.com/JumpMind/openpos-framework/pull/205) | add install to grunt so it creates version.ts |  |Dan Kaste|2019-08-13 13:48:14|
  | [#206](https://github.com/JumpMind/openpos-framework/pull/206) | Remove transient from ActionItem autoAssignEnabled |  |Dan Kaste|2019-08-13 14:22:39|
  | [#208](https://github.com/JumpMind/openpos-framework/pull/208) | Add a module api specific to modules backed by an RDBMS |  |Chris Henson|2019-08-13 17:55:34|
  | [#209](https://github.com/JumpMind/openpos-framework/pull/209) | Feature/expose rdbms modules |  |Chris Henson|2019-08-13 18:07:25|
  | [#212](https://github.com/JumpMind/openpos-framework/pull/212) | add @AssignKeyBindings to ItemDetailUIMessage so it will auto-create … |  |Klementina S. Chirico|2019-08-16 20:00:24|
  | [#213](https://github.com/JumpMind/openpos-framework/pull/213) | add @AssignKeyBindings to TenderUIMessage so it will auto-create keyb… |  |Klementina S. Chirico|2019-08-16 20:00:39|
  | [#24](https://github.com/JumpMind/openpos-framework/issues/24) | Make main floating action buttons the primary color |  |Dan Kaste|2019-08-08 13:14:24|

## 0.4.1 
Released on: 2019-08-01 15:05:32

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#131](https://github.com/JumpMind/openpos-framework/pull/131) | bugfix/selectable-item-multiselect-index | [bug, client] |Klementina S. Chirico|2019-07-15 17:30:50|
  | [#133](https://github.com/JumpMind/openpos-framework/pull/133) | Revert CloseDialogMessage as it allows double clicking dialog buttons |  |Chris Henson|2019-07-16 17:24:33|
  | [#134](https://github.com/JumpMind/openpos-framework/pull/134) | Add UK date formatter |  |Chris Henson|2019-07-16 17:37:52|
  | [#135](https://github.com/JumpMind/openpos-framework/pull/135) | Default no locale phone formatter to numeric |  |Chris Henson|2019-07-16 17:40:19|
  | [#137](https://github.com/JumpMind/openpos-framework/pull/137) | Focus service and restore focus when closing kebab menu |  |stevencarley|2019-07-17 19:11:06|
  | [#139](https://github.com/JumpMind/openpos-framework/pull/139) | Adding NavListComponent to entryComponents |  |stevencarley|2019-07-18 12:09:08|
  | [#140](https://github.com/JumpMind/openpos-framework/pull/140) | Add device_unknown icon from material |  |Jason Mihalick|2019-07-18 14:32:19|
  | [#141](https://github.com/JumpMind/openpos-framework/pull/141) | Price checker screens get slower over time.  Subscriptions were not being cleaned up.   | [bug, client] |Chris Henson|2019-07-18 15:30:58|
  | [#143](https://github.com/JumpMind/openpos-framework/pull/143) | Making SystemStatusDialogComponent an entryComponent |  |stevencarley|2019-07-19 13:48:30|
  | [#144](https://github.com/JumpMind/openpos-framework/pull/144) | Fix dialog screen dialog race condition |  |Chris Henson|2019-07-19 19:30:41|
  | [#146](https://github.com/JumpMind/openpos-framework/pull/146) | Refresh status bar control when data changes |  |maxwellpettit|2019-07-23 13:51:25|
  | [#147](https://github.com/JumpMind/openpos-framework/pull/147) | Only send selected indexes with select action to improve performance |  |maxwellpettit|2019-07-23 14:52:40|
  | [#148](https://github.com/JumpMind/openpos-framework/pull/148) | Adding system status to home component |  |stevencarley|2019-07-23 16:27:18|
  | [#149](https://github.com/JumpMind/openpos-framework/pull/149) | Using ViewChildren decorator in FormComponent results in empty list of children DynamicFormFieldComponents | [bug] |Jason Mihalick|2019-07-23 16:28:35|
  | [#152](https://github.com/JumpMind/openpos-framework/pull/152) | Restyling Sausage Links |  |Eric Amiralian|2019-07-25 12:11:21|
  | [#154](https://github.com/JumpMind/openpos-framework/pull/154) | Add method to convert a string to a FieldInputType |  |Jason Mihalick|2019-07-24 17:55:39|
  | [#155](https://github.com/JumpMind/openpos-framework/pull/155) | Add logging of component name being placed in dialog |  |Jason Mihalick|2019-07-24 18:08:31|
  | [#156](https://github.com/JumpMind/openpos-framework/pull/156) | Allow for subclasses to override translation of dialog titles |  |Jason Mihalick|2019-07-24 19:25:17|
  | [#158](https://github.com/JumpMind/openpos-framework/pull/158) | Sausage Link Spacing in Sell Component |  |Eric Amiralian|2019-07-25 13:54:00|
  | [#159](https://github.com/JumpMind/openpos-framework/pull/159) | Add formatter support for fields that allow any character but numerics |  |Jason Mihalick|2019-07-26 20:36:43|
  | [#163](https://github.com/JumpMind/openpos-framework/pull/163) | Moving Wait Component to temporarily-shared-screens.module |  |Eric Amiralian|2019-07-26 19:03:00|
  | [#167](https://github.com/JumpMind/openpos-framework/pull/167) | Switch Sausage Links to secondary-button for style overrides |  |Eric Amiralian|2019-07-30 14:07:14|
  | [#168](https://github.com/JumpMind/openpos-framework/pull/168) | Add support for controlling formatting of password field |  |Jason Mihalick|2019-07-30 03:01:11|
  | [#169](https://github.com/JumpMind/openpos-framework/pull/169) | Convert PromptWithInfo to screen-part and UIMessage |  |stevencarley|2019-07-30 13:26:07|
  | [#172](https://github.com/JumpMind/openpos-framework/pull/172) | Translated Screen Id |  |Dan Kaste|2019-07-30 20:52:48|
  | [#175](https://github.com/JumpMind/openpos-framework/pull/175) | Remote Content |  |Dan Kaste|2019-07-31 18:36:26|
  | [#177](https://github.com/JumpMind/openpos-framework/pull/177) | Bugfix/currency text |  |Dan Kaste|2019-08-01 12:08:21|
  | [#178](https://github.com/JumpMind/openpos-framework/pull/178) | status-bar &amp; bacon-strip now using the same format |  |Eric Amiralian|2019-08-01 13:30:24|

## 0.4.0 
Released on: 2019-07-12 18:47:38

|       | Name   | Labels | Author | Time |
|-------|--------|--------|--------|------|
  | [#107](https://github.com/JumpMind/openpos-framework/pull/107) | Feature/106 differentiate web orders | [enhancement] |Klementina Stojanovska Chirico|2019-07-04 13:44:33|
  | [#110](https://github.com/JumpMind/openpos-framework/pull/110) | Add client documentation | [architecture] |Dan Kaste|2019-07-04 13:44:11|
  | [#111](https://github.com/JumpMind/openpos-framework/pull/111) | bugfix/PriceChecker price per unit label | [bug, client] |jliao300|2019-07-08 14:04:39|
  | [#114](https://github.com/JumpMind/openpos-framework/issues/114) | Aila Scanner passes Null for UPCA barcode types | [bug] |Dan Kaste|2019-07-08 19:46:48|
  | [#115](https://github.com/JumpMind/openpos-framework/pull/115) | 114: Correctly map UPCA barcode types for Aila Scanner | [bug, client] |Dan Kaste|2019-07-08 19:46:48|
  | [#116](https://github.com/JumpMind/openpos-framework/pull/116) | added maxResults to the persist api | [enhancement, server] |Chris Henson|2019-07-09 15:12:14|
  | [#119](https://github.com/JumpMind/openpos-framework/pull/119) | IValidatorSpec implementations fail deserialization with jackson  | [bug, server] |Jason Mihalick|2019-07-09 22:52:32|
  | [#120](https://github.com/JumpMind/openpos-framework/pull/120) | 120: Ensure MaxValueValidator can be serialized, fix typo in error msg | [bug, client, server] |Jason Mihalick|2019-07-10 13:59:43|
  | [#122](https://github.com/JumpMind/openpos-framework/pull/122) | bugfix/PromoColor to BL orange | [bug] |jliao300|2019-07-11 17:43:32|
  | [#123](https://github.com/JumpMind/openpos-framework/pull/123) | Add test module to make sure we catch and sass errors in themes | [build, client] |Dan Kaste|2019-07-11 14:02:23|
  | [#124](https://github.com/JumpMind/openpos-framework/pull/124) | Don&#39;t deploy if the branch name is merge | [bug, build] |Chris Henson|2019-07-11 14:35:15|
  | [#126](https://github.com/JumpMind/openpos-framework/pull/126) | Cleanup of client build and documentation update | [build, client] |Dan Kaste|2019-07-12 14:13:30|
  | [#87](https://github.com/JumpMind/openpos-framework/pull/87) | Feature/selection list paging | [client, enhancement, server] |Klementina Stojanovska Chirico|2019-07-04 13:44:50|
