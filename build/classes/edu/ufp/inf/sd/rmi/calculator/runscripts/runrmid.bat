@REM *************************************************
@REM Description: start rmi deamon
@REM Author: Rui Moreira
@REM Date: 20/02/2005
@REM ************************************************************************************
@REM Script usage: runsetup <role> (where role should be: server / client)
@REM call setenv
call setenv server

@REM Starts the Activator and an internal registry on default port 1098 and binds an
@REM ActivationSystem to the name java.rmi.activation.ActivationSystem in this internal registry.
@REM rmid -J-Dsun.rmi.rmid.maxstartgroup=10 -J-Dsun.rmi.server.exceptionTrace=true -J-Dsun.rmi.server.activation.debugExec=true -J-Dsun.rmi.log.debug=true -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.hostname=%MarsupilamiIP% -J-Djava.security.policy=%POLICY% -J-D-Djava.rmi.server.codebase=%CODEBASE%
@REM rmid -J-Dsun.rmi.rmid.maxstartgroup=10 -J-Dsun.rmi.server.exceptionTrace=true -J-Dsun.rmi.server.activation.debugExec=true -J-Dsun.rmi.log.debug=true -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.hostname=%ZezinhoIP% -J-Djava.security.policy=%POLICY% -J-D-Djava.rmi.server.codebase=%CODEBASE%
@REM rmid -J-Djava.security.policy=rmid.policy -port 1099

rmid -J-Djava.security.policy=%RMID_SECURITY_POLICY% -J-Djava.rmi.server.codebase=%SERVER_CODEBASE% -J-D%JAVAPACKAGE%.activation.policy=%GROUP_SECURITY_POLICY%

@REM cd %ABSPATH2CLASSES%
@REM cls