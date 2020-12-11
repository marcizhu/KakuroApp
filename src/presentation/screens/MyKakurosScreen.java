package src.presentation.screens;

import src.presentation.controllers.MyKakurosScreenCtrl;
import src.presentation.views.KakuroInfoCardView;
import src.utils.Pair;

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
        title.setForeground(Color.BLACK);
        title.setVisible(true);
        contents.add(title);

        ArrayList<Map<String, Object>> info = ((MyKakurosScreenCtrl) ctrl).getInfoToDisplay();
        if (info == null) return;
        kakuroListLayout = new JPanel(new GridLayout(info.size()/3+1, 3));

        for (Map<String, Object> kakuroData : info) {
            System.out.println("Creating kakuroCardView");
            String state = (String) kakuroData.get("state");
            int stateCode = 0;

            switch (state) {
                case "unfinished":  stateCode = KakuroInfoCardView.STATE_UNFINISHED; break;
                case "solved":      stateCode = KakuroInfoCardView.STATE_SOLVED; break;
                case "surrendered": stateCode = KakuroInfoCardView.STATE_SURRENDERED; break;
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
        kakuroListPane = new JScrollPane(kakuroListLayout);
        kakuroListPane.setVisible(true);
        kakuroListPane.getVerticalScrollBar().setUnitIncrement(20);
        contents.add(kakuroListPane);
        contents.setVisible(true);
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        int remainingHeight = height - title.getHeight();
        kakuroListPane.setSize(width, remainingHeight);
        kakuroListLayout.setSize(width-50, remainingHeight);
        /*int rowSize = 3;
        if (kakuroListPane.getComponents().length > 0) {
            if (width < 2*kakuroListPane.getComponents()[0].getWidth()) rowSize = 1;
            else if (width < 3*kakuroListPane.getComponents()[0].getWidth()) rowSize = 2;
            kakuroListLayout.setLayout(new GridLayout(kakuroListLayout.getComponents().length/rowSize +1, rowSize));
        }*/
        for (Component c : kakuroListLayout.getComponents()) {
            c.setSize(contents.getWidth()/4, height*2/3);
            c.revalidate();
        }
        kakuroListLayout.revalidate();
        kakuroListPane.revalidate();
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}
}
