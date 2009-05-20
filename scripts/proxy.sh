#!/bin/sh

. env.sh

PROXY_PROPS="-Dlisten.port=9090 -Dtarget.host=localhost -Dtarget.port=8080"
#PROXY_PROPS="$PROXY_PROPS -Dlatency.millis=250"
#PROXY_PROPS="$PROXY_PROPS -Dpacket.loss.rate=100"
#PROXY_PROPS="$PROXY_PROPS -Dbandwidth.throttle=50"
#PROXY_PROPS="$PROXY_PROPS -Denable.connection.loss=true"

java $PROXY_PROPS -Xmx1024m -cp "$CP" com.moneybender.proxy.Proxy
