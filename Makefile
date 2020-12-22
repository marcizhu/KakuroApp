# Default target: Build solver, generator and tests
all: solver generator app tests


# Solver target
solver: app/SolverApp.class

app/SolverApp.class: app/SolverApp.java src/domain/controllers/Reader.java src/domain/algorithms/Solver.java
	javac app/SolverApp.java


# Generator target
generator: app/GeneratorApp.class

app/GeneratorApp.class: app/GeneratorApp.java src/domain/algorithms/Generator.java src/domain/entities/Board.java src/domain/entities/Difficulty.java
	javac app/GeneratorApp.java


# Main app
app: src/Main.class

src/Main.class: src/Main.java
	javac -cp .:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar src/Main.java
	javac -cp .:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar src/*.java


# Unit tests
tests: test/SolverTest.class test/SwappingCellQueueTest.class test/GeneratorTest.class test/repository/BoardRepositoryDBTest.class test/repository/DBTest.class test/repository/KakuroRepositoryDBTest.class test/repository/UserRepositoryDBTest.class test/GameTest.class test/KakuroTest.class test/UserTest.class

test/SolverTest.class: test/SolverTest.java src/domain/controllers/Reader.java src/domain/algorithms/Solver.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SolverTest.java

test/SwappingCellQueueTest.class: test/SwappingCellQueueTest.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/SwappingCellQueueTest.java

test/GeneratorTest.class: test/GeneratorTest.java src/domain/algorithms/Generator.java src/domain/entities/Board.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar test/GeneratorTest.java

test/repository/BoardRepositoryDBTest.class: test/repository/BoardRepositoryDBTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/repository/BoardRepositoryDBTest.java

test/repository/DBTest.class: test/repository/DBTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/repository/DBTest.java

test/repository/KakuroRepositoryDBTest.class: test/repository/KakuroRepositoryDBTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/repository/KakuroRepositoryDBTest.java

test/repository/UserRepositoryDBTest.class: test/repository/UserRepositoryDBTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/repository/UserRepositoryDBTest.java

test/GameTest.class: test/GameTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/GameTest.java

test/KakuroTest.class: test/KakuroTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/KakuroTest.java

test/UserTest.class: test/UserTest.java
	javac -cp .:lib/apiguardian-api-1.1.0.jar:lib/junit-jupiter-5.7.0.jar:lib/junit-jupiter-api-5.7.0.jar:lib/junit-jupiter-params-5.7.0.jar:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/mockito-core-2.0.111-beta.jar test/UserTest.java


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

run-tests: test/SolverTest.class test/SwappingCellQueueTest.class test/GeneratorTest.class test/repository/BoardRepositoryDBTest.class test/repository/DBTest.class test/repository/KakuroRepositoryDBTest.class test/repository/UserRepositoryDBTest.class test/GameTest.class test/KakuroTest.class test/UserTest.class
	java -jar lib/junit-platform-console-standalone-1.7.0.jar -cp .:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar:lib/byte-buddy-1.4.17.jar:lib/mockito-core-2.0.111-beta.jar:test/SolverTest.class --scan-classpath

run-app: app
	java -cp .:lib/byte-buddy-1.4.17.jar:lib/gson-2.8.6.jar:lib/objenesis-2.4.jar src/Main


# Cleanup
clean:
	find src app test -name "*.class" -type f -delete
	rm -f kakurosolver.tar.gz
	rm -f kakurogenerator.tar.gz
