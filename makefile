JCC = javac
RM = rm

JFLAGS = -g

JCLASSPATH = -classpath /usr/hdp/2.3.0.0-2557/hadoop/hadoop-common.jar:/usr/hdp/2.3.0.0-2557/hadoop-mapreduce/hadoop-mapreduce-client-core.jar:/usr/share/java/slf4j/api.jar:classes/
JDEBUG = -g
JOUTPUT = -d classes/

default: deleteOutput jar runJar

runJar: target/distributedKnapsack.jar
	yarn jar target/distributedKnapsack.jar cloud.DistributedKnapsack

jar: sources.class
	jar -cvf target/distributedKnapsack.jar -C classes/ .

sources.class: src/cloud/Utils.java src/cloud/CreateInitialPopulationMapReduce.java src/cloud/SelectGeneMapReduce.java src/cloud/CrossOverMapReduce.java src/cloud/DistributedKnapsack.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/Utils.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/CreateInitialPopulationMapReduce.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/SelectGeneMapReduce.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/CrossOverMapReduce.java
	$(JCC) $(JCLASSPATH) $(JDEBUG) $(JOUTPUT) src/cloud/DistributedKnapsack.java

clean:
	$(RM) classes/*.class
	$(RM) target/*.jar

deleteOutput:
	hdfs dfs -rm -f -R distributedKnapsack/files/population-*
	hdfs dfs -rm -f -R distributedKnapsack/files/selected-*
	hdfs dfs -rm -f -R distributedKnapsack/files/crossover-*

copyMakefile:
	pscp.exe -pw hadoop makefile root@127.0.0.1:/root/distributedKnapsack