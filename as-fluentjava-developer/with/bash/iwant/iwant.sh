#!/bin/bash
set -eu

COMMIT=81c9215180a3a0528dc925dc441bae73665f7716
GITHUBUSER=wipu
URL=https://raw.githubusercontent.com/$GITHUBUSER/iwant/$COMMIT

HERE=$(dirname "$0")
cd "$HERE/../../.."

fetch() {
    local RELPATH=$1
    rm -f "$RELPATH"
    local PARENT=$(dirname "$RELPATH")
    mkdir -p "$PARENT"
    cd "$PARENT"
    wget "$URL/essential/iwant-entry/as-some-developer/$RELPATH"
    cd -
}

fetch with/ant/iw/build.xml
fetch with/bash/iwant/help.sh
chmod u+x with/bash/iwant/help.sh
fetch with/java/org/fluentjava/iwant/entry/Iwant.java

CONF=i-have/conf
mkdir -p "$CONF"
echo "iwant-from=https://github.com/$GITHUBUSER/iwant/archive/$COMMIT.zip" > "$CONF/iwant-from"
