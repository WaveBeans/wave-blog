HTTP_SERVICE_PORT=${1:-6800}
LOCAL_MAVEN=${2:-true}

MAVEN_REPO_VOLUME=
if [ "$LOCAL_MAVEN" == "true" ]; then
    MAVEN_REPO_VOLUME="-v ${HOME}/.m2:/home/jovyan/maven-local"
fi
docker run -it -p 8888:8888 -p ${HTTP_SERVICE_PORT}:${HTTP_SERVICE_PORT} -v "$(pwd)"/notebooks:/home/jovyan/work ${MAVEN_REPO_VOLUME} kotlin-jupyter
