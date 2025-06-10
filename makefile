all: LangCompiler.class

LangCompiler.class: LangLex.java
	javac -cp . LangCompiler.java

LangLex.java:
	java -jar jflex.jar -nobak lang.flex

cleanClasses:
	find -name "*.class" -delete

clean: cleanClasses
	rm LangLex.java
