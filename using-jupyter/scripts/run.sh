docker run -it -p 8888:8888 -p 6800:6800 -v "$(pwd)"/notebooks:/home/jovyan/work -v "$HOME/.m2":/home/jovyan/maven-local kotlin-jupyter
