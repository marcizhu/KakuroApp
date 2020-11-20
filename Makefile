# Default target: Build solver, generator and tests
all: solver generator tests

# Solver target
solver: app/SolverApp.class

app/SolverApp.class: app/SolverApp.java src/controllers/Reader.java src/controllers/Solver.java
	javac app/SolverApp.java

# Generator target
generator: app/GeneratorApp.class

app/GeneratorApp.class: app/GeneratorApp.java src/controllers/Generator.java src/domain/Board.java src/domain/Difficulty.java
	javac app/GeneratorApp.java

# Unit tests
tests: test/SolverTest.class

test/SolverTest.class: test/SolverTest.java src/controllers/Reader.java src/controllers/Solver.java src/domain/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SolverTest.java

# Make kakurosolver.tar.gz
kakurosolver:
	cp test/kakurosolver.java kakurosolver.java
	tar zcf kakurosolver.tar.gz src/* kakurosolver.java
	rm kakurosolver.java

# Run targets
run-solver: app/SolverApp.class
	java app/SolverApp

run-generator: app/GeneratorApp.class
	java app/GeneratorApp

run-tests: test/SolverTest.class
	java -jar lib/junit-platform-console-standalone-1.7.0.jar -cp .:test/*.class --scan-classpath

clean:
	find src app test -name "*.class" -type f -delete
	rm -f kakurosolver.tar.gz
