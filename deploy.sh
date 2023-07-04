#!/bin/bash

function error_exit() {
    echo "ERROR: $1" 1>&2
    exit $2
}

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [[ ${OSTYPE} != *"linux"* ]]; then
    DIR=`cygpath -lm $DIR`
fi

set -euo pipefail

#source deployment.env

oc create cm brokered-product --from-file=$DIR/application.properties

oc process -f $DIR/deployment_os.yaml | oc create -f -
