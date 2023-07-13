#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

cd $(dirname $0)

# Compile the project
javac -classpath :src/lib/* -d build -proc:none -sourcepath src src/Main.java

# Run 2 clients in parrallel
cd src
java -classpath ../build:lib/* Main & java -classpath ../build:lib/* Main
