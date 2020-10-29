package src.controllers;

import java.util.ArrayList;
import java.util.HashMap;

public class KakuroConstants {
    public static final KakuroConstants INSTANCE = new KakuroConstants();

    //TODO: I add this as a new singleton class, maybe should go inside the solver
    private HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Integer>>>> cases;

    private KakuroConstants() {
        instantiateHashMaps();
    }

    public ArrayList<ArrayList<Integer>> getPossibleCases(int space, int sum) {
        return cases.get(space).get(sum);
    }

    private void instantiateHashMaps() {
        final int[] startingValues = {1, 3, 6, 10, 15, 21, 28, 36, 45};
        cases = new HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Integer>>>>();
        for (int i = 1; i <= 9; i++) {
            HashMap<Integer, ArrayList<ArrayList<Integer>>> hm = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
            int numOfSums = -i*i+9*i+1;
            for (int j = 0; j < numOfSums; j++) {
                int currentSum = startingValues[i-1]+j;
                hm.put(currentSum, findCombinations(i, currentSum));
            }
            cases.put(i, hm);
        }
    }

    private ArrayList<ArrayList<Integer>> findCombinations(int space, int sum) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

        boolean[] values = {false, false, false, false, false, false, false, false, false};
        backtrackingFindCombinations(1, space, sum, 0, 0, values, result);

        return result;
    }

    private void backtrackingFindCombinations(int idx, int space, int sum, int currentSpace, int currentSum, boolean[] values, ArrayList<ArrayList<Integer>> found) {
        if (idx > 9 || currentSpace >= space) return;
        if (idx + currentSum == sum && currentSpace == space-1) {
            values[idx-1] = true;
            ArrayList<Integer> solution = new ArrayList<Integer>();
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
