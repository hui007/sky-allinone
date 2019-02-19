#!/bin/bash
rootPath="../../.."
cd $rootPath
pwd
scp ./target/allinone-0.0.1-SNAPSHOT.jar root@193.112.47.33:/home/joshui
#scp $ENV/target/allinone-0.0.1-SNAPSHOT.jar root@193.112.47.33:/home/joshui