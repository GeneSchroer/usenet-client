#!/bin/bash

javac -cp classes/ -d classes/ sources/net/usenet_client/utils/*.java
javac -cp classes/ -d classes/ sources/net/usenet_client/core/*.java
javac -cp classes/ -d classes/ sources/net/usenet_client/app/*.java
javac -cp classes/ -d classes/ sources/net/usenet_client/tests/*.java
