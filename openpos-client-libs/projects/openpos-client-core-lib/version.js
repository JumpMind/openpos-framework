const { version } = require(__dirname + '/package.json');
const { resolve, relative } = require('path');
const { writeFileSync } = require('fs-extra');

const revision = require('child_process')
    .execSync('git rev-parse HEAD')
    .toString().trim();

const branch = require('child_process')
.execSync('git rev-parse --abbrev-ref HEAD').toString().trim();

const versionInfo = { 'componentName': 'openpos-client-core-lib', 'version': version, 'gitHash': revision, 'buildTime': new Date().toISOString().replace(/T/, ' ').replace(/\..+/, '') , 'gitBranch': branch, 
  'buildNumber': process.env.BUILD_NUMBER, 'buildName': process.env.BUILD_NAME };

const file = resolve(__dirname, 'src', 'lib', 'version.ts');
writeFileSync(file,
    `// IMPORTANT: THIS FILE IS AUTO GENERATED! DO NOT MANUALLY EDIT OR CHECKIN!
/* tslint:disable */
export const VERSION = ${JSON.stringify(versionInfo)};
/* tslint:enable */
`, { encoding: 'utf-8' });

console.log(`Wrote version info ${versionInfo.version} to ${relative(resolve(__dirname, '..'), file)}`);