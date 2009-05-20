#!/bin/sh

SEP=":"

if [ `uname -o` == "Cygwin" ] ; then
	SEP=";"
fi

CP="../config${SEP}../eclipse_bin${SEP}."
JARS=`ls ../lib/*.jar`
for JAR in $JARS ; do
	CP="$CP${SEP}$JAR"
done

#echo $CP
