#! /bin/sh
mvn -Dmaven.test.skip=true install
stty_save=$(stty -g)
tput civis
stty -echo
stty cbreak
java -jar ./target/mvc-1.1-SNAPSHOT-jar-with-dependencies.jar "$@"
stty $stty_save
tput cnorm
clear
