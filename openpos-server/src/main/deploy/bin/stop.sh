#!/bin/sh
 
. "`dirname "$0"`/setenv.sh"
exec "$OPENPOS_JAVA" $OPENPOS_OPTIONS -jar $OPENPOS_HOME/lib/openpos-wrapper.jar "stop" $OPENPOS_HOME/config/openpos_service.conf