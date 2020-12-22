package src.presentation.screens;

import src.presentation.controllers.KakuroListScreenCtrl;
import src.presentation.views.KakuroInfoCardView;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class KakuroListScreen extends AbstractScreen {
    JLabel title;
    JScrollPane[] kakuroListPane;
    JPanel[] kakuroListLayout;
    JTabbedPane tabbedPane;

    public KakuroListScreen(KakuroListScreenCtrl ctrl) {
        super(ctrl);
    }

    public void setSelectedTab(String difficulty) {
        tabbedPane.setSelectedIndex(difficultyToInt(difficulty));
    }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setSize(width, height);
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        title = new JLabel("Kakuro List");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        title.setVisible(true);
        contents.add(title);

        tabbedPane = new JTabbedPane();
        kakuroListPane = new JScrollPane[5];
        kakuroListLayout = new JPanel[5];

        for(int diff = 0; diff < 5; diff++) {
            ArrayList<Map<String, Object>> info = ((KakuroListScreenCtrl) ctrl).getInfoToDisplay(diff);
            kakuroListLayout[diff] = new JPanel(new GridLayout(info.size() / 3 + ((info.size() % 3 != 0) ? 1 : 0), 3, 10, 10));

            for (Map<String, Object> kakuroData : info) {
                String state = (String) kakuroData.get("state");
                int stateCode = 0;

                switch (state) {
                    case "neutral":
                        stateCode = KakuroInfoCardView.STATE_NEUTRAL;
                        break;
                    case "unfinished":
                        stateCode = KakuroInfoCardView.STATE_UNFINISHED;
                        break;
                    case "solved":
                        stateCode = KakuroInfoCardView.STATE_SOLVED;
                        break;
                    case "surrendered":
                        stateCode = KakuroInfoCardView.STATE_SURRENDERED;
                        break;
                }

                KakuroInfoCardView kak = new KakuroInfoCardView(
                        (String) kakuroData.get("board"),
                        (String) kakuroData.get("name"),
                        (String) kakuroData.get("difficulty"),
                        (Integer) kakuroData.get("timesPlayed"),
                        (String) kakuroData.get("createdBy"),
                        (Timestamp) kakuroData.get("createdAt"),
                        (Integer) kakuroData.get("bestTime"),
                        stateCode,
                        (Integer) kakuroData.get("color")
                );
                kak.setListener(new KakuroInfoCardView.InfoCardButtonsClickListener() {
                    @Override
                    public void onExportClicked(String id) {
                        ((KakuroListScreenCtrl) ctrl).onExportKakuroClicked(id);
                    }

                    @Override
                    public void onPlayClicked(String id) {
                        ((KakuroListScreenCtrl) ctrl).onPlayKakuroClicked(id);
                    }
                });
                kak.setSize(width / 4, height * 2 / 3);
                kakuroListLayout[diff].add(kak);
                kakuroListLayout[diff].setBackground(Color.LIGHT_GRAY);
            }
            kakuroListPane[diff] = new JScrollPane(kakuroListLayout[diff]);
            kakuroListPane[diff].setVisible(true);
            kakuroListPane[diff].getVerticalScrollBar().setUnitIncrement(20);
            kakuroListPane[diff].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            kakuroListPane[diff].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            tabbedPane.addTab(difficultyToString(diff).equals("USER_MADE") ? "BY USERS" : difficultyToString(diff), kakuroListPane[diff]);
        }

        contents.add(tabbedPane);
        contents.setVisible(true);
        onResize(width, height);
    }

    private String difficultyToString(int diff) {
        switch (diff) {
            case 0: return "EASY";
            case 1: return "MEDIUM";
            case 2: return "HARD";
            case 3: return "EXTREME";
            case 4: return "USER_MADE";
        }
        return "";
    }

    private int difficultyToInt(String difficulty) {
        int diff = 0;
        /**/ if (difficulty.equals("EASY")) diff = 0;
        else if (difficulty.equals("MEDIUM")) diff = 1;
        else if (difficulty.equals("HARD")) diff = 2;
        else if (difficulty.equals("EXTREME")) diff = 3;
        else if (difficulty.equals("USER_MADE")) diff = 4;
        return diff;
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        for(int i = 0; i < 5; i++) {
            int remainingHeight = height - title.getHeight();
            kakuroListPane[i].setSize(width, remainingHeight);
            kakuroListLayout[i].setSize(width - 50, remainingHeight);
            int rowSize = 3;
            int numComp = kakuroListLayout[i].getComponents().length;
            if (numComp > 0) {
                if (width < 2 * ((KakuroInfoCardView) kakuroListLayout[i].getComponents()[0]).getRealMinimumSize().width)
                    rowSize = 1;
                else if (width < 3 * ((KakuroInfoCardView) kakuroListLayout[i].getComponents()[0]).getRealMinimumSize().width)
                    rowSize = 2;
                kakuroListLayout[i].setLayout(new GridLayout(numComp / rowSize + ((numComp % rowSize != 0) ? 1 : 0), rowSize, 10, 10));
            } else {
                kakuroListLayout[i].setLayout(new GridLayout(1, numComp, 10, 10));
            }
            for (Component c : kakuroListLayout[i].getComponents()) {
                c.setSize(width / 4, width/4);
                c.revalidate();
            }
            kakuroListLayout[i].revalidate();
            kakuroListPane[i].setViewportView(kakuroListLayout[i]);
        }
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}
}
