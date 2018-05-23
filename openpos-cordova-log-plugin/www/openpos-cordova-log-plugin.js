
var exec = require('cordova/exec');

const PLUGIN_NAME = 'OpenPOSCordovaLogPlugin';

var OpenPOSCordovaLogPlugin = {
  pluginId: 'openPOSCordovaLogPlugin',
  pluginName: PLUGIN_NAME,
  config: {
    buildNumber: '0'
  },

  init: function(successCallback, errorCallback) {
    // Nothing to init for this plugin
    // Make sure plugin at least has default config
    _configure(null);
    successCallback();
  },
  
  configure: function(pluginConfig) {
    return _configure(pluginConfig);
  },

  getAppVersion: function(successCallback, errorCallback) {
    exec(
      successCallback,
      errorCallback,
      PLUGIN_NAME, 
      'getAppVersion', []
    );
  },

  /** 
   * Lists the name of all the files ending with *.log in the Logs directory.
   * 
   * successCallback - a single parameter callback that will receive an
   *   array of log file names found in the Logs directory
   * errorCallback - a single parameter callback which will receive an error message if
   *   there was a failure. 
   */
  listLogFiles: function(successCallback, errorCallback ) {
    exec(
        successCallback,
        errorCallback,
        PLUGIN_NAME, 
        'listLogFiles', []
      );
  },
  
  /** 
   * If the file exists, the successCallback will return the contents
   * of the file as a string.
   * 
   * logFilename - just the name of the logfile, no path information
   * successCallback - a single parameter callback that will receive the contents of the
   *   logfile as a string
   * errorCallback - a single parameter callback which will receive an error message if
   *   there was a failure in reading the file. 
   */
  readLogFileContents: function(logFilename, successCallback, errorCallback) {
    exec(
        successCallback, 
        errorCallback,
        PLUGIN_NAME, 
        'readLogFileContents', [logFilename]
      );
  },
  
  getLogDirectoryPath: function(successCallback, errorCallback) {
    exec(
      successCallback, 
      errorCallback,
      PLUGIN_NAME, 
      'getLogDirectoryPath', []
    );
  },

  /** 
   * If the file exists, the successCallback will return the full path
   * of the file as a string.
   * 
   * logFilename - just the name of the logfile, no path information
   * successCallback - a single parameter callback that will receive the full path of the logfile
   *    as a string
   * errorCallback - a single parameter callback which will receive an error message if
   *   there was a failure in locating the file. 
   */
  getLogFilePath: function(logFilename, successCallback, errorCallback) {
    exec(
        successCallback, 
        errorCallback,
        PLUGIN_NAME, 
        'getLogFilePath', [logFilename]
      );
  },

  /** Returns the filename of the current log file via successCallback. */
  getCurrentLogFilePath: function(successCallback, errorCallback) {
    exec(
      successCallback, 
      errorCallback,
      PLUGIN_NAME, 
      'getCurrentLogFilePath', []
    );
  },

  /**
   * Provide the user with an option to share the given log file using
   * standard OS sharing mechanism.
   */
  shareLogFile: function(logFilename, successCallback, errorCallback ) {
    exec(
        successCallback, 
        errorCallback,
        PLUGIN_NAME, 
        'shareLogFile', [logFilename]
      );
  },

  /**
   * Will remove any log files that are older than OpenPOSCordovaLogPlugin.logRetentionDays via 
	 * which is set via platform preference.
   */
  purgeOldLogFiles: function(successCallback, errorCallback) {
    exec(
      successCallback, 
      errorCallback,
      PLUGIN_NAME, 
      'purgeOldLogs', []
    );
  }
};

function _configure(pluginConfig) {
  if (pluginConfig) {
    if (pluginConfig.buildNumber !== 'undefined' && pluginConfig.buildNumber) {
      OpenPOSCordovaLogPlugin.config.buildNumber = pluginConfig.buildNumber;
    }
  }
  
  exec(
    function() {},
    function(error) {},
    PLUGIN_NAME,
    'configure', [OpenPOSCordovaLogPlugin.config.buildNumber]
  );

  return true;
}

module.exports = OpenPOSCordovaLogPlugin;
