
public class Solver {
	Board board;
	int solutions;

	public Solver(Board b) {
		this.board = new Board(b);
        this.solutions = -1;
    }

    public Solver(String formattedBoard) {
		this.board = new Board(formattedBoard);
        this.solutions = -1;
    }

    public void solve() {
        solutions = 0;
        solve(0, 0);
    }

    private void solve(int row, int col) {
        if (we reached the end) { return; }
        ArrayList<int> possibleValues = getPossibleValues();
        for (int i in possibleValues) {
            board.setCellValue(row, col, i);
            Pair<Integer, Integer> nextPos = board.getNextPos(row, col);
            solve(nextPos.getValue0(), nextPos.getValue1()) // potser ens passem de la fila
            if (solutions > 1) return;
        }
        
    }

	public Board retrieveResult() {
		return board;
    }
}
