#!/bin/sh

. env.sh

PROXY_PROPS="-Dproxy.host=localhost -Dproxy.port=2005"
java $PROXY_PROPS -cp "$CP" com.moneybender.proxy.subscribers.HttpHeadSubscriber
