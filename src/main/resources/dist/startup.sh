BASE_DIR="/home/projects/PizzeriaFabulosa/be"

cd ${BASE_DIR}

if [ -f ./pid ]; then
    LASTPID=`cat ./pid`
    kill -0 ${LASTPID} >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "App is already running with PID ${LASTPID}. Exiting..."
        exit 1
    fi
fi

LAUNCHER_JAR="PizzeriaBackEnd.jar"
JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
CONFIG_PATH="${BASE_DIR}/config"
JAVA_OPTIONS="-Dspring.config.location=${CONFIG_PATH}/application.yaml"
mkdir -p ${BASE_DIR}/logs

nohup ${JAVA_HOME}/bin/java ${JAVA_OPTIONS} -cp ${CONFIG_PATH} -jar ${LAUNCHER_JAR} >/dev/null 2>&1 &
PID=$!
echo $PID > pid
echo "PizzeriaBackEnd started with PID ${PID}."