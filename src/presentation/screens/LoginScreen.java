package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;
import src.presentation.controllers.LoginScreenCtrl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class LoginScreen extends AbstractScreen {
    private JLabel userListTitle, registerTitle;
    private JPanel userListLayout;
    private JScrollPane userListPane;
    private JTextField registerUsernameInput;
    private JButton registerButton;

    public LoginScreen(AbstractScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        userListTitle = new JLabel("Who are you? Choose your profile");
        userListTitle.setFont(new Font(userListTitle.getFont().getName(), Font.PLAIN, 20));
        EmptyBorder border = new EmptyBorder(5, 10, 5, 10);
        userListTitle.setBorder(border);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(userListTitle, constraints);

        ArrayList<String> users = ((LoginScreenCtrl)ctrl).getUserList();
        userListLayout = new JPanel(new GridLayout(1, users.size()));
        userListLayout.setBorder(new EmptyBorder(0, 0, 20, 0));

        for (String user : users) {
            JLabel profile = new JLabel(user);
            profile.setHorizontalTextPosition(JLabel.CENTER);
            profile.setHorizontalAlignment(JLabel.CENTER);
            profile.setVerticalTextPosition(JLabel.BOTTOM);
            profile.setVerticalAlignment(JLabel.BOTTOM);
            profile.setBorder(new EmptyBorder(5, 20, 5, 20));
            profile.setIcon(new UserIcon(user));

            profile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ((LoginScreenCtrl)ctrl).loginUser(user);
                }
            });
            userListLayout.add(profile);
        }

        userListPane = new JScrollPane(userListLayout);
        userListPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        contents.add(userListPane, constraints);

        registerTitle = new JLabel("Create new profile");
        registerTitle.setForeground(Color.BLACK);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        contents.add(registerTitle, constraints);

        registerUsernameInput = new JTextField();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 3;
        contents.add(registerUsernameInput, constraints);

        registerButton = new JButton("Register");
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String username = registerUsernameInput.getText();
                ((LoginScreenCtrl)ctrl).register(username);
            }
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 3;
        contents.add(registerButton, constraints);
    }

    private static class UserIcon implements Icon {
        private final Color color;
        private final int width = 120;
        private final int height = 120;
        private final char letter;

        public UserIcon(String user) {
            this.letter = user.charAt(0);
            this.color = new Color(user.hashCode());
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Font oldFont = g.getFont();

            g.setColor(color);
            g.setFont(new Font(g.getFont().getName(), Font.BOLD, 40));
            g.fillRoundRect(x, y, width, height, 10, 10);
            g.setColor(Color.WHITE);

            FontMetrics metrics = g.getFontMetrics(g.getFont());
            // Determine the X & Y coordinates for the text
            int textX = x + (width - metrics.stringWidth("" + letter)) / 2;
            int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString("" + letter, textX, textY);
            g.setFont(oldFont);
        }
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onResize(int width, int height) {}
}
