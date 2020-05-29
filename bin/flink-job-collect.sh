#!/usr/bin/env bash

# 脚本失败时即退出
set -e
# set -x
set -u
baseDir=$(
  cd "$(dirname "$0")" || exit
  pwd
)

#baseDir=$(
#  dirname "$0"
#)

baseDir="$(dirname "$baseDir")"
flink_job_dir="$baseDir/flink_job"
echo "flink_job_dir => $flink_job_dir"
# 如果flink_jobs文件夹不存在，则创建
if [ ! -d "$flink_job_dir" ]; then
  mkdir "$flink_job_dir"
elif [ "$(ls -A ${flink_job_dir})" = "" ]; then
  echo "$flink_job_dir is empty"
else
  for flink_job_jar in "$flink_job_dir"/*.jar; do
    echo "find flink_job_jar => $flink_job_jar"
    echo "rm command => rm -rf $flink_job_jar"
    rm -rf "$flink_job_jar"
  done
fi

if [ -f "$flink_job_dir/flink_job.tar" ]; then
  echo "$flink_job_dir/flink_job.tar is exist, remove it"
  rm -rf "$flink_job_dir/flink_job.tar"
fi

for flink_job_project in "$baseDir"/weye-data-engine/weye-flink*; do
  [[ -d "$flink_job_project" ]] || break
  echo "$flink_job_project"
  cp $flink_job_project/target/weye*.jar $flink_job_dir/
done

echo "tar compress"

cd ${flink_job_dir} && tar -czvf flink_job.tar ./*
