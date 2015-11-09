JCC = javac
RM = rm

JFLAGS = -g

JCLASSPATH = -classpath /usr/hdp/2.3.0.0-2557/hadoop/hadoop-common.jar:/usr/hdp/2.3.0.0-2557/hadoop-mapreduce/hadoop-mapreduce-client-core.jar
JDEBUG = -g
JOUTPUT = -d classes/

default: deleteOutput runJar

runJar: target/distributedKnapsack.jar
	yarn jar target/distributedKnapsack.jar PopulationCreator

jar: PopulationCreator.class
	jar -cvf target/distributedKnapsack.jar -C classes/ .

PopulationCreator.class: src/PopulationCreator.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/PopulationCreator.java

clean:
	$(RM) classes/*.class
	$(RM) target/*.jar

deleteOutput:
	hdfs dfs -rm distributedKnapsack/files/output/*
	hdfs dfs -rmdir distributedKnapsack/files/output/