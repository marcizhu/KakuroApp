# Kakuro solver algorithm

In this brief essay we will explain the outline of our Kakuro solver algorithm and dive into some details of the optimizations done in order to speed up the solving.

Our solver is capable of solving empty Kakuros as well as checking if a given solution is valid (i.e. a Kakuro with no empty white cells) or if a partially filled Kakuro is solvable. In addition, it can check if the given Kakuro has multiple solutions (two or more), only one solution or no solutions.



## Main algorithm

The solver is based on a inference algorithm which falls back to backtracking. It will try to deduce the values of all cells based on the row/column size, numbers already placed on nearby white cells, etc... If at any point, a white cell can't have any numbers placed in it, then the number of solutions is zero and the solver returns said value. If instead this deduction algorithm can place a single number in each and every white cell, then the given kakuro had only a single solution and it has been successfully solved.

Finally, if the algorithm reaches a point where there are empty white cells, each one with multiple values that can be placed on it, and it is impossible to deduce any value, the algorithm falls back to a backtracking solver.

This solver processes all remaining white cells, evaluating first the cells with less possible values.


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


### Optimization #3: Process cells in order of possibilities

Our first algorithm tried to solve the kakuro by evaluating white cells from left to right and from top to bottom. Our new approach processes first cells with less possibilities (for example, cells where only two or three values can be placed in that cell) before evaluating cells with more possibilities.

While this improvement might not seem a big deal, it proved one of the most important optimizations done to the solver. This one, together with the optimization described in the next section, sped up the solver by a factor of around ~80-100x according to our (somewhat informal) benchmarks.


### Optimization #4: Use inference to speed up the solving process

As described in the introduction of this document, our solver is now based on a inference algorithm which falls back to backtracking if a solution can't be found. Even if it has to do backtracking to find a solution, the number of branches that it has to check is dramatically reduced, since now many white cells have a number already placed on them.


### Further improvement

Of course, our solver is not perfect nor the fastest in the world. Some extra optimizations that we could perform are, for example, to use faster data structures in order to optimize the way the inference is done, allowing a faster deduction process and further decreasing the total time required by the algorithm.

Finally, another optimization that we could perform is to add more deduction rules to the deduction algorithm. This optimization would greatly improve performance, because if the inference algorithm can find a solution for kakuros with unique solutions without having to call the backtracker, it would mean that a solution can be found in `O(n^2)` worst case instead of the `O(9^n)` worst-case time required by the backtracking algorithm.
