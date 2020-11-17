all: solver tests

solver: app/SolverApp.class

app/SolverApp.class: app/SolverApp.java src/controllers/Reader.java src/controllers/Solver.java
	javac app/SolverApp.java

tests: test/SolverTest.class

test/SolverTest.class: test/SolverTest.java src/controllers/Reader.java src/controllers/Solver.java src/domain/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SolverTest.java

run-solver: app/SolverApp.class
	java app/SolverApp

run-tests: test/SolverTest.class
	java -jar lib/junit-platform-console-standalone-1.7.0.jar -cp .:test/*.class --scan-classpath

clean:
	find src app test -name "*.class" -type f -delete

