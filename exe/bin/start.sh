#!/usr/bin/env bash
cur_path=$(cd $(dirname $0) && pwd)
echo $cur_path
cd $cur_path
cd ..
nohup java -jar NoWaterServer-1.0-SNAPSHOT.jar &