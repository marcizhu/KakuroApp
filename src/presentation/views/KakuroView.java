package src.presentation.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KakuroView extends JPanel {
    public static final int BLACK_SECTION_TOP = 1;
    public static final int BLACK_SECTION_BOTTOM = 2;
    public static final int BLACK_SECTION_LEFT = 3;
    public static final int BLACK_SECTION_RIGHT = 4;
    private Color blackCellColor;


    private int rows, columns, cellSideSize;
    private JPanel[][] cells;

    private BoardMouseEventListener listener;

    public KakuroView(String board, boolean showValues) {
        blackCellColor = Color.BLACK;

        Matcher m = Pattern.compile("^C(\\d+)$|^F(\\d+)$|^C(\\d+)F(\\d+)$").matcher("");
        String[] rows = board.split("\\n");
        String[] line1 = rows[0].split(",");

        int height = Integer.parseInt(line1[0].trim());
        int width  = Integer.parseInt(line1[1].trim());

        assert(rows.length - 1 == height);

        this.rows = height+1;
        this.columns = width+1;
        cells = new JPanel[height+1][width+1];

        setLayout(new GridLayout(this.rows, this.columns));
        computeCellSideSize(getWidth(), getHeight());

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
                    add(cells[i][j]);
                    currRowSum = 0;
                    currColSums[j] = 0;
                }
                else if(cols[j].equals("?")) {
                    cells[i][j] = new WhiteCellView(i, j, 0, 0, showValues);
                    add(cells[i][j]);
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
                    add(cells[i][j]);
                    currRowSum = row;
                    currColSums[j] = col;
                } else {
                    int val = Integer.parseInt(cols[j]);
                    cells[i][j] = new WhiteCellView(i, j, val, 0, showValues);
                    add(cells[i][j]);
                }
            }
            cells[i][width] = new BlackCellView(i, width, 0, 0, currRowSum, 0, showValues);
            add(cells[i][width]);
            currRowSum = 0;
        }
        for (int j = 0; j < width; j++) {
            cells[height][j] = new BlackCellView(height, j, currColSums[j], 0, 0, 0, showValues);
            add(cells[height][j]);
        }
        cells[height][width] = new BlackCellView(height, width, 0, 0, 0, 0, showValues);
        add(cells[height][width]);

        this.setBackground(new Color(0,0,0,0));
        this.setVisible(true);
    }

    private void computeCellSideSize(int width, int height) {
        if(width/columns < height/rows)// horizontally constrained
            cellSideSize = width/columns;
        else
            cellSideSize = height/rows;
    }

    @Override
    public void setSize(int width, int height) {
        computeCellSideSize(width, height);
        super.setSize(cellSideSize*columns, cellSideSize*rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c].setSize(cellSideSize, cellSideSize);
                cells[r][c].revalidate();
            }
        }
        revalidate();
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

    public void setBoardMouseEventListener(BoardMouseEventListener l) {
        if (listener != null) listener.onListenerDetached();
        this.listener = l;
    }

    public void setWhiteCellValue(int r, int c, int value) {
        if (r < 0 || c < 0 || r >= rows || c >= columns) return;
        if (cells[r][c] instanceof WhiteCellView) ((WhiteCellView)cells[r][c]).setValue(value);
    }
    public void setWhiteCellNotations(int r, int c, int notations) {
        if (r < 0 || c < 0 || r >= rows || c >= columns) return;
        if (cells[r][c] instanceof WhiteCellView) ((WhiteCellView)cells[r][c]).setNotations(notations);
    }
    public void setBlackCellValue(int r, int c, int value, int section) {
        if (r < 0 || c < 0 || r >= rows || c >= columns) return;
        if (cells[r][c] instanceof BlackCellView) ((BlackCellView)cells[r][c]).setSectionValue(value, section);
    }

    public void setBlackCellColor(Color color) {
        blackCellColor = color;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                if (cells[r][c] instanceof BlackCellView)
                    ((BlackCellView)cells[r][c]).resetBlackCellColor();
    }

    public void setBlackCellSelectedColor(int r, int c, int section, Color color) {
        if (cells[r][c] instanceof WhiteCellView) return;
        ((BlackCellView)cells[r][c]).setSectionColor(section, color);
    }
    public void unselectBlackCell(int r, int c, int section) {
        if (cells[r][c] instanceof WhiteCellView) return;
        ((BlackCellView)cells[r][c]).unselectSection(section);
    }
    public void setWhiteCellSelectedColor(int r, int c, Color color) {
        if (cells[r][c] instanceof BlackCellView) return;
        ((WhiteCellView)cells[r][c]).setSelectedColor(color);
    }
    public void unselectWhiteCell(int r, int c) {
        if (cells[r][c] instanceof BlackCellView) return;
        ((WhiteCellView)cells[r][c]).unselect();
    }

    private class BlackCellView extends JPanel {
        private int row, col;
        private boolean topToPaint, bottomToPaint, leftToPaint, rightToPaint;
        private JLabel topLbl, bottomLbl, leftLbl, rightLbl;
        private Color topColor, bottomColor, leftColor, rightColor;
        public BlackCellView (int row, int col, int top, int bottom, int left, int right, boolean showValues) {
            this.row = row;
            this.col = col;
            topToPaint = top!=0;
            topColor = blackCellColor;
            topLbl = new JLabel("", JLabel.CENTER);
            topLbl.setForeground(Color.WHITE);
            topLbl.setOpaque(false);
            bottomToPaint = bottom!=0;
            bottomColor = blackCellColor;
            bottomLbl = new JLabel("", JLabel.CENTER);
            bottomLbl.setForeground(Color.WHITE);
            bottomLbl.setOpaque(false);
            leftToPaint = left!=0;
            leftColor = blackCellColor;
            leftLbl = new JLabel("", JLabel.CENTER);
            leftLbl.setForeground(Color.WHITE);
            leftLbl.setOpaque(false);
            rightToPaint = right!=0;
            rightColor = blackCellColor;
            rightLbl = new JLabel("", JLabel.CENTER);
            rightLbl.setForeground(Color.WHITE);
            rightLbl.setOpaque(false);
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

            setBackground(new Color(0,0,0,0));
        }

        // FIXME: Ugly solution, I don't know if there's a better way to do this
        private JPanel transparentSpace() {
            JPanel p = new JPanel();
            p.setVisible(false);
            return p;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color c = g.getColor();
            int centerX = getWidth()/2, centerY = getHeight()/2;
            int topLeftX = 0, topLeftY = 0;
            int topRightX = getWidth(), topRightY = 0;
            int bottomLeftX = 0, bottomLeftY = getHeight();
            int bottomRightX = getWidth(), bottomRightY = getHeight();
            if (topToPaint) {
                g.setColor(topColor);
                g.fillPolygon(new int[] { topLeftX, topRightX, centerX }, new int[] { topLeftY, topRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topLeftX, topRightX, centerX }, new int[] { topLeftY, topRightY, centerY }, 3);
            }
            if (bottomToPaint) {
                g.setColor(bottomColor);
                g.fillPolygon(new int[] { bottomLeftX, bottomRightX, centerX }, new int[] { bottomLeftY, bottomRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { bottomLeftX, bottomRightX, centerX }, new int[] { bottomLeftY, bottomRightY, centerY }, 3);
            }
            if (leftToPaint) {
                g.setColor(leftColor);
                g.fillPolygon(new int[] { topLeftX, bottomLeftX, centerX }, new int[] { topLeftY, bottomLeftY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topLeftX, bottomLeftX, centerX }, new int[] { topLeftY, bottomLeftY, centerY }, 3);
            }
            if (rightToPaint) {
                g.setColor(rightColor);
                g.fillPolygon(new int[] { topRightX, bottomRightX, centerX }, new int[] { topRightY, bottomRightY, centerY }, 3);
                g.setColor(Color.BLACK);
                g.drawPolygon(new int[] { topRightX, bottomRightX, centerX }, new int[] { topRightY, bottomRightY, centerY }, 3);
            }
            g.setColor(c);
            paintComponents(g);
        }

        public void setSectionValue(int value, int section) {
            switch(section) {
                case BLACK_SECTION_TOP:
                    topToPaint = value != 0;
                    topLbl.setText(value == 0 ? "" : ""+value);
                    break;
                case BLACK_SECTION_BOTTOM:
                    bottomToPaint = value != 0;
                    bottomLbl.setText(value == 0 ? "" : ""+value);
                    break;
                case BLACK_SECTION_LEFT:
                    leftToPaint = value != 0;
                    leftLbl.setText(value == 0 ? "" : ""+value);
                    break;
                case BLACK_SECTION_RIGHT:
                    rightToPaint = value != 0;
                    rightLbl.setText(value == 0 ? "" : ""+value);
                    break;
            }
            revalidate();
        }

        public void setSectionColor(int section, Color color) {
            int brightness = (color.getRed()+color.getGreen()+color.getBlue())/3;
            Color fontColor = brightness > 150 ? Color.BLACK : Color.WHITE;
            switch(section) {
                case BLACK_SECTION_TOP:
                    topColor = color;
                    topLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_BOTTOM:
                    bottomColor = color;
                    bottomLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_LEFT:
                    leftColor = color;
                    leftLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_RIGHT:
                    rightColor = color;
                    rightLbl.setForeground(fontColor);
                    break;
            }
            revalidate();
        }
        public void unselectSection(int section) {
            int brightness = (blackCellColor.getRed()+blackCellColor.getGreen()+blackCellColor.getBlue())/3;
            Color fontColor = brightness > 150 ? Color.BLACK : Color.WHITE;
            switch(section) {
                case BLACK_SECTION_TOP:
                    topColor = blackCellColor;
                    topLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_BOTTOM:
                    bottomColor = blackCellColor;
                    bottomLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_LEFT:
                    leftColor = blackCellColor;
                    leftLbl.setForeground(fontColor);
                    break;
                case BLACK_SECTION_RIGHT:
                    rightColor = blackCellColor;
                    rightLbl.setForeground(fontColor);
                    break;
            }
            revalidate();
        }
        public void resetBlackCellColor() {
            int brightness = (blackCellColor.getRed()+blackCellColor.getGreen()+blackCellColor.getBlue())/3;
            Color fontColor = brightness > 150 ? Color.BLACK : Color.WHITE;
            topColor = blackCellColor;
            topLbl.setForeground(fontColor);
            bottomColor = blackCellColor;
            bottomLbl.setForeground(fontColor);
            leftColor = blackCellColor;
            leftLbl.setForeground(fontColor);
            rightColor = blackCellColor;
            rightLbl.setForeground(fontColor);
            revalidate();
        }
    }

    private class WhiteCellView extends JPanel {
        private final int row, col;
        private int value, notations;
        private boolean showValues;
        private Color selectedColor;

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
            setBackground(Color.WHITE);
            setVisible(true);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    resetLayout();
                }
            });

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
            Color valueFontColor = Color.BLACK;
            Color notationFontColor = Color.GRAY;
            if (selectedColor != null) {
                int brightness = (selectedColor.getRed()+selectedColor.getGreen()+selectedColor.getBlue())/3;
                valueFontColor = brightness > 150 ? Color.BLACK : Color.WHITE;
                notationFontColor = brightness > 150 ? Color.GRAY : Color.LIGHT_GRAY;
            }
            if (value != 0) {
                removeAll();
                setLayout(new BorderLayout());
                JLabel valueLbl = new JLabel(""+value);
                valueLbl.setForeground(valueFontColor);
                valueLbl.setOpaque(false);
                valueLbl.setHorizontalAlignment(SwingConstants.CENTER);

                int stringWidth = valueLbl.getFontMetrics(valueLbl.getFont()).stringWidth(""+value);
                double widthRatio = (double)this.getWidth() / (double)stringWidth;

                // Pick a new font size so it will not be larger than the height of label.
                int fontSizeToUse = (int)Math.min(valueLbl.getFont().getSize() * widthRatio, this.getHeight() * 0.6);

                // Set the label's font size to the newly determined size.
                valueLbl.setFont(new Font(valueLbl.getFont().getName(), Font.PLAIN, fontSizeToUse));
                add(valueLbl);
            } else {
                removeAll();
                setLayout(new GridLayout(3,3));
                for (int i = 0; i < 9; i++) {
                    JLabel notationLbl = new JLabel();
                    notationLbl.setForeground(notationFontColor);
                    notationLbl.setOpaque(false);
                    notationLbl.setHorizontalAlignment(SwingConstants.CENTER);
                    if ((notations & (1<<i)) != 0) notationLbl.setText(""+(i+1));
                    add(notationLbl);
                }
            }
            revalidate();
        }

        public void setValue(int value) {
            if (this.value != value) {
                this.value = value;
                resetLayout();
            }
        }

        public void setNotations(int notations) {
            if (this.value == 0 && this.notations != notations) {
                this.notations = notations;
                resetLayout();
            } else {
                this.notations = notations;
            }
        }

        public void setSelectedColor(Color color) {
            selectedColor = color;
            setBackground(selectedColor);
            resetLayout();
        }
        public void unselect() {
            if (selectedColor == null) return;
            selectedColor = null;
            setBackground(Color.WHITE);
            resetLayout();
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
