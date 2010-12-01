JCC = javac -J-Xmx1024m
JRE = java -Xms512m -Xmx1024m

CLASSPATH = jar/*
SOURCEPATH = src

JFLAGS = -sourcepath $(SOURCEPATH) -cp .:$(CLASSPATH) -d .
JREFLAGS = -cp .:$(CLASSPATH):$(DESTINATIONPATH)

default : classes

classes :
	
	$(JCC) $(JFLAGS) $(SOURCEPATH)/Resource.java
	$(JCC) $(JFLAGS) $(SOURCEPATH)/GraphADS.java
	$(JCC) $(JFLAGS) $(SOURCEPATH)/IdMaps.java
	$(JCC) $(JFLAGS) $(SOURCEPATH)/SerializableGraphADS.java
	$(JCC) $(JFLAGS) $(SOURCEPATH)/ComputeEnrichments.java



wri : 
	$(JRE) $(JREFLAGS) ammo/SerializableGraphADS $(O)

enrich :
	$(JRE) $(JREFLAGS) ammo/ComputeEnrichments $(O) $(D) $(L)

clean :
	rm -rf ammo/*

