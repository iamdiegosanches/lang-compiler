#######################################################################
### Álvaro Braz Cunha - 21.1.8163                                   ###
### Diego Sanches Nere dos Santos - 21.1.8003                       ###
#######################################################################

all: LangCompiler.class

LangCompiler.class: lang/parser/LangParser.java
	javac -cp ".:tools/antlr-4.13.2-complete.jar" lang/parser/*.java *.java

lang/parser/LangParser.java:
	java -jar ./tools/antlr-4.13.2-complete.jar lang/parser/Lang.g4 -package lang.parser -no-listener -visitor

cleanClasses:
	find -name "*.class" -delete

clean: cleanClasses
	rm lang/parser/Lang.interp
	rm lang/parser/Lang.tokens
	rm lang/parser/LangLexer.interp
	rm lang/parser/LangLexer.tokens
	rm lang/parser/LangLexer.java
	rm lang/parser/LangParser.java
	rm lang/parser/LangVisitor.java
	rm lang/parser/LangBaseVisitor.java
