@REM ************************************************************************************
@REM Description: run Pingclient
@REM Author: Rui Moreira
@REM Date: 20/02/2005
@REM ************************************************************************************
@REM Script usage: runclient <role> (where role should be: server / client)
@REM call setclientenv
call setenv client

@cd %ABSPATH2CLASSES%
@cls
@REM java 
@REM     #Root directory of client program/class (class path)
@REM     -cp classpath \
@REM     #Property defining security policy, i.e., permissions to grant to code/classes
@REM     -Djava.security.policy=client.policy \
@REM     #Property defining URL where client serves its classes
@REM     -Djava.rmi.server.codebase=${SERVER_CODEBASE}
@REM     #Property defining URL of client class (this property is used inside client.policy file to grant permissions to client class)
@REM     -Dexamples.activation.client.codebase=${CLIENT_CODEBASE} \
@REM     #Property defining the name of the service (this property is used inside main() for registry lookup)
@REM     -Dexamples.activation.servicename=${SERVICE_NAME} \
@REM     examples.activation.client.Client ${SERVER_RMI_HOST} ${SERVER_RMI_PORT}
java -cp %CLASSPATH% -Djava.security.policy=%CLIENT_SECURITY_POLICY% -Djava.rmi.server.codebase=%SERVER_CODEBASE% -D%JAVAPACKAGEROLE%.codebase=%CLIENT_CODEBASE% -D%JAVAPACKAGE%.servicename=%SERVICE_NAME_ON_REGISTRY% %JAVAPACKAGEROLE%.%CLIENT_CLASS_PREFIX%%CLIENT_CLASS_POSTFIX% %REGISTRY_HOST% %REGISTRY_PORT% %SERVICE_NAME_ON_REGISTRY%

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%