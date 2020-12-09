package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.LoginScreen;
import src.utils.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LoginScreenCtrl extends AbstractScreenCtrl {
    public LoginScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        screen = new LoginScreen(this);
    }

    public void login(String user) {
        if(!presentationCtrl.logIn(user)){
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while attempting to log in. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public ArrayList<String> getUsers() {
        Pair<ArrayList<String>, String> ret = domainCtrl.getUsers();
        return ret.first;
    }

    public void register(String user) {
        Pair<Boolean, String> ret = domainCtrl.register(user);
        if(!ret.first) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unable to register new user: " + ret.second,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onFocusRegained(int width, int height) {}
    @Override
    public void onDashboardMenuItemClicked() {}
    @Override
    public void onKakuroListMenuItemClicked() {}
    @Override
    public void onMyKakurosMenuItemClicked() {}
    @Override
    public void onStatisticsMenuItemClicked() {}
    @Override
    public void onRankingsMenuItemClicked() {}
    @Override
    public void onLogOutMenuItemClicked() {}
}
