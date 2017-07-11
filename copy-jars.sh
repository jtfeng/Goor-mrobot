#!/bin/bash

################################################################
JOB_NAME=$PWD
WORKSPACE_FOREGROUND=$JOB_NAME/jars
MOD_VERSION=1.0
TARGET=target


################################################################
CURRENT_MOD_NAME=goor

#rm $WORKSPACE_FOREGROUND/$CURRENT_MOD_NAME/$CURRENT_MOD_NAME-$MOD_VERSION.jar
mv $JOB_NAME/$CURRENT_MOD_NAME/$TARGET/$CURRENT_MOD_NAME-$MOD_VERSION.jar $WORKSPACE_FOREGROUND/


################################################################
CURRENT_MOD_NAME=goor-server

#rm $WORKSPACE_FOREGROUND/$CURRENT_MOD_NAME/$CURRENT_MOD_NAME-$MOD_VERSION.jar
mv $JOB_NAME/$CURRENT_MOD_NAME/$TARGET/$CURRENT_MOD_NAME-$MOD_VERSION.jar $WORKSPACE_FOREGROUND/
