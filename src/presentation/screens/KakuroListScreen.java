package src.presentation.screens;

import src.domain.entities.Difficulty;
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

    public void setSelectedTab(Difficulty difficulty) {
        tabbedPane.setSelectedIndex(difficulty.ordinal());
    }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setSize(width, height);
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        title = new JLabel("Kakuro List");
        title.setForeground(Color.BLACK);
        title.setVisible(true);
        contents.add(title);

        tabbedPane = new JTabbedPane();
        kakuroListPane = new JScrollPane[Difficulty.values().length];
        kakuroListLayout = new JPanel[Difficulty.values().length];

        for(Difficulty diff : Difficulty.values()) {
            ArrayList<Map<String, Object>> info = ((KakuroListScreenCtrl) ctrl).getInfoToDisplay(diff);
            if (info.size() == 0) return;
            kakuroListLayout[diff.ordinal()] = new JPanel(new GridLayout(info.size() / 3 + ((info.size() % 3 != 0) ? 1 : 0), 3));

            for (Map<String, Object> kakuroData : info) {
                String state = (String) kakuroData.get("state");
                int stateCode = 0;

                switch (state) {
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
                        stateCode);
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
                kakuroListLayout[diff.ordinal()].add(kak);
            }
            kakuroListPane[diff.ordinal()] = new JScrollPane(kakuroListLayout[diff.ordinal()]);
            kakuroListPane[diff.ordinal()].setVisible(true);
            kakuroListPane[diff.ordinal()].getVerticalScrollBar().setUnitIncrement(20);

            tabbedPane.addTab(diff.name().equals("USER_MADE") ? "CUSTOM" : diff.name(), kakuroListPane[diff.ordinal()]);
        }

        contents.add(tabbedPane);
        contents.setVisible(true);
        onResize(width, height);
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        for(int i = 0; i < Difficulty.values().length; i++) {
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
                kakuroListLayout[i].setLayout(new GridLayout(numComp / rowSize + ((numComp % rowSize != 0) ? 1 : 0), rowSize));
            }
            for (Component c : kakuroListLayout[i].getComponents()) {
                c.setSize(contents.getWidth() / 4, height * 2 / 3);
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
