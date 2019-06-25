@REM ************************************************************************************
@REM Description: make file for RMI stub generation
@REM Author: Rui Moreira
@REM Date: 20/02/2009
@REM ************************************************************************************
call setenv server

@REM ****** GENRATE STUBS & SKELETONS FOR JAVARM PACKAGE *******
 rmic -d %ABSPATH2CLASSES% -classpath %CLASSPATH% %JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SERVANT_IMPL_CLASS_POSTFIX%
 rmic -d %ABSPATH2CLASSES% -classpath %CLASSPATH% %JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SERVANT_ACTIVATABLE_IMPL_CLASS_POSTFIX%