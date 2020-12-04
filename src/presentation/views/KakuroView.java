package src.presentation.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KakuroView extends JPanel {
    public static final int BLACK_SECTION_TOP = 1;
    public static final int BLACK_SECTION_BOTTOM = 2;
    public static final int BLACK_SECTION_LEFT = 3;
    public static final int BLACK_SECTION_RIGHT = 4;

    private BoardView boardView;

    private BoardMouseEventListener listener;

    public KakuroView(String board, boolean showValues) {
        Matcher m = Pattern.compile("^C(\\d+)$|^F(\\d+)$|^C(\\d+)F(\\d+)$").matcher("");
        String[] rows = board.split("\\n");
        String[] line1 = rows[0].split(",");

        int height = Integer.parseInt(line1[0].trim());
        int width  = Integer.parseInt(line1[1].trim());

        assert(rows.length - 1 == height);

        boardView = new BoardView(height+1, width+1);
        JPanel[][] cells = new JPanel[height+1][width+1];

        int[] currColSums = new int[width];
        int currRowSum = 0;

        for(int i = 0; i < height; i++) {
            String[] cols = rows[i + 1].split(",");
            assert(cols.length == width);

            for (int j = 0; j < width; j++) {
                cols[j] = cols[j].trim();
                m.reset(cols[j]);

                if (cols[j].equals("*")) {
                    cells[i][j] = new BlackCellView(i, j, currColSums[j], 0, currRowSum, 0, showValues);
                    currRowSum = 0;
                    currColSums[j] = 0;
                }
                else if(cols[j].equals("?")) {
                    cells[i][j] = new WhiteCellView(i, j, 0, 0, showValues);
                }
                else if(m.find()) {
                    int col = 0;
                    int row = 0;

                    /**/ if(m.group(1) != null) col = Integer.parseInt(m.group(1));
                    else if(m.group(2) != null) row = Integer.parseInt(m.group(2));
                    else if(m.group(3) != null && m.group(4) != null) {
                        col = Integer.parseInt(m.group(3));
                        row = Integer.parseInt(m.group(4));
                    }

                    cells[i][j] = new BlackCellView(i, j, currColSums[j], col, currRowSum, row, showValues);
                    currRowSum = row;
                    currColSums[j] = col;
                } else {
                    int val = Integer.parseInt(cols[j]);
                    cells[i][j] = new WhiteCellView(i, j, val, 0, showValues);
                }
            }
            cells[i][width] = new BlackCellView(i, width, 0, 0, currRowSum, 0, showValues);
            currRowSum = 0;
        }
        for (int j = 0; j < width; j++)
            cells[height][j] = new BlackCellView(height, j, currColSums[j], 0, 0, 0, showValues);
        cells[height][width] = new BlackCellView(height, width, 0, 0, 0, 0, showValues);

        this.setLayout(new GridBagLayout());
        boardView.setCells(cells);
        this.add(boardView);
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
        this.setVisible(true);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        boardView.setSize(width, height);
    }

    public void setBoardMouseEventListener(BoardMouseEventListener l) {
        if (listener != null) listener.onListenerDetached();
        this.listener = l;
    }

    private class BoardView extends JPanel {
        private int rows, columns, cellSideSize;
        private JPanel[][] cells;

        public BoardView(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            computeCellSideSize(getWidth(), getHeight());
            setLayout(new GridLayout(rows, columns));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setVisible(true);
        }

        private void computeCellSideSize(int width, int height) {
            if(width/columns < height/rows)// horizontally constrained
                cellSideSize = width/columns;
            else
                cellSideSize = height/rows;
        }

        public void setCells(JPanel[][] CC) {
            cells = CC;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    add(CC[r][c]);
                }
            }
        }

        @Override
        public void setSize(int width, int height) {
            computeCellSideSize(width, height);
            super.setSize(cellSideSize*columns, cellSideSize*rows);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    cells[r][c].setSize(cellSideSize, cellSideSize);
                }
            }
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(this.columns*this.cellSideSize, this.rows*this.cellSideSize);
        }
    }

    private class BlackCellView extends JPanel {
        private int row, col;
        private boolean topToPaint, bottomToPaint, leftToPaint, rightToPaint;
        private JLabel topLbl, bottomLbl, leftLbl, rightLbl;
        public BlackCellView (int row, int col, int top, int bottom, int left, int right, boolean showValues) {
            this.row = row;
            this.col = col;
            topToPaint = top!=0;
            topLbl = new JLabel("", JLabel.CENTER);
            topLbl.setForeground(Color.WHITE);
            topLbl.setOpaque(false);
            bottomToPaint = bottom!=0;
            bottomLbl = new JLabel("", JLabel.CENTER);
            bottomLbl.setForeground(Color.WHITE);
            leftToPaint = left!=0;
            leftLbl = new JLabel("", JLabel.CENTER);
            leftLbl.setForeground(Color.WHITE);
            rightToPaint = right!=0;
            rightLbl = new JLabel("", JLabel.CENTER);
            rightLbl.setForeground(Color.WHITE);
            if (showValues) {
                if (topToPaint) topLbl.setText(""+top);
                if (bottomToPaint) bottomLbl.setText(""+bottom);
                if (leftToPaint) leftLbl.setText(""+left);
                if (rightToPaint) rightLbl.setText(""+right);
                setLayout(new GridLayout(3,3));
                add(transparentSpace());
                add(topToPaint ? topLbl : transparentSpace());
                add(transparentSpace());
                add(leftToPaint ? leftLbl : transparentSpace());
                add(transparentSpace());
                add(rightToPaint ? rightLbl : transparentSpace());
                add(transparentSpace());
                add(bottomToPaint ? bottomLbl : transparentSpace());
                add(transparentSpace());
            }
            setVisible(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int section;
                    int relative_x = e.getX() - getLocation().x;
                    int relative_y = e.getY() - getLocation().y;

                    //this works because cells are always squares, if they weren't just multiply by ratio
                    if (relative_x > relative_y) {
                        if (getWidth() - relative_x > relative_y) section = BLACK_SECTION_TOP;
                        else section = BLACK_SECTION_RIGHT;
                    } else {
                        if (relative_x > getHeight() - relative_y) section = BLACK_SECTION_BOTTOM;
                        else section = BLACK_SECTION_LEFT;
                    }

                    if (listener != null)
                        listener.onBlackCellViewClicked(row, col, section);
                }

                @Override
                public void mousePressed(MouseEvent e){
                    if (listener != null)
                        listener.onCellInBoardPressed(row, col);
                }

                @Override
                public void mouseReleased(MouseEvent e){
                    if (listener != null)
                        listener.onCellInBoardReleased(row, col);
                }

                @Override
                public void mouseEntered(MouseEvent e){
                    if (listener!=null)
                        listener.onBlackCellViewEntered(row, col);
                }
            });
        }

        // FIXME: Ugly solution, I don't know if there's a better way to do this
        private JPanel transparentSpace() {
            JPanel p = new JPanel();
            p.setVisible(false);
            return p;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Color c = g.getColor();
            int centerX = getWidth()/2, centerY = getHeight()/2;
            int topLeftX = 0, topLeftY = 0;
            int topRightX = getWidth(), topRightY = 0;
            int bottomLeftX = 0, bottomLeftY = getHeight();
            int bottomRightX = getWidth(), bottomRightY = getHeight();
            if (topToPaint) {
                // FIXME: Hardcoded primary color, might want to make it a variable that can change
                g.setColor(new Color(20,145,20));
                g.fillPolygon(new int[] { topLeftX, topRightX, centerX }, new int[] { topLeftY, topRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topLeftX, topRightX, centerX }, new int[] { topLeftY, topRightY, centerY }, 3);
            }
            if (bottomToPaint) {
                g.setColor(new Color(20,145,20));
                g.fillPolygon(new int[] { bottomLeftX, bottomRightX, centerX }, new int[] { bottomLeftY, bottomRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { bottomLeftX, bottomRightX, centerX }, new int[] { bottomLeftY, bottomRightY, centerY }, 3);
            }
            if (leftToPaint) {
                g.setColor(new Color(20,145,20));
                g.fillPolygon(new int[] { topLeftX, bottomLeftX, centerX }, new int[] { topLeftY, bottomLeftY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topLeftX, bottomLeftX, centerX }, new int[] { topLeftY, bottomLeftY, centerY }, 3);
            }
            if (rightToPaint) {
                g.setColor(new Color(20,145,20));
                g.fillPolygon(new int[] { topRightX, bottomRightX, centerX }, new int[] { topRightY, bottomRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topRightX, bottomRightX, centerX }, new int[] { topRightY, bottomRightY, centerY }, 3);
            }
            g.setColor(c);
            paintComponents(g);
        }

        public void setTopSectionValue(int value) {
            topToPaint = value != 0;
            //topLbl
        }
    }

    private class WhiteCellView extends JPanel {
        private final int row, col;
        private int value, notations;
        private boolean showValues;

        public WhiteCellView (int row, int col, int value, int notations, boolean showValues) {
            this.row = row;
            this.col = col;
            this.value = value;
            this.notations = notations;
            this.showValues = showValues;
            if (showValues) {
                resetLayout();
            }
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setVisible(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (listener != null)
                        listener.onWhiteCellViewClicked(row, col);
                }

                @Override
                public void mousePressed(MouseEvent e){
                    if (listener != null)
                        listener.onCellInBoardPressed(row, col);
                }

                @Override
                public void mouseReleased(MouseEvent e){
                    if (listener != null)
                        listener.onCellInBoardReleased(row, col);
                }

                @Override
                public void mouseEntered(MouseEvent e){
                    if (listener != null)
                        listener.onWhiteCellViewEntered(row, col);
                }
            });
        }

        public void resetLayout() {
            if (!showValues) return;
            if (value != 0) {
                setLayout(new BorderLayout());
                JLabel valueLbl = new JLabel(""+value);
                valueLbl.setForeground(Color.BLACK);
                valueLbl.setOpaque(true);
                valueLbl.setHorizontalAlignment(SwingConstants.CENTER);
                add(valueLbl);
            } else {
                setLayout(new GridLayout(3,3));
                for (int i = 0; i < 9; i++) {
                    JLabel notationLbl = new JLabel();
                    notationLbl.setForeground(Color.GRAY);
                    notationLbl.setOpaque(true);
                    notationLbl.setHorizontalAlignment(SwingConstants.CENTER);
                    if ((notations & (1<<i)) != 0) notationLbl.setText(""+(i+1));
                    add(notationLbl);
                }
            }
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setNotations(int notations) {
            this.notations = notations;
        }
    }

    public interface BoardMouseEventListener {
        void onBlackCellViewClicked(int row, int col, int section);
        void onWhiteCellViewClicked(int row, int col);
        void onBlackCellViewEntered(int row, int col);
        void onWhiteCellViewEntered(int row, int col);
        void onCellInBoardPressed(int row, int col);
        void onCellInBoardReleased(int row, int col);

        void onListenerDetached();
    }
}
