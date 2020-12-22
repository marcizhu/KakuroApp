# Kakuro App

Repository containing the Java source code for a Kakuro application.

## Kakuro format

The format used for input and output in all applications is rather simple: it consists of a first row indicating the number of rows and the number of columns, separated by a comma.

Then, there are a list of lines (as many as rows), and for each row there's a list of comma-delimited strings (as many as columns) that **must** follow the following format:

- A string with `*` represents a black cell with no specified sum for a row or a column

- A string with `C` followed by a number in the range \[0, 45] represents a black cell with a specified sum for that column.

- A string with `F` followed by a number in the range \[0, 45] represents a black cell with a specified sum for that row.

- A string with `CxFy` represents a black cell with sum `x` for that column and sum `y` for that row

- A string with `?` represents an empty white cell.

- A string containing a number from 0 to 9 represents a white cell with that value.

An example file following this format might look like this:

```
9,9 
*,*,C19,C12,*,*,*,C7,C10
*,F14,?,?,C4,C11,C17F4,?,? 
*,C7F36,?,?,?,?,?,?,? 
F12,?,?,F10,?,?,?,C25,C14 
F3,?,?,C20,C11F20,?,?,?,? 
F17,?,?,?,?,C8,F6,?,? 
*,C11,C7F13,?,?,?,C4F10,?,? 
F28,?,?,?,?,?,?,?,* 
F6,?,?,*,*,F8,?,?,*
```

## Kakuro solver app

The source for the Kakuro solver application is inside `app/SolverApp.java`. It is a pretty straightforward application used to solve Kakuros. The application reads a Kakuro from the standard input following the specified format, solves it (it might take up to a few minutes depending on the size and complexity of the Kakuro) and then outputs two things: the first row of of the output is a number. It can be `0`, `1` or `2`:

- `0` indicates that this kakuro is unsolvable, i.e. there is no solution that satisfies all restrictions exisiting in the board.

- `1` specifies that there is exactly one solution. 

- `2` indicated that there are **at least** two solutions. There could be more, but our solver stops once it has found two solutions for performance sake.

If the first line is either a `1` or a `2`, it is followed by the first solution found by the solver. The output is written using the format described above.

## Kakuro generator app

The source of this application can be found inside `app/GeneratorApp.java`. This application reads three parameters from the command line. The first parameter is the width of the board to generate, the second parameter is the height of the board to generate and the third one is the difficulty of the board specified as a number from 1 to 4, where `1` is `EASY`, `2` is `MEDIUM`, `3` is `HARD` and `4` is `EXTREME`.

If this application is called with no parameters or with the wrong number of parameters, it will print some help output about how this application should be used.

If the given parameters are correct, then the generation process begins and finally the generated Kakuro is printed to the standard output using the specified format. Please note that the generation time depends on three factors: first, it depends on the size of the kakuro to generate (bigger kakuros take more time than smaller ones); second, it depends on the difficulty (easy Kakuros can be generated faster than say hard or extreme kakuros); and finally it depends on the random seed used to generate Kakuros. This means that given the same size and the same difficulty, some Kakuros might generate a lot faster than others.



## Building & executing

This repository was built using IntelliJ IDEA, so it can easily be built using it. In case this IDE is not available for building, we also provide a Makefile to compile all applications. Simply run `make` on the root folder to build the solver app, the generator app and the unit tests. You can also use `make solver` to _only_ build the solver app, `make generator` to _only_ build the generator app, `make tests` to _only_ build unit tests and finally `make app` to _only_ make the main application.

This makefile also allows to run the tests as well as all the apps:

- To execute the tests, just execute `make run-tests`. The execution of all tests should take around one minute (the first test takes ~50s, the others should run in a few milliseconds each)

- To execute the solver use `make run-solver`. Since the program reads from standard input, stdin redirections can be used to pipe a kakuro to the application. For example, try `make run-solver < data/kakuros/unsolved/sample.kak`. Alternatively, using `java app/SolverApp < file.txt` after a `make solver` should work just as fine.

- To execute the main application use either `make run-app` or `java src/Main`. This application requires no command line parameters to be executed. Additionally, we have provided a `.jar` file for the application, so you can run that by using `java -jar bin/app.jar`. Any of these commands should result in the app opening without any issue. However, the JAR version has its own database (located at `bin/data/DB`), so changes in that application will NOT be shown if the app is executed using any of the first two commands. 

- However, to execute the generator you **must** use `java app/GeneratorApp <width> <height> <difficulty>`. This is due to the fact that you cannot pipe command-line arguments through `make`. Running `make run-generator` will execute the app but since it will not recieve command line arguments, it will only display the help output and exit. For example, try running `java app/GeneratorApp 10 10 4` to generate an extreme 10 by 10 Kakuro! :D
