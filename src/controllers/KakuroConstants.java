package src.controllers;

import src.domain.Difficulty;
import src.utils.Pair;

import java.util.ArrayList;

/**
 * Kakuro Constants.
 * Contains all possible combinations for different row/column size and sum.
 *
 * @version 0.1.0 (17/11/2020)
 */

public class KakuroConstants {
    /**
     * Singleton instance of this class
     */
    public static final KakuroConstants INSTANCE = new KakuroConstants();

    private ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> cases;
    private final int[] numOfSumsAtSpace = { 9, 15, 19, 21, 21, 19, 15, 9, 1 };
    private final int[] firstSumAtSpace = { 1, 3, 6, 10, 15, 21, 28, 36, 45 };

    private KakuroConstants() {
        instantiateHashMaps();
    }

    /**
     * Get possible cases for a row or column with the given size and sum
     * @param space Number of 'slots' of the row or column
     * @param sum   Total sum of the row or column
     * @return an ArrayList of ArrayList containing all possible cases for this row or column (without permutations)
     */
    public ArrayList<ArrayList<Integer>> getPossibleCases(int space, int sum) {
        return (ArrayList<ArrayList<Integer>>) cases.get(space).get(sum).clone();
    }

    /**
     * Similar to @link KakuroConstants::getPossibleCases(), but for non-empty rows or columns where some values are
     * already placed
     * @param space  Number of 'slots' of the row or column
     * @param sum    Total sum of the row or column
     * @param values Boolean array representing numbers already present in the row or column
     * @return an ArrayList of ArrayList containing all possible cases for this row or column (without permutations)
     */
    public ArrayList<ArrayList<Integer>> getPossibleCasesWithValues(int space, int sum, boolean[] values) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        int sizeOfValues = 0;
        for (boolean b : values) if (b) sizeOfValues++;
        if (sizeOfValues > space) return result; //if there are more values than space available (or same) there is no possibility to add any more numbers

        ArrayList<ArrayList<Integer>> possible = cases.get(space).get(sum);
        if (sizeOfValues == 0) return (ArrayList<ArrayList<Integer>>) possible.clone(); // if no values are specified, it will return all possibilities

        for (ArrayList<Integer> p : possible) {
            int idx = 1;
            boolean noValuesFound = true;
            for (int i = 0; i < p.size() && noValuesFound; i++) {
                Integer o = p.get(i);
                while (idx < o && noValuesFound) {
                    noValuesFound = !values[idx-1]; // if there is a value in values[] and not in an option p, the option is not valid
                    idx++;
                }
                idx++;
            }
            // check up to position 9 if it hadn't reached there yet
            while (idx < 10 && noValuesFound) {
                noValuesFound = !values[idx-1]; // if there is a value in values[] and not in an option p, the option is not valid
                idx++;
            }
            if (noValuesFound) result.add((ArrayList<Integer>) p.clone());   // otherwise we add p to the array of valid options "result"
        }

        return result;
    }

    /**
     * Get possible cases given the number of white cells, regardless of the total sum
     * @param space  Number of white cells in the row or column
     * @param values Values already present in that row or column, represented as a boolean[9] array
     * @return the combinations of values that would fit in that row or column, as well as the total sum in each case
     */
    public ArrayList<Pair<Integer, ArrayList<Integer>>> getPossibleCasesUnspecifiedSum(int space, boolean[] values) {
        ArrayList<Pair<Integer, ArrayList<Integer>>> result = new ArrayList<>();
        if (space < 1 || space > 9) return result;
        int sizeOfValues = 0;
        for (boolean b : values) if (b) sizeOfValues++;
        if (sizeOfValues > space) return result; //if there are more values than space available there is no possibility to add any more numbers

        int numOfSums = numOfSumsAtSpace[space-1];

        for (int i = 0; i < numOfSums; i++) {
            int sum = firstSumAtSpace[space-1]+i;
            ArrayList<ArrayList<Integer>> possibilities = getPossibleCasesWithValues(space, sum, values);
            for (ArrayList<Integer> p : possibilities) result.add(new Pair<>(sum, (ArrayList<Integer>) p.clone()));
        }
        return result;
    }

    /**
     * Get unique cross values given a row size, a column size and the difficulty of the kakuro
     * @param rowSpace Number of white cells in the row
     * @param colSpace Number of white cells in the column
     * @param diff     Difficulty of the kakuro being generated
     * @return an ArrayList of values unique in that cell
     */
    // TODO: as it is, it always returns the possibilities in a specific order for the same input, maybe should apply some randomness to the order of the partial solutions
    public ArrayList<int[]> getUniqueCrossValues(int rowSpace, int colSpace, Difficulty diff) {
        ArrayList<int[]> result = new ArrayList<>(); // all int[] will have 3 values: {rowSum, colSum, uniqueValueInCommon}
        if (rowSpace < 1 || rowSpace > 9 || colSpace < 1 || colSpace > 9) return result;

        int rowNumOfSums = numOfSumsAtSpace[rowSpace-1], colNumOfSums = numOfSumsAtSpace[colSpace-1];

        ArrayList<int[]> partialEasy = new ArrayList<>();
        ArrayList<int[]> partialMedium = new ArrayList<>();
        ArrayList<int[]> partialHard = new ArrayList<>();
        ArrayList<int[]> partialExtreme = new ArrayList<>();

        for (int rowIdx = 0; rowIdx < rowNumOfSums; rowIdx++) {
            // How many times is a certain value seen for a given space and a given sum in the row
            int rowSum = firstSumAtSpace[rowSpace-1]+rowIdx;
            ArrayList<ArrayList<Integer>> rowOptions = cases.get(rowSpace).get(rowSum);
            boolean[] rowValuesSeen = { false, false, false, false, false, false, false, false, false };
            for (ArrayList<Integer> rowOption : rowOptions) {
                for (Integer value : rowOption) {
                    rowValuesSeen[value-1] = true;
                }
            }

            for (int colIdx = 0; colIdx < colNumOfSums; colIdx++) {
                // How many times is a certain value seen for a given space and a given sum in the col
                int colSum = firstSumAtSpace[colSpace-1]+colIdx;
                ArrayList<ArrayList<Integer>> colOptions = cases.get(colSpace).get(colSum);
                boolean[] colValuesSeen = { false, false, false, false, false, false, false, false, false };
                for (ArrayList<Integer> colOption : colOptions) {
                    for (Integer value : colOption) {
                        colValuesSeen[value-1] = true;
                    }
                }

                int uniqueCrossValuePos = -1;
                for (int i = 0; i < 9 && uniqueCrossValuePos != -2; i++) {
                    if (rowValuesSeen[i] && colValuesSeen[i]) {
                        if (uniqueCrossValuePos == -1) uniqueCrossValuePos = i; // there is one value in common
                        else uniqueCrossValuePos = -2; // there is more than one value in common
                    }
                }

                if (uniqueCrossValuePos >= 0) {
                    // Depending on the rowIdx and colIdx we consider them more or less difficult options
                    int rowOptDiff;
                    if (rowIdx+1 > rowNumOfSums*3/8 && rowIdx+1 < rowNumOfSums*5/8) rowOptDiff = 5;
                    else if (rowIdx+1 > rowNumOfSums/4 && rowIdx+1 < rowNumOfSums*3/4) rowOptDiff = 3;
                    else if (rowIdx+1 > rowNumOfSums/8 && rowIdx+1 < rowNumOfSums*7/8) rowOptDiff = 1;
                    else rowOptDiff = 0;

                    int colOptDiff;
                    if (colIdx > colNumOfSums*3/8 && colIdx < colNumOfSums*5/8) colOptDiff = 5;
                    else if (colIdx > colNumOfSums/4 && colIdx < colNumOfSums*3/4) colOptDiff = 3;
                    else if (colIdx > colNumOfSums/8 && colIdx < colNumOfSums*7/8) colOptDiff = 1;
                    else colOptDiff = 0;

                    /* Depending on the difficulty of the options we assign it to its partial solution, so we can
                     *   return all the options ordered in the difficulty asked for.
                     * */
                    int diffValue = rowOptDiff + colOptDiff;
                    if (diffValue > 7) // EXTREME
                        partialExtreme.add(new int[] { rowSum, colSum, uniqueCrossValuePos+1 });
                    else if (diffValue > 4) // HARD
                        partialHard.add(new int[] { rowSum, colSum, uniqueCrossValuePos+1 });
                    else if (diffValue > 1) // MEDIUM
                        partialMedium.add(new int[] { rowSum, colSum, uniqueCrossValuePos+1 });
                    else // EASY
                        partialEasy.add(new int[] { rowSum, colSum, uniqueCrossValuePos+1 });
                }
            }
        }

        switch (diff) {
            case EASY:
                partialEasy.addAll(partialMedium);
                partialEasy.addAll(partialHard);
                partialEasy.addAll(partialExtreme);
                result = partialEasy;
                break;
            case MEDIUM:
                partialMedium.addAll(partialEasy);
                partialMedium.addAll(partialHard);
                partialMedium.addAll(partialExtreme);
                result = partialMedium;
                break;
            case HARD:
                partialHard.addAll(partialExtreme);
                partialHard.addAll(partialMedium);
                partialHard.addAll(partialEasy);
                result = partialHard;
                break;
            case EXTREME:
                partialExtreme.addAll(partialHard);
                partialExtreme.addAll(partialMedium);
                partialExtreme.addAll(partialEasy);
                result = partialExtreme;
                break;
        }

        return result;
    }

    private void instantiateHashMaps() {
        cases = new ArrayList<>(10);
        cases.add(new ArrayList<>());
        for (int i = 1; i <= 9; i++) {
            ArrayList<ArrayList<ArrayList<Integer>>> al = new ArrayList<>(46);

            for(int j = 0; j <= 45; j++)
                al.add(new ArrayList<>());

            int numOfSums = numOfSumsAtSpace[i-1];
            for (int j = 0; j < numOfSums; j++) {
                int currentSum = firstSumAtSpace[i-1]+j;
                al.set(currentSum, findCombinations(i, currentSum));
            }
            cases.add(al);
        }
    }

    private ArrayList<ArrayList<Integer>> findCombinations(int space, int sum) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        boolean[] values = { false, false, false, false, false, false, false, false, false };
        backtrackingFindCombinations(1, space, sum, 0, 0, values, result);

        return result;
    }

    private void backtrackingFindCombinations(int idx, int space, int sum, int currentSpace, int currentSum, boolean[] values, ArrayList<ArrayList<Integer>> found) {
        if (idx > 9 || currentSpace >= space) return;
        if (idx + currentSum == sum && currentSpace == space-1) {
            values[idx-1] = true;
            ArrayList<Integer> solution = new ArrayList<>();
            for (int i = 0; i < 9; i++) if (values[i]) solution.add(i+1);
            found.add(solution);
            values[idx-1] = false;
            return;
        }
        if (idx + currentSum < sum) {
            values[idx-1] = true;
            backtrackingFindCombinations(idx+1, space, sum, currentSpace+1, currentSum+idx, values, found);
            values[idx-1] = false;
            backtrackingFindCombinations(idx+1, space, sum, currentSpace, currentSum, values, found);
        }
    }
}
