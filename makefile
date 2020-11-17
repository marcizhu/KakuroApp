solver:
	javac app/SolverApp.java
	java app/SolverApp
tester:
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SolverTest.java
	java -jar lib/junit-platform-console-standalone-1.7.0.jar -cp .:test/*.class --scan-classpath
clean:
	find src app test -name "*.class" -type f -delete

