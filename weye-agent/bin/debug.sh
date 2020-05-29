#! /bin/bash
AGENT_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

echo Preparing to execute Agent in debug mode

env AGENT_OPTS="$AGENT_OPTS" AGENT_DEBUG_OPTS="$AGENT_DEBUG_OPTS" "$(dirname "$0")/startup.sh" "$@"
