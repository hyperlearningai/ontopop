#!/bin/sh

# ------------------------------------------------------------------------------
# Start Graph API (Local)
# ------------------------------------------------------------------------------
# Title: start-graph-api.sh
# Author: Jillur Quddus
# E-Mail: jillur.quddus@hyperlearning.ai
# Description: Start Graph API locally
# ------------------------------------------------------------------------------

# ------------------------------------------------------------------------------
# (1) Change to the directory of the script itself
# ------------------------------------------------------------------------------

cd "$(dirname "$0")"

# ------------------------------------------------------------------------------
# (2) Start the Graph API locally
# ------------------------------------------------------------------------------

echo "Starting the Graph API..."
java -Xms512m -Xmx1g -cp "../libs/*:../conf:../data" ai.hyperlearning.ontology.services.api.graph.apps.StartAPIApplication $1
