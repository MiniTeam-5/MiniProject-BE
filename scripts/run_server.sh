# run_server.sh

SERVER_COMMAND="nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &"
echo "${SERVER_COMMAND}"