package src.presentation.screens;

import src.presentation.controllers.DisplayKakuroScreenCtrl;
import src.presentation.utils.RGBUtils;
import src.presentation.views.KakuroView;

import javax.swing.*;
import java.awt.*;

public class DisplayKakuroScreen extends AbstractScreen {
    JLabel title;
    KakuroView kakuroView;
    JLabel body;
    JButton okBtn;

    public DisplayKakuroScreen(DisplayKakuroScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        Color backgroundColor = ((DisplayKakuroScreenCtrl)ctrl).getBackground();
        Color fontColor = RGBUtils.getContrastColor(backgroundColor);

        title = new JLabel(((DisplayKakuroScreenCtrl)ctrl).getTitle());
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(fontColor);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JPanel kakuroWrapper = new JPanel();
        kakuroWrapper.setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        kakuroView = new KakuroView(((DisplayKakuroScreenCtrl)ctrl).getBoard(), ((DisplayKakuroScreenCtrl)ctrl).getBlackCellColor().getRGB(), true);
        kakuroView.setSize(height/2, height/2);
        kakuroWrapper.add(kakuroView, constraints);
        kakuroWrapper.setOpaque(false);

        body = new JLabel("<html><body>" + ((DisplayKakuroScreenCtrl)ctrl).getBody() + "</body></html>");
        body.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        body.setForeground(fontColor);
        body.setOpaque(false);
        body.setHorizontalAlignment(SwingConstants.LEFT);
        body.setVerticalAlignment(SwingConstants.CENTER);

        okBtn = new JButton("OK");
        okBtn.addActionListener(e -> ((DisplayKakuroScreenCtrl)ctrl).onFinishClick());

        constraints.insets = new Insets(10,10,10,10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(title, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        contents.add(kakuroWrapper, constraints);

        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contents.add(body, constraints);

        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.NONE;
        contents.add(okBtn, constraints);

        contents.setBackground(backgroundColor);
        contents.setVisible(true);

        onResize(width, height);
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        int remainingHeight = height - title.getHeight() - body.getHeight() - okBtn.getHeight();
        kakuroView.setSize(remainingHeight*2/3, remainingHeight*2/3);
    }
}
