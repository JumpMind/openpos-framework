#!/bin/sh

dp0="`dirname "$0"`"
OPENPOS_HOME="`cd "$dp0/.." && pwd`"

export OPENPOS_HOME

OPENPOS_OPTIONS="-Dfile.encoding=utf-8 \
-Duser.language=en \
-Djava.io.tmpdir=$OPENPOS_HOME/tmp"
export OPENPOS_OPTIONS

OPENPOS_JAVA=java
export OPENPOS_JAVA
