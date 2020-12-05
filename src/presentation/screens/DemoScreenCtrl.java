package src.presentation.screens;

import src.domain.controllers.DomainCtrl;
import src.presentation.controllers.PresentationCtrl;

public class DemoScreenCtrl extends AbstractScreenCtrl{

    public DemoScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        screen = new DemoScreen(this);
    }

    @Override
    public void onFocusRegained() {
        screen.onShow();
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