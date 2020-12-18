package src.presentation.utils;

import javax.swing.*;

public class Dialogs {
    public static void showErrorDialog(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoDialog(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showYesNoOptionDialog(String msg, String title) {
        return JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
    }
}
