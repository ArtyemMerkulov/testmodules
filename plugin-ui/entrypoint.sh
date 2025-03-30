#!/bin/sh

if [ "${DEV}" = "false" ]; then
    echo "Starting test mode"
    exec /run.sh
fi

echo "Starting development mode"