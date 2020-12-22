package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.DisplayKakuroScreen;

import java.awt.*;

public class DisplayKakuroScreenCtrl extends AbstractScreenCtrl {
    private String title;
    private String board;
    private Color blackCellColor;
    private Color background;
    private String body;

    private DefaultFinishOperation callback;

    public interface DefaultFinishOperation {
        void onFinished();
    }

    public DisplayKakuroScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        screen = new DisplayKakuroScreen(this);

        title = "";
        board = "";
        background = Color.WHITE;
        body = "";
    }

    public void prepareContents(String title, String board, Color blackCellColor, Color background, String body, DefaultFinishOperation callback) {
        this.title = title;
        this.board = board;
        this.blackCellColor = blackCellColor;
        this.background = background;
        this.body = body;
        this.callback = callback;
    }

    public String getTitle() { return title; }
    public String getBoard() { return board; }
    public Color getBlackCellColor() { return blackCellColor; }
    public Color getBackground() { return background; }
    public String getBody() { return body; }

    public void onFinishClick() {
        if (callback != null) callback.onFinished();
    }

    @Override
    public void onFocusRegained(int width, int height) {}
}
