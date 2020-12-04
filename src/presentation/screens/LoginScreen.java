package src.presentation.screens;

import src.presentation.controllers.PresentationCtrl;
import src.presentation.views.KakuroView;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginScreen extends Screen{
    KakuroView kak;

    public LoginScreen(PresentationCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        System.out.println("Login: build()");
        contents = new JPanel();
        contents.setLayout(new BorderLayout());
        contents.add(new JLabel());
        try {
            System.out.println("Adding KakuroView");
            kak = new KakuroView(new String(Files.readAllBytes(Paths.get("data/kakuros/unsolved/cpu_burner.kak"))), true);
            System.out.println("Size: " + width + ", "+ height);
            kak.setSize(width*3/4, height*3/4);
            contents.add(kak);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        contents.add(new JLabel("HelloWorld", JLabel.CENTER));
        contents.setVisible(true);
    }

    @Override
    public void onShow() {
        System.out.println("Login: onShow()");
    }

    @Override
    public void onHide() {
        System.out.println("Login: onHide()");
    }

    @Override
    public void onDestroy() {
        System.out.println("Login: onDestroy()");
    }

    @Override
    public void onResize(int width, int height) {
        //super.onResize(width, height);
        kak.setSize(width, height);
        System.out.println("Login: onResize("+width+","+height+")");
    }
}
