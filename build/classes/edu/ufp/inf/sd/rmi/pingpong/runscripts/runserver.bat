@REM ************************************************************************************
@REM Description: run Client
@REM Author: Rui Moreira
@REM Date: 20/02/2005
@REM ************************************************************************************
@REM Script usage: runsetup <role> (where role should be: server / client)
@REM call setenv
call setenv server

@cd %ABSPATH2CLASSES%
@cls
@REM java -cp %CLASSPATH% -Djava.security.policy=%POLICY% -Djava.rmi.server.codebase=%CODEBASE% %JAVAPACKAGE%.%SERVER_CLASS_PREFIX%%SERVER_CLASS_POSTFIX%
@REM # * java.rmi.server.codebase property specifies the location (codebase URL) from which the definitions for classes originating from this server can be downloaded.
@REM #   ND: if codebase specifies a directory hierarchy (as opposed to a JAR file), you must include a trailing slash at the end of the codebase URL.
@REM # * java.rmi.server.hostname property specifies the host name or address to put in the stubs for remote objects exported in this Java virtual machine. 
@REM #   This value is the host name or address used by clients when they attempt remote method invocations.
@REM #   By default, the RMI implementation uses the server's IP address as indicated by the java.net.InetAddress.getLocalHost API.
@REM #   However, sometimes, this address is not appropriate for all clients and a fully qualified host name would be more effective.
@REM #   To ensure that RMI uses a host name (or IP address) for the server that is routable from all potential clients, set the java.rmi.server.hostname property.
@REM # * java.security.policy property is used to specify the policy file that contains the permissions you intend to grant.
java -cp %CLASSPATH% -Djava.rmi.server.codebase=%SERVER_CODEBASE% -Djava.rmi.server.hostname=%SERVER_RMI_HOST% -Djava.security.policy=%SERVER_SECURITY_POLICY% %JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SERVER_CLASS_POSTFIX% %REGISTRY_HOST% %REGISTRY_PORT% %SERVICE_NAME_ON_REGISTRY%

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%