JC = javac

DIR_E = src/utils
DIR_BnB = src/branch_and_bound

DIR_B = bin
DIR_BE = $(DIR_B)/utils
DIR_BBnB = $(DIR_B)/branch_and_bound

ARG_JC = -d $(DIR_B) -cp $(DIR_B)/
ARG_J = -cp
ARG_RM = -f -r
ARG_MK = -p


.SUFFIXES: .java .class

$(DIR_BBnB)/TSPBranchAndBound.class: $(DIR_BE)/Grafo.class
	$(JC) $(ARG_JC) $(DIR_BnB)/TSPBranchAndBound.java

$(DIR_BE)/Grafo.class: $(DIR_BE)/ComparatorDouble.class $(DIR_BE)/PriorityQueue.class $(DIR_BE)/UnionFind.class mkdir
	$(JC) $(ARG_JC) $(DIR_E)/Grafo.java

$(DIR_BE)/PriorityQueue.class: $(DIR_BE)/ComparatorDouble.class mkdir
	$(JC) $(ARG_JC) $(DIR_E)/PriorityQueue.java
	
$(DIR_BE)/UnionFind.class: mkdir
	$(JC) $(ARG_JC) $(DIR_E)/UnionFind.java
	
$(DIR_BE)/ComparatorDouble.class: mkdir
	$(JC) $(ARG_JC) $(DIR_E)/ComparatorDouble.java

.PHONY: mkdir
mkdir:
	mkdir $(ARG_MK) $(DIR_B)

.PHONY: clean
clean:
	rm $(ARG_RM) $(DIR_B)
