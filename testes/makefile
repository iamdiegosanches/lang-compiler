
all: compile

compile:
	javac -cp . langtester/TesterUI.java

clean:
	find -name "*.class" -delete
dist :
	jar cfm LangTester.jar Manifest.txt langtester/*.class
