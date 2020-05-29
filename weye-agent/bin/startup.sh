#! /bin/bash
echo "agent start"

## resolve links - $0 may be a link to Maven's home
PRG="$0"

# need this for relative symlinks
# 如果是符号链接， 找到原始文件
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  # 正则匹配
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' >/dev/null; then
    PRG="$link"
  else
    PRG="$(dirname "$PRG")/$link"
  fi
done

saveddir=$(pwd)

AGENT_HOME=$(dirname "$PRG")/..

# make it fully qualified
AGENT_HOME=$(cd "$AGENT_HOME" && pwd)

cd "$saveddir" || exit

# 找到JAVA执行程序
if [ -z "$JAVA_HOME" ]; then
  JAVACMD=$(which java)
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ]; then
  echo "The JAVA_HOME environment variable is not defined correctly" >&2
  echo "This environment variable is needed to run this program" >&2
  echo "NB: JAVA_HOME should point to a JDK not a JRE" >&2
  exit 1
fi

sudo $JAVACMD \
  $AGENT_OPTS \
  $AGENT_DEBUG_OPTS \
  -Dagent.home="${AGENT_HOME}" \
  -Dio.netty.tryReflectionSetAccessible=true \
  -jar $AGENT_HOME/bin/weye-agent.jar
