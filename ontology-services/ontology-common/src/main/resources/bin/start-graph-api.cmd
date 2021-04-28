@REM ==========================================================================
@REM Start Graph API (Local)
@REM ==========================================================================
@REM Title: start-graph-api.cmd
@REM Author: Jillur Quddus
@REM E-Mail: jillur.quddus@hyperlearning.ai
@REM Description: Start Graph API locally
@REM ==========================================================================

@echo off
title Graph API

@REM --------------------------------------------------------------------------
@REM Change to the directory of the script itself
@REM --------------------------------------------------------------------------

cd /d %~dp0

@REM --------------------------------------------------------------------------
@REM Start the Graph API locally
@REM --------------------------------------------------------------------------

java -Xms512m -Xmx1g -cp "..\libs\*;..\conf;..\data" ai.hyperlearning.ontology.services.api.graph.apps.StartAPIApplication %~1
pause
