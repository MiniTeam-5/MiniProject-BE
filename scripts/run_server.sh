# run_server.sh

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
source "${SCRIPT_DIR}/common.sh"

SERVER_COMMAND="nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &"
echo "${SERVER_COMMAND}"
${SERVER_COMMAND}

ps aux | grep java | grep -v grep