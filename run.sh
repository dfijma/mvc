#! /bin/sh
mvn -Dmaven.test.skip=true install
stty_save=$(stty -g)
tput civis
stty -echo
stty cbreak
java -cp ./target/mvc-1.0-SNAPSHOT.jar net.fijma.mvc.example.Main "$@"
stty $stty_save
tput cnorm
clear
