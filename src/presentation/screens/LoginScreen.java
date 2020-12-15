package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;
import src.presentation.controllers.LoginScreenCtrl;
import src.presentation.utils.RGBUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LoginScreen extends AbstractScreen {
    private JTextField registerUsernameInput;

    private static final EmptyBorder thinBorder = new EmptyBorder(0, 10, 0, 10);
    private static final EmptyBorder thickBorder = new EmptyBorder(10, 20, 10, 20);

    public LoginScreen(AbstractScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel userListTitle = new JLabel("Who are you? Choose your profile");
        userListTitle.setFont(new Font(userListTitle.getFont().getName(), Font.PLAIN, 20));
        EmptyBorder border = new EmptyBorder(5, 10, 5, 10);
        userListTitle.setBorder(border);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(userListTitle, constraints);

        ArrayList<String> users = ((LoginScreenCtrl)ctrl).getUserList();
        JPanel userListLayout = new JPanel(new GridLayout(1, users.size()));
        userListLayout.setBorder(new EmptyBorder(0, 0, 20, 0));

        for (String user : users) {
            JLabel profile = new JLabel(user);
            profile.setHorizontalTextPosition(JLabel.CENTER);
            profile.setHorizontalAlignment(JLabel.CENTER);
            profile.setVerticalTextPosition(JLabel.BOTTOM);
            profile.setVerticalAlignment(JLabel.BOTTOM);
            profile.setBorder(thickBorder);
            profile.setIcon(new UserIcon(user));

            profile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    ((LoginScreenCtrl)ctrl).loginUser(user);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    ((UserIcon)profile.getIcon()).onMouseEnter();
                    profile.setBorder(thinBorder);
                    profile.revalidate();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    ((UserIcon)profile.getIcon()).onMouseLeave();
                    profile.setBorder(thickBorder);
                    profile.revalidate();
                }
            });
            userListLayout.add(profile);
        }

        JScrollPane userListPane = new JScrollPane(userListLayout);
        userListPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        contents.add(userListPane, constraints);

        JLabel registerTitle = new JLabel("Create new profile");
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

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = registerUsernameInput.getText();
            ((LoginScreenCtrl)ctrl).register(username);
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 3;
        contents.add(registerButton, constraints);
    }

    private static class UserIcon implements Icon {
        private final Color color;
        private final char letter;
        private int width = 120;
        private int height = 120;

        public void onMouseEnter() {
            width += 20;
            height += 20;
        }

        public void onMouseLeave() {
            width -= 20;
            height -= 20;
        }

        public UserIcon(String user) {
            letter = user.charAt(0);
            color = RGBUtils.Hash2Color(user);
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
            g.setFont(new Font(g.getFont().getName(), Font.BOLD, (width == 120 ? 40 : 46)));
            g.fillRoundRect(x, y, width, height, 10, 10);
            g.setColor(RGBUtils.getContrastColor(color));

            FontMetrics metrics = g.getFontMetrics(g.getFont());
            // Determine the X & Y coordinates for the text
            final int textX = x + (width - metrics.stringWidth(Character.toString(letter))) / 2;
            final int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(Character.toString(letter), textX, textY);
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
