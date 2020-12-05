package src.presentation.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class KakuroInfoCardView extends JPanel {
    public static final int STATE_NEUTRAL = 0;
    public static final int STATE_UNFINISHED = 1;
    public static final int STATE_SOLVED = 2;
    public static final int STATE_SURRENDERED = 3;

    private InfoCardButtonsClickListener listener;

    public interface InfoCardButtonsClickListener {
        void onExportClicked(String id);
        void onPlayClicked(String id);
    }

    private KakuroView kakuroView;
    private JPanel kakuroInfoAndButtons;

    public KakuroInfoCardView(String board, final String name, String difficulty, String timesPlayed, String ownerName, String date, String recordTime, int state) {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Main kakuro view
        kakuroView = new KakuroView(board, false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(kakuroView, constraints);

        kakuroInfoAndButtons = new JPanel();
        kakuroInfoAndButtons.setLayout(new GridBagLayout());

        // Lower left info content
        JLabel nameLbl = new JLabel(name);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        kakuroInfoAndButtons.add(nameLbl, constraints);

        JLabel difficultyLbl = new JLabel("Difficulty: "+difficulty);
        constraints.gridy = 1;
        kakuroInfoAndButtons.add(difficultyLbl, constraints);

        String[] rows = board.split("\\n");
        String[] line1 = rows[0].split(",");
        int boardHeight = Integer.parseInt(line1[0].trim());
        int boardWidth  = Integer.parseInt(line1[1].trim());
        JLabel sizeLbl = new JLabel("Size: "+boardWidth+","+boardHeight);
        constraints.gridy = 2;
        kakuroInfoAndButtons.add(sizeLbl, constraints);

        JLabel recordLbl = new JLabel("BEST TIME: "+recordTime);
        constraints.gridy = 3;
        kakuroInfoAndButtons.add(recordLbl, constraints);

        // Lower right info content
        JLabel timesPlayedLbl = new JLabel("Times played: "+timesPlayed);
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        kakuroInfoAndButtons.add(timesPlayedLbl, constraints);

        JLabel ownerLbl = new JLabel("Owner: "+ownerName);
        constraints.gridy = 1;
        kakuroInfoAndButtons.add(ownerLbl, constraints);

        JLabel creationLbl = new JLabel("Creation date: " + date);
        constraints.gridy = 2;
        kakuroInfoAndButtons.add(creationLbl, constraints);

            // Flag and buttons
        if (state != STATE_NEUTRAL) {

        }

        JButton exportBtn = new JButton("=");
        exportBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onExportClicked(name);
            }
        });
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        kakuroInfoAndButtons.add(exportBtn, constraints);

        String playStr;
        if (state == STATE_UNFINISHED) {
            playStr = "RESUME";
        } else {
            playStr = "PLAY";
        }
        JButton playBtn = new JButton(playStr);
        playBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) listener.onPlayClicked(name);
            }
        });
        constraints.gridx = 6;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        kakuroInfoAndButtons.add(playBtn, constraints);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0,0,0,0));
        constraints.gridx = 3;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        kakuroInfoAndButtons.add(separator, constraints);


        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(kakuroInfoAndButtons, constraints);

        kakuroInfoAndButtons.setBackground(Color.GRAY);
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        kakuroView.setSize(width, height - kakuroInfoAndButtons.getHeight());
    }

    public void setListener(InfoCardButtonsClickListener l) {
        listener = l;
    }
}
