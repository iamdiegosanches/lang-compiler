#######################################################################
### Álvaro Braz Cunha - 21.1.8163                                   ###
### Diego Sanches Nere dos Santos - 21.1.8003                       ###
#######################################################################

all: LangCompiler.class

LangCompiler.class: lang/parser/LangParser.java
	javac -cp ".:tools/antlr-4.13.2-complete.jar" lang/parser/*.java *.java

lang/parser/LangParser.java: lang/parser/Lang.g4
	java -jar ./tools/antlr-4.13.2-complete.jar lang/parser/Lang.g4 -package lang.parser -no-listener -visitor

cleanClasses:
	find -name "*.class" -delete

clean: cleanClasses
	rm -f lang/parser/Lang.interp
	rm -f lang/parser/Lang.tokens
	rm -f lang/parser/LangLexer.interp
	rm -f lang/parser/LangLexer.tokens
	rm -f lang/parser/LangLexer.java
	rm -f lang/parser/LangParser.java
	rm -f lang/parser/LangVisitor.java
	rm -f lang/parser/LangBaseVisitor.java
