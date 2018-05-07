JC = javac

DIR_E = src/utils
DIR_BnB = src/branch_and_bound
DIR_B = bin

ARG_JC = -d $(DIR_B) -cp $(DIR_B)/
ARG_J = -cp
ARG_RM = -f
ARG_MK = -p


.SUFFIXES: .java .class

bin/Grafo.class: bin/ComparatorDouble.class bin/PriorityQueue.class bin/UnionFind.class mkdir
	$(JC) $(ARG_JC) $(DIR_E)/Grafo.java

bin/PriorityQueue.class: bin/ComparatorDouble.class mkdir
	$(JC) $(ARG_JC) $(DIR_E)/PriorityQueue.java
	
bin/UnionFind.class: mkdir
	$(JC) $(ARG_JC) $(DIR_E)/UnionFind.java
	
bin/ComparatorDouble.class: mkdir
	$(JC) $(ARG_JC) $(DIR_E)/ComparatorDouble.java

.PHONY: mkdir
mkdir:
	mkdir $(ARG_MK) $(DIR_B)

.PHONY: clean
clean:
	rm $(ARG_RM) $(DIR_B)/*.class
	rm $(ARG_RM) $(DIR_B)/$(DIR_E)/*.class