JCC = javac
RM = rm

JFLAGS = -g

JCLASSPATH = -classpath /usr/hdp/2.3.0.0-2557/hadoop/hadoop-common.jar:/usr/hdp/2.3.0.0-2557/hadoop-mapreduce/hadoop-mapreduce-client-core.jar:classes/
JDEBUG = -g
JOUTPUT = -d classes/

default: runJar

runJar: target/distributedKnapsack.jar
	yarn jar target/distributedKnapsack.jar cloud.DistributedKnapsack

jar: DistributedKnapsack.class
	jar -cvf target/distributedKnapsack.jar -C classes/ .

DistributedKnapsack.class: src/cloud/DistributedKnapsack.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/Utils.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/CreateInitialPopulationMapReduce.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/DistributedKnapsack.java

clean:
	$(RM) classes/*.class
	$(RM) target/*.jar

deleteOutput:
	hdfs dfs -rm distributedKnapsack/files/output/*
	hdfs dfs -rmdir distributedKnapsack/files/output/

copyMakefile:
	pscp.exe -pw hadoop makefile root@127.0.0.1:/root/distributedKnapsack