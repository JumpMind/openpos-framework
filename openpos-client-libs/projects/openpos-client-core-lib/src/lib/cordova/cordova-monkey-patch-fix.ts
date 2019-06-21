// https://weblogs.thinktecture.com/thomas/2017/02/cordova-vs-zonejs-or-why-is-angulars-document-event-listener-not-in-a-zone.html

export function CordovaMonkeyPatch() {
    'use strict';

    window.addEventListener = function() {
        EventTarget.prototype.addEventListener.apply(this, arguments);
    };

    window.removeEventListener = function() {
        EventTarget.prototype.removeEventListener.apply(this, arguments);
    };

    document.addEventListener = function() {
        EventTarget.prototype.addEventListener.apply(this, arguments);
    };

    document.removeEventListener = function() {
        EventTarget.prototype.removeEventListener.apply(this, arguments);
    };
}
