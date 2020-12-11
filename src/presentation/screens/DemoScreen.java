package src.presentation.screens;

import src.presentation.controllers.DemoScreenCtrl;
import src.presentation.views.KakuroInfoCardView;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DemoScreen extends AbstractScreen {

    KakuroInfoCardView kak;
    JLabel leftContent, rightContent;

    public DemoScreen(DemoScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        System.out.println("Demo: build()");
        contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        leftContent = new JLabel("Some content left", JLabel.CENTER);
        leftContent.setHorizontalAlignment(SwingConstants.CENTER);
        leftContent.setVerticalAlignment(SwingConstants.CENTER);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(leftContent, constraints);
        try {
            String boardStr = new String(Files.readAllBytes(Paths.get("data/kakuros/solved/jutge.kak")));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse("23/09/20020");
            kak = new KakuroInfoCardView(boardStr,"Breakfast kakuro","HARD",12,"Cesc",new Timestamp(date.getTime()),1234,KakuroInfoCardView.STATE_SURRENDERED);
            kak.setSize(width, height);
            constraints.fill = GridBagConstraints.VERTICAL;
            constraints.gridx = 1;
            constraints.gridy = 0;
            contents.add(kak, constraints);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        rightContent = new JLabel("Some content right", JLabel.CENTER);
        rightContent.setHorizontalAlignment(SwingConstants.CENTER);
        rightContent.setVerticalAlignment(SwingConstants.CENTER);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        contents.add(rightContent, constraints);
        contents.setVisible(true);
    }

    @Override
    public void onShow() {
        System.out.println("Demo: onShow()");
    }

    @Override
    public void onHide() {
        System.out.println("Demo: onHide()");
    }

    @Override
    public void onDestroy() {
        System.out.println("Demo: onDestroy()");
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        int remainingWidth = width - leftContent.getWidth() - rightContent.getWidth();
        kak.setSize(remainingWidth, height);
        System.out.println("Demo: onResize("+width+","+height+")");
    }
}
