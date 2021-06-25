#!/bin/sh

# pandoc with latex docker
docker run --rm \
        --volume "$(pwd)/$1:/data" \
        pandoc/latex --wrap=preserve "$2" -f gfm -t $3 -o "$4"
