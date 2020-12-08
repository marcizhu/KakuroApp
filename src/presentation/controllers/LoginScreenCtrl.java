package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.LoginScreen;

public class LoginScreenCtrl extends AbstractScreenCtrl {
    public LoginScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        screen = new LoginScreen(this);
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
