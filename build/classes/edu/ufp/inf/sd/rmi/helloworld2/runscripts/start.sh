#!/usr/bin/env bash
trap cleanup 1 2 3 6

PACKAGE=edu.ufp.inf.sd.rmi.helloworld2
CLIENT_CLASS=Client
SERVER_CLASS=Server


cleanup()
{
  echo "cleanup"
  pkill -f 'rmiregistry'
  pkill -f 'java'

  IFS='.' tokens=( $PACKAGE )
  rm -rf ${tokens[0]}
  exit 1
}

compile(){
  javac -d . ../server/Hello.java ../server/Server.java ../client/Client.java
}

start_registry(){
  pkill -f 'rmiregistry'
  rmiregistry &
}


start_server(){
 java -classpath . -Djava.rmi.server.codebase=file:./ ${PACKAGE}.server.$SERVER_CLASS &
}


start_client(){
 java  -classpath . ${PACKAGE}.client.$CLIENT_CLASS
}

compile
start_registry
start_server

sleep 2




while true; do
    options=("Run Client" "Quit")

    echo "Choose an option:"
    select opt in "${options[@]}"; do
        case $REPLY in
            1) echo "Run Client"; start_client; break ;;
            2) cleanup; break ;;
            *) echo "invalid option $REPLY";;
        esac
    done
done



