if [ -f ./pid ]; then
    LASTPID=`cat ./pid`
    kill -0 ${LASTPID} >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "App is already running with PID ${LASTPID}. Exiting..."
        exit 1
    fi
fi

mkdir -p ./logs

nohup java -cp ./application.yml -jar PizzeriaBackEnd.jar >/dev/null 2>&1 &
PID=$!
echo $PID > pid
echo "PizzeriaBackEnd started with PID ${PID}."