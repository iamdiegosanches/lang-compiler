#######################################################################
### Álvaro Braz Cunha - 21.1.8163                                   ###
### Diego Sanches Nere dos Santos - 21.1.8003                       ###
#######################################################################

all: LangCompiler.class

LangCompiler.class: LangLex.java
	javac -cp . LangCompiler.java

LangLex.java:
	java -jar tools/jflex.jar -nobak lang.flex

cleanClasses:
	find -name "*.class" -delete

clean: cleanClasses
	rm LangLex.java
