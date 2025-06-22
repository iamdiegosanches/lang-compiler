#######################################################################
### Álvaro Braz Cunha - 21.1.8163                                   ###
### Diego Sanches Nere dos Santos - 21.1.8003                       ###
#######################################################################

all: LangCompiler.class

LangCompiler.class: lang/parser/LangLexer.java lang/parser/LangParser.java
	javac -cp .:tools/java-cup-11b-runtime.jar LangCompiler.java

lang/parser/LangParser.java:
	java -jar tools/java-cup-11b.jar -destdir lang/parser/ lang/parser/lang.cup

lang/parser/LangLexer.java:
	java -jar  tools/jflex.jar -nobak -d lang/parser lang/parser/lang.flex

cleanClasses:
	find -name "*.class" -delete

clean: cleanClasses
	rm lang/parser/LangLex.java
	rm lang/parser/parser.java
	rm lang/parser/sym.java
