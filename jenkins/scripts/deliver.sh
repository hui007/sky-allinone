#!/bin/bash
#rootPath="../../.."
#cd $rootPath
#pwd
#cd /usr/bin
pwd
#ls
echo '不清楚为啥会打包入@2目录'
cd "$(pwd|xargs -I {} echo {}@2)"
scp ./target/allinone-0.0.1-SNAPSHOT.jar root@193.112.47.33:/home/joshui
#scp $ENV/target/allinone-0.0.1-SNAPSHOT.jar root@193.112.47.33:/home/joshui