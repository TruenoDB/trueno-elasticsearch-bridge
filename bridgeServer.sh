#!/usr/bin/env bash

DAEMON=0

LAUNCH_PATH=`PWD`

CLASSPATH=${LAUNCH_PATH}/trueno-elastic-bridge-server.jar

# Find the java binary
if [ -n "${JAVA_HOME}" ]; then
  RUNNER="${JAVA_HOME}/bin/java"
else
  if [ `command -v java` ]; then
    RUNNER="java"
  else
    echo "JAVA_HOME is not set" >&2
    exit 1
  fi
fi

sleep 5

# Check if the process will be detached as daemon
if [ $DAEMON -eq 0 ]; then
  exec "$RUNNER" -cp "$CLASSPATH" org.trueno.es.bridge.Server "$@"
else
  exec "$RUNNER" -cp "$CLASSPATH" org.trueno.es.bridge.Server $@ <&- &
  retval=$?
  pid=$!

  [ $retval -eq 0 ] || exit $retval
  echo ${pid} > trueno.pid
  # Check if trueno is UP
  if [ ! ps -p $pid > /dev/null ]; then
    exit 1
  fi
  exit 0
fi

exit $?