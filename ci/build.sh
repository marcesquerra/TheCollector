#!/bin/bash

set -eu

echo "$GPG_PRIVATE_KEY_B64" | base64 --decode > gpg_key

gpg --import gpg_key

rm gpg_key

curl -L -o ~/bin/mill https://github.com/lihaoyi/mill/releases/download/0.4.0/0.4.0-12-102ddf && chmod +x ~/bin/mill
export PATH=~/bin/mill:$PATH

mill __.compile

mill mill.scalalib.PublishModule/publishAll \
    --sonatypeCreds $SONATYPE_CREDS \
    --gpgPassphrase $GPG_PASSWORD \
    --publishArtifacts __.publishArtifacts \
    --readTimeout 600000 \
    --release true \
    --signed true
