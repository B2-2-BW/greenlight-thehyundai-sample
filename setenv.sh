#!/bin/sh

export JAVA_OPTS="$JAVA_OPTS -Dgreenlight.api.core.url=${GREENLIGHT_API_CORE_URL}"
export JAVA_OPTS="$JAVA_OPTS -Dgreenlight.api.core.key=${GREENLIGHT_API_CORE_KEY}"
export JAVA_OPTS="$JAVA_OPTS -Djwt.secret=${JWT_SECRET}"
export JAVA_OPTS="$JAVA_OPTS -Djwt.expiration=${JWT_EXPIRATION}"