PRGDIR=`dirname "$0"`
cd ${PRGDIR}

if [ -f ./pid ]; then
    LASTPID=`cat ./pid`
    
    if kill -0 ${LASTPID} >/dev/null 2>&1; then
        echo "App is running with PID ${LASTPID}."
    else
        echo "PID ${LASTPID} is not running: removed unused PID file."
        rm -f ./pid
    fi
else
    echo "No PID file found."
fi