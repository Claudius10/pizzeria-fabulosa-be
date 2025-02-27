PRGDIR=`dirname "$0"`
cd ${PRGDIR}

if [ -f ./pid ]; then
    LASTPID=`cat ./pid`

    kill -0 ${LASTPID} >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "Trying to shutdown app with PID ${LASTPID}..."
        
        kill ${LASTPID} >/dev/null 2>&1
        sleep 2

        kill -0 ${LASTPID} >/dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo "Could not shutdown with SIGTERM, trying with SIGKILL..."
            kill -9 ${LASTPID} >/dev/null 2>&1
        else
            echo "Shutdown complete."
        fi
    else
        echo "The app is not running."
    fi

    rm -f ./pid
else
    echo "No PID file found."
fi