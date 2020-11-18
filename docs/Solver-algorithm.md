# Kakuro solver algorithm

In this brief essay we will explain the outline of our Kakuro solver algorithm and dive into some details of the optimizations done in order to speed up the solving.

Our solver is capable of solving empty Kakuros as well as checking if a given solution is valid (i.e. a Kakuro with no empty white cells) or if a partially filled Kakuro is solvable. In addition, it can check if the given Kakuro has multiple solutions (two or more), only one solution or no solutions.



## Main algorithm

The solver is based on a backtracking algorithm. It will process all cells from left to right and top to bottom. Depending on the cell it finds, it will perform different actions:

- **White cell:** If a white cell is being evaluated, then the algorithm will try one of the possible values that could go on that cell. After a number has been placed in a cell, the algorithm tries to solve the next cell. If any of the possible values for this cell yield any valid solution, then this function returns, therefore backtracking and allowing the previous white cell to get a new value and continue solving.

- **Black cell:** When a black cell is being evaluated, we first check if the row sum is correct or not. If it is not correct, we backtrack. Else, we also check if the value of column on top (if it exists) is valid. In case it is not valid, we backtrack once again. Finally, if both sums are correct, we continue solving the next cell.

As we said, this process is repeated for all cells from left to right and top to bottom until we either find two solutions (indicating that the given Kakuro had at least two or more solutions), one solution (meaning that the given Kakuro only had a unique solution) or no solution was found and all possibilities were explored.



## Optimizations

If we check every single possible value for each possible cell in the entire board we would eventually find the solution, but it would take too long. This is why we decided to perform some optimizations, which we will describe in this section.



### Optimization #1: Preprocessing the board

In order to check if the row or column sum are correct, we first need to know the expected sums. We could of course use a simple loop to go to the left until we find the black cell that indicates the row sum and another loop to go up and find the total column sum, but this approach would be too slow and would require lots of iterations over the same cells. This is why we decided to create different matrices that indicate the total row sum and total column sum for any white cell. This way, we can check this sums in `O(1)` time instead of `O(n)` time.

While we were at it, we also decided to add matrices to easily retrieve the number of cells in any given row or column. This might seem unnecessary but it proved key for other optimizations to work properly and efficiently.



### Optimization #2: Only evaluate possible combinations

Given a total sum and the number of white cells in a row or column, only a small number of combinations are possible. Thus, if we only check those possible combinations, we can dramatically reduce the number of branches our algorithm has to check.

To perform this task, we created the class `KakuroConstants`, which uses a small backtracking algorithm to generate all possible combinations of numbers given a size and total sum of a row at initialization time. Then, once the solver is initialized, we can get the possible values for a cell in constant time.

In our case, for each white cell we get the possible values for that row and column, and we then do the intersection. This intersection further reduces the number of branches checked by the solver because we skip the numbers that could fit in a row or column but not both.

But we still can do a bit more in order to reduce the number of branches: say we have a row (or column) with three white cells and a total sum of 8. The possible combinations for that row are only 1, 2, 5 _or_ 1, 3, 4 (in any order). Now supose we already have a 5 in that row. Obviously, the only possible numbers for the other two white cells are 1 and 2, since we already have a 5 in there and replacing either the 1 or the 2 with a 3 or a 4 would exceed the total sum of the row. Thus, we wanted to add this small optimization to our number selection algorithm.

To implement this last bit of optimization, we use some matrices that hold the numbers already present in each row and column of the board, and then we "filter" the possible values given by the class `KakuroConstants` to reduce the possibilities even further.
