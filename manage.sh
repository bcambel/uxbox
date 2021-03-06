#!/usr/bin/env bash

REV=`git rev-parse --short HEAD`
IMGNAME="uxbox"

function kill_container {
    if $(sudo docker ps |grep -q $IMGNAME); then
        sudo docker ps |grep $IMGNAME | awk '{print $1}' | xargs --no-run-if-empty sudo docker kill
    fi
}

function build_image {
    kill_container
    sudo docker build --rm=true -t $IMGNAME:$REV docker/
}

function run_image {
    kill_container

    if ! $(sudo docker images|grep $IMGNAME |grep -q $REV); then
        build_image
    fi

    mkdir -p $HOME/.m2

    sudo docker run -ti \
         -v `pwd`:/home/uxbox/uxbox  \
         -v $HOME/.m2:/home/uxbox/.m2 \
         -v $HOME/.gitconfig:/home/uxbox/.gitconfig \
         -p 3449:3449 -p 6060:6060 -p 9090:9090 $IMGNAME:$REV
}

function usage {
    echo "USAGE: $0 [ build | run]"
}

case $1 in
    build)
        build_image
        ;;
    run)
        run_image
        ;;
    *)
        usage
        ;;
esac
