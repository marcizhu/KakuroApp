package src.presentation.screens;

import src.presentation.controllers.PresentationCtrl;

public class LoginScreen extends Screen{

    public LoginScreen(PresentationCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        System.out.println("Login: build()");
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
        super.onResize(width, height);
        System.out.println("Login: onResize("+width+","+height+")");
    }
}
