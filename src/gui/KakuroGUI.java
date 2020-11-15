package src.gui;

import src.domain.Board;
import src.domain.Cell;
import src.domain.BlackCell;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class KakuroGUI extends JFrame {
    private Board b;
    private int rows, cols;
    private JLabel[][] cellLabels;
    private int[] selectedCell = {-1, -1};

    private JPanel mainPanel, upperPanel, centerPanel;

    Boolean addBool = false;
    Boolean subBool = false;
    Boolean divBool = false;
    Boolean mulBool = false;

    String display = "";

    public KakuroGUI(Board b) {
        this.b = b;
        rows = b.getHeight();
        cols = b.getWidth();

        cellLabels = initCells();

        upperPanel = new JPanel();
        upperPanel.add(new JLabel("Yessir this mf working"));

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(rows, cols));
        initCenterPanel();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        //button.addActionListener(new sayHello());

    }

    private JLabel[][] initCells() {
        JLabel[][] labels = new JLabel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                String s = "";
                JLabel l = new JLabel();
                Cell c = b.getCell(i, j);

                if (c instanceof BlackCell) {
                    int hs = ((BlackCell) c).getHorizontalSum();
                    int vs = ((BlackCell) c).getVerticalSum();
                    if (hs == 0 && vs == 0) s = "*";
                    else if (hs != 0 && vs == 0) {
                        s = "F:" + Integer.toString(hs);
                    } else if (hs == 0 && vs != 0) {
                        s = "C:" + Integer.toString(vs);
                    } else {
                        s = "C:" + Integer.toString(vs) + " F: " + Integer.toString(hs);
                    }

                    l.setBackground(Color.darkGray);
                } else {
                    int val = c.getValue();
                    if (val != 0) s = Integer.toString(val);

                    l.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e)
                        {
                            //unpaintSelecteCell();
                            if (selectedCell[0] >= 0 && selectedCell[1] >= 1) cellLabels[selectedCell[0]][selectedCell[1]].setBackground(Color.white);
                            //selectedCell = l;
                            l.setBackground(Color.lightGray);
                        }
                    });
                }

                l.setText(s);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                l.setHorizontalAlignment(SwingConstants.CENTER);

                labels[i][j] = l;
            }
        }
        return labels;
    }

    private void initCenterPanel() {
        for (int i = 0; i < rows; i++) for (int j = 0; j < cols; j++) centerPanel.add(cellLabels[i][j]);
    }


    class sayHello implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("The button works!! Hello!!");
        }
    }

}
