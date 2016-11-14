#!/bin/bash

PATH="/bin:/usr/bin:/usr/local/bin:/usr/lib:/usr/local/lib"

javac -d classes/ sources/net/310usenet/core/*.java
javac -d classes/ sources/net/310usenet/gui/*.java
javac -d classes/ sources/net/310usenet/utils/*.java
javac -d classes/ sources/net/310usenet/tests/*.java
