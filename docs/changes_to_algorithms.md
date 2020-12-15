# Changes to the algorithms from the last assignment

## Solver Algorithm

First of all, it is important to note that we changed the solver algorithm from a purely backtracking algorithm to an inference deduction algorithm which falls back to backtracking if a solution can't be found using only deduction. This new optimization, which greatly sped up the solver is explained in depth in section `Optimization #4: Use inference to speed up the solving process` of the file `solver_algorithm.md`

This inference uses many of the same functions from the generator, which where moved to their own class, `KakuroFunctions`, in order to both simplify the generator code and the reusability of the project as a whole.

Aside from this, another important changed to the solver is that it now doesn't check all white cells from left to right and top to bottom, but instead it just checks the cells which are NOT empty, and it does this in increasing order of possibilities (that is, it first evaluates the cells with less possible values in order to cut off branches and speed up the solving process). This optimization is described in detail in section `Optimization #3: Process cells in order of possibilities` of the file `solver_algorithm.md`.


## Generator Algorithm

A big change from the previous assignment is, as mentioned in the [previous section](#solver-algorithm), that we moved most of the functions related to the elimination of possibilities by inference from the file `Generator.java` to a new file (and class) called `KakuroFunctions.java`. This change has many reasons:

- First and most important, it keeps files small and organized, and each file only contains code to perform a single task. For example, the `Solver.java` file only contains the solver algorithm, the `Generator.java` file only contains code related to generating boards, etc...

- Second, it increases reusability of code. This change allowed us to reuse some functions in order to implement inference into the solver algorithm. We believe that reusing good, tested, generalized code is always a good thing and we aimed at this objective throughout the entire project.

Another important change in this algorithm is the generation of black & white tiles. Our previous version relied on much, much simpler methods to generate the kakuro which led to some issues:

- **Non-connected kakuro:** We consider a graph to be connected if for every pair of vertices _u_, _v_ there exists a path from _u_ to _v_ or viceversa. Similarly, we consider a kakuro to be connected if, for a given pair of white cells, we could navigate from one to the other whithout having to navigate through black cells. The rules of this game state that a kakuro must be connected, and our generator, many times made kakuros with too many disconnected components.

- **Lines of length 1:** In the version of the generator that we handed in at the last assignment, it was fairly common to find rows or columns of just one white cell as a result of avoiding lines of length greater than nine. With the new method we try to minimize the probability of this happening by taking it into account when trying to avoid lines of length greater then nine. It can still happen, however, when all positions of a "long line" are at distance 2 of a black cell, or if turning a cell that wouldn't be problematic in this aspect to black would create a disconnected graph.

- **Lack of symmetry:** Many of the kakuros you might find online have some sort of simmetry. Kakuros might be symmetric along a single edge, along multiple edges or along a diagonal line on the board. This symmetry is added mostly for aesthetic reasons and plays no important role on the generation, complexity or playability of the final kakuro. Nevertheless, we wanted our kakuros to be comparable to any other kakuro app, and thus we wanted to fix this small issue.

The three of this problems where mostly fixed from the last assignment, and now our algorithm generates beautiful symmetric boards with just a single strongly-connected component.

This changes have been documented in depth in their respective document. For more information, please see the `Step 1` part of the section `The algorithm` in the document `generator_algorithm.pdf` (pages 3 and 4), where it is explained how we fixed this problems.

Further improvements could be done to the process of generation of white/black cells by (when trying to partition a line of length greater than nine) inspecting the possibility of turning problematic black cells back to white (to avoid length-1 lines or disconnected kakuros in these few cases we mentioned).


## The kakuro inference functions

These functions that now are used by both the Solver algorithm and the Generator algorithm have also received a couple of improvements since last delivery that aim to make it able to eliminate possibilities of values in more complex cases. 

There is now a function that analyzes each combination returned by KakuroConstants for a row/column and decides if it is possible to assign it to the row/column. This decision can get rid of certain possibilities that were previously contemplated as possible.

There is a new function that given the possible values of all the cells of the row/column decides if some possibilities must be reserved to a subset of white cells and thus, should be subtracted from the rest of white cells that have it in their current possibilities. For instance, take three cells, one of them has possibilities 1,2,3 and the other two have possibilities 1,2; then the values 1 and 2 should be reserved to the two last cells and the first one should take value 3. This example is simple but the algorithm contemplates cases for all possible lengths.
