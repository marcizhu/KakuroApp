package src.presentation.screens;

import src.presentation.controllers.PresentationCtrl;
import src.presentation.views.KakuroView;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DemoScreen extends Screen{

    KakuroView kak;
    JLabel leftContent, rightContent;

    public DemoScreen(PresentationCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        System.out.println("Login: build()");
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
            kak = new KakuroView(new String(Files.readAllBytes(Paths.get("data/kakuros/solved/jutge.kak"))), true);
            kak.setSize(width, height);
            kak.setBlackCellColor(new Color(20,120,20));
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
        kak.setBlackCellColor(new Color(20,120,20));
    }

    @Override
    public void onHide() {
        System.out.println("Demo: onHide()");
        kak.setBlackCellColor(new Color(150,150,150));
    }

    @Override
    public void onDestroy() {
        System.out.println("Demo: onDestroy()");
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        int remainingWidth = width - leftContent.getWidth() - rightContent.getWidth();
        int min = remainingWidth < height ? remainingWidth : height;
        kak.setSize(min, min);
        System.out.println("Demo: onResize("+width+","+height+")");

        if (min == remainingWidth) kak.setBlackCellColor(new Color(150,150,150));
        else kak.setBlackCellColor(new Color(20,120,20));
    }
}
