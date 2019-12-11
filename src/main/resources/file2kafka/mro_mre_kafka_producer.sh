#!/bin/bash

#TIME_RGION 是发送的时间分区数据
TIME_REGION=20190717100000
MRO_PRO_PATH=/home/shejiyuan/process/file2kafka/properties/mro-public.properties
MRE_PRO_PATH=/home/shejiyuan/process/file2kafka/properties/mre-public.properties

if [ $# -eq 1 ]
then
  TIME_REGION=${1}
fi

echo 'TIME_REGION:'${TIME_REGION}

#mro程序
java -cp /home/shejiyuan/process/file2kafka/networkoptimization.jar cn.gz.cm.networkoptimization.submit.VoilteDataProducer ${MRO_PRO_PATH} ${TIME_REGION}

#mre程序
java -cp /home/shejiyuan/process/file2kafka/networkoptimization.jar cn.gz.cm.networkoptimization.submit.VoilteDataProducer ${MRE_PRO_PATH} ${TIME_REGION}
