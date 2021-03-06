package src.presentation.screens;

import src.presentation.controllers.MyKakurosScreenCtrl;
import src.presentation.views.KakuroInfoCardView;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class MyKakurosScreen extends AbstractScreen {
    JLabel title;
    JScrollPane kakuroListPane;
    JPanel kakuroListLayout;

    public MyKakurosScreen(MyKakurosScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setSize(width, height);
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

        title = new JLabel("My Kakuros");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        title.setVisible(true);
        contents.add(title);

        ArrayList<Map<String, Object>> info = ((MyKakurosScreenCtrl) ctrl).getInfoToDisplay();
        if (info.size() == 0) return;
        kakuroListLayout = new JPanel(new GridLayout(info.size()/3 + ((info.size()%3 != 0) ? 1 : 0), 3, 10, 10));

        for (Map<String, Object> kakuroData : info) {
            String state = (String) kakuroData.get("state");
            int stateCode = 0;

            switch (state) {
                case "neutral":     stateCode = KakuroInfoCardView.STATE_NEUTRAL; break;
                case "unfinished":  stateCode = KakuroInfoCardView.STATE_UNFINISHED; break;
                case "solved":      stateCode = KakuroInfoCardView.STATE_SOLVED; break;
                case "surrendered": stateCode = KakuroInfoCardView.STATE_SURRENDERED; break;
            }

            KakuroInfoCardView kak = new KakuroInfoCardView(
                    (String) kakuroData.get("board"),
                    (String) kakuroData.get("seed"),
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
                    ((MyKakurosScreenCtrl) ctrl).onExportKakuroClicked(id);
                }

                @Override
                public void onPlayClicked(String id) {
                    ((MyKakurosScreenCtrl) ctrl).onPlayKakuroClicked(id);
                }
            });
            kak.setSize(width/4, height*2/3);
            kakuroListLayout.add(kak);
        }
        kakuroListLayout.setBackground(Color.LIGHT_GRAY);
        kakuroListPane = new JScrollPane(kakuroListLayout);
        kakuroListPane.setVisible(true);
        kakuroListPane.getVerticalScrollBar().setUnitIncrement(20);
        kakuroListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        kakuroListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contents.add(kakuroListPane);
        contents.setVisible(true);
        onResize(width, height);
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        int remainingHeight = height - title.getHeight();
        kakuroListPane.setSize(width, remainingHeight);
        kakuroListLayout.setSize(width-50, remainingHeight);
        int rowSize = 3;
        int numComp = kakuroListLayout.getComponents().length;
        if (numComp > 3) {
            if (width < 2*((KakuroInfoCardView)kakuroListLayout.getComponents()[0]).getRealMinimumSize().width) rowSize = 1;
            else if (width < 3*((KakuroInfoCardView)kakuroListLayout.getComponents()[0]).getRealMinimumSize().width) rowSize = 2;
            kakuroListLayout.setLayout(new GridLayout(numComp / rowSize  + ((numComp % rowSize != 0) ? 1 : 0), rowSize, 10, 10));
        } else {
            kakuroListLayout.setLayout(new GridLayout(1, numComp, 10, 10));
        }
        for (Component c : kakuroListLayout.getComponents()) {
            c.setSize(contents.getWidth()/4, height*2/3);
            c.revalidate();
        }
        kakuroListLayout.revalidate();
        kakuroListPane.setViewportView(kakuroListLayout);
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}
}
