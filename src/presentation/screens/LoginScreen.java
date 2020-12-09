package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;
import src.presentation.controllers.LoginScreenCtrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

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
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(userListTitle, constraints);

        // TODO: remove this, i coded this only for testing purposes
        ArrayList<String> users = new ArrayList<>(Arrays.asList("Alex", "Cesc", "Xavi", "Marc"));
        userListLayout = new JPanel(new GridLayout(1, users.size()));
        for (String user: users) {
            JLabel profile = new JLabel(user);
            userListLayout.add(profile);
        }
        userListPane = new JScrollPane(userListLayout);
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
                ((LoginScreenCtrl)ctrl).login();
            }
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 3;
        contents.add(registerButton, constraints);
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
