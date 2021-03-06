JC = javac
J = java

DIR_E = src/utils
DIR_BnB = src/branch_and_bound
DIR_AE = src/aestrela
DIR_EX = src/experimento

DIR_B = bin
DIR_BE = $(DIR_B)/utils
DIR_BBnB = $(DIR_B)/branch_and_bound
DIR_BAE = $(DIR_B)/aestrela
DIR_BEX = $(DIR_B)/experimento

ARG_JC = -d $(DIR_B) -cp $(DIR_B)/
ARG_J = -cp $(DIR_B)
ARG_RM = -f -r
ARG_MK = -p


.SUFFIXES: .java .class

# Experimento

compile: $(DIR_BEX)/Experimento.class

experimento:
	@$(J) $(ARG_J) experimento/Experimento

$(DIR_BEX)/Experimento.class: $(DIR_BAE)/PCVAEstrela.class $(DIR_BBnB)/PCVBranchAndBound.class
	$(JC) $(ARG_JC) $(DIR_EX)/Experimento.java

# A Estrela

$(DIR_BAE)/PCVAEstrela.class: $(DIR_BE)/Grafo.class $(DIR_BAE)/Estado.class
	$(JC) $(ARG_JC) $(DIR_AE)/PCVAEstrela.java

$(DIR_BAE)/Estado.class:
	$(JC) $(ARG_JC) $(DIR_AE)/Estado.java

#Branch-and-Bound

$(DIR_BBnB)/PCVBranchAndBound.class: $(DIR_BE)/Grafo.class
	$(JC) $(ARG_JC) $(DIR_BnB)/PCVBranchAndBound.java

# Estruturas de dados usados usadas

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
