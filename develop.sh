cd openpos-assemble
./gradlew develop
cd ../openpos-client-core-lib/src/app/openpos-core/
npm install
npm link
cd ../../../../openpos-client/
npm install
npm link openpos-core

# To start client: 
# $cd openpos-client/
# $ng serve

