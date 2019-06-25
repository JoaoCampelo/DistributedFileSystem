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
@REM java -cp ${CLASSPATH} \
@REM     #Property defining the security policy file for the Setup program
@REM     -Djava.security.policy=setup.policy \
@REM     #Property defining the root directory for the implementation's classes
@REM     -Djava.rmi.server.codebase=${SERVER_CODEBASE} \
@REM     #Property defining the root directory for the Setup program's classes
@REM     -Dexamples.activation.setup.codebase=${SERVER_CODEBASE} \
@REM     #Property defining the root directory for the implementation's classes
@REM     -Dexamples.activation.impl.codebase=${SERVER_CODEBASE} \
@REM     #Property defining the name of the service (this property is used inside main() for registry rebind)
@REM     -Dexamples.activation.name=${SERVICE_NAME} \
@REM     #Property defining a file containing a persistent object
@REM     [-Dexamples.activation.file=filename] \
@REM     #Property defining the security policy file for the activation group
@REM     [-Dexamples.activation.policy=group.policy] \
@REM     examples.activation.Setup ${SERVER_ACTIVATABLE_CLASS}
java -cp %CLASSPATH% -Djava.security.policy=%SERVER_SECURITY_POLICY% -Djava.rmi.server.codebase=%SERVER_CODEBASE% -D%JAVAPACKAGE%.activation.setup.codebase=%SERVER_CODEBASE% -D%JAVAPACKAGE%.activation.impl.codebase=%SERVER_CODEBASE% -D%JAVAPACKAGE%.activation.servicename=%SERVICE_NAME% -D%JAVAPACKAGE%.activation.file=%SERVANT_PERSISTENT_STATE_FILENAME% -D%%JAVAPACKAGE.activation.policy=%GROUP_SECURITY_POLICY% %JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SETUP_CLASS_POSTFIX% %REGISTRY_HOST% %REGISTRY_PORT% %SERVANT_ACTIVATABLE_IMPL_CLASS%

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%