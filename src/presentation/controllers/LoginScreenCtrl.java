package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.LoginScreen;
import src.presentation.utils.Dialogs;
import src.utils.Pair;

import java.util.ArrayList;

public class LoginScreenCtrl extends AbstractScreenCtrl {

    public LoginScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        screen = new LoginScreen(this);
    }

    public void loginUser(String user) {
        if(!presentationCtrl.logIn(user)){
            Dialogs.showErrorDialog("An error occurred while attempting to log in. Please try again.","Error");
        }
    }

    public ArrayList<String> getUserList() {
        Pair<ArrayList<String>, String> ret = domainCtrl.getUserList();
        if(ret.first == null) {
            Dialogs.showErrorDialog("Unable to load users: " + ret.second,"Error");
        }

        return ret.first;
    }

    public void register(String user) {
        Pair<Boolean, String> ret = domainCtrl.registerUser(user);

        if(!ret.first)
            Dialogs.showErrorDialog("Unable to register new user: " + ret.second, "Error");
        else
            loginUser(user);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        screen.build(width, height);
    }

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
