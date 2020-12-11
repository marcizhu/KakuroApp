# Default target: Build solver, generator and tests
all: solver generator tests

# Solver target
solver: app/SolverApp.class

app/SolverApp.class: app/SolverApp.java src/domain/controllers/Reader.java src/domain/algorithms/Solver.java
	javac app/SolverApp.java

# Generator target
generator: app/GeneratorApp.class

app/GeneratorApp.class: app/GeneratorApp.java src/domain/algorithms/Generator.java src/domain/entities/Board.java src/domain/entities/Difficulty.java
	javac app/GeneratorApp.java

# Unit tests
tests: test/SolverTest.class test/SwappingCellQueueTest.class test/GeneratorTest.class

test/SolverTest.class: test/SolverTest.java src/domain/controllers/Reader.java src/domain/algorithms/Solver.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SolverTest.java

test/SwappingCellQueueTest.class: test/SwappingCellQueueTest.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SwappingCellQueueTest.java

test/GeneratorTest.class: test/GeneratorTest.java src/domain/algorithms/Generator.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/GeneratorTest.java

# Make kakurosolver.tar.gz
kakurosolver: clean
	cp test/kakurosolver.java kakurosolver.java
	tar zcf kakurosolver.tar.gz src/* kakurosolver.java
	rm kakurosolver.java

# Make kakurogenerator.tar.gz
kakurogenerator: clean
	cp test/kakurogenerator.java kakurogenerator.java
	tar zcf kakurogenerator.tar.gz src/* kakurogenerator.java
	rm kakurogenerator.java

# Run targets
run-solver: app/SolverApp.class
	java app/SolverApp

run-generator: app/GeneratorApp.class
	java app/GeneratorApp

run-tests: test/SolverTest.class test/SwappingCellQueueTest.class test/GeneratorTest.class
	java -jar lib/junit-platform-console-standalone-1.7.0.jar -cp .:test/SolverTest.class --scan-classpath

clean:
	find src app test -name "*.class" -type f -delete
	rm -f kakurosolver.tar.gz kakurogenerator.tar.gz
