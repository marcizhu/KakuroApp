package src.presentation.views;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class KakuroInfoCardView extends JPanel {
    public static final int STATE_NEUTRAL = 0;
    public static final int STATE_UNFINISHED = 1;
    public static final int STATE_SOLVED = 2;
    public static final int STATE_SURRENDERED = 3;

    private final KakuroView kakuroView;
    private final JPanel kakuroInfoAndButtons;
    private InfoCardButtonsClickListener listener;

    public interface InfoCardButtonsClickListener {
        void onExportClicked(String id);
        void onPlayClicked(String id);
    }

    public KakuroInfoCardView(String board, final String name, String difficulty, Integer timesPlayed, String ownerName, Timestamp date, Integer recordTime, int state) {
        //setLayout(new GridBagLayout());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Main kakuro view
        kakuroView = new KakuroView(board, false);
        add(kakuroView);

        kakuroInfoAndButtons = new JPanel();
        kakuroInfoAndButtons.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3,10,2,10);

        // Lower left info content
        JLabel nameLbl = new JLabel(name);
        nameLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        nameLbl.setForeground(Color.BLACK);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        kakuroInfoAndButtons.add(nameLbl, constraints);

        JLabel difficultyLbl = new JLabel("Difficulty: " + difficulty);
        difficultyLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        difficultyLbl.setForeground(Color.BLACK);
        constraints.gridy = 1;
        kakuroInfoAndButtons.add(difficultyLbl, constraints);

        String[] rows = board.split("\\n");
        String[] line1 = rows[0].split(",");
        int boardHeight = Integer.parseInt(line1[0].trim());
        int boardWidth  = Integer.parseInt(line1[1].trim());
        JLabel sizeLbl = new JLabel("Size: "+boardWidth+","+boardHeight);
        sizeLbl.setHorizontalAlignment(SwingConstants.LEFT);
        sizeLbl.setForeground(Color.BLACK);
        constraints.gridy = 2;
        kakuroInfoAndButtons.add(sizeLbl, constraints);

        JLabel recordLbl = new JLabel("BEST TIME: "+recordTime);
        recordLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        recordLbl.setForeground(Color.BLACK);
        constraints.gridy = 3;
        kakuroInfoAndButtons.add(recordLbl, constraints);

        // Lower right info content
        JLabel timesPlayedLbl = new JLabel("Times played: "+timesPlayed);
        timesPlayedLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
        timesPlayedLbl.setForeground(Color.BLACK);
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        kakuroInfoAndButtons.add(timesPlayedLbl, constraints);

        JLabel ownerLbl = new JLabel("Owner: "+ownerName);
        ownerLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
        ownerLbl.setForeground(Color.BLACK);
        constraints.gridy = 1;
        kakuroInfoAndButtons.add(ownerLbl, constraints);

        JLabel creationLbl = new JLabel("Creation date: " + date);
        creationLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
        creationLbl.setForeground(Color.BLACK);
        constraints.gridy = 2;
        kakuroInfoAndButtons.add(creationLbl, constraints);

            // Flag and buttons
        if (state != STATE_NEUTRAL) {
            String flag;
            Color flagColor;
            switch (state) {
                case STATE_UNFINISHED:
                    flag = "U";
                    flagColor = Color.YELLOW;
                    break;
                case STATE_SOLVED:
                    flag = "S";
                    flagColor = Color.GREEN;
                    break;
                case STATE_SURRENDERED:
                    flag = "X";
                    flagColor = Color.RED;
                    break;
                default:
                    flag = "";
                    flagColor = new Color(0,0,0,0);
            }
            JLabel flagLbl = new JLabel(flag);
            flagLbl.setBackground(flagColor);
            flagLbl.setOpaque(true);
            flagLbl.setHorizontalTextPosition(SwingConstants.CENTER);
            flagLbl.setForeground(Color.BLACK);
            constraints.gridx = 4;
            constraints.gridy = 3;
            constraints.gridwidth = 1;
            constraints.fill = GridBagConstraints.NONE;
            kakuroInfoAndButtons.add(flagLbl, constraints);
        }

        JButton exportBtn = new JButton("=");
        exportBtn.addActionListener(e -> {
            if (listener != null) listener.onExportClicked(name);
        });
        exportBtn.setForeground(Color.BLACK);
        exportBtn.setFocusable(false);
        constraints.gridx = 5;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        kakuroInfoAndButtons.add(exportBtn, constraints);

        String playStr = state == STATE_UNFINISHED ? "RESUME" : "PLAY";
        JButton playBtn = new JButton(playStr);
        playBtn.addActionListener(e -> {
            if (listener != null) listener.onPlayClicked(name);
        });
        playBtn.setForeground(Color.BLACK);
        playBtn.setFocusable(false);
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

        kakuroInfoAndButtons.setBackground(Color.GRAY);
        add(kakuroInfoAndButtons);

        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public void setSize(int width, int height) {
        kakuroInfoAndButtons.setSize(width, kakuroInfoAndButtons.getHeight());
        kakuroView.setSize(width, height - kakuroInfoAndButtons.getHeight());
        int maxWidth = Math.max(kakuroInfoAndButtons.getWidth(), kakuroView.getWidth());
        if (maxWidth < kakuroInfoAndButtons.getMinimumSize().width) maxWidth = kakuroInfoAndButtons.getMinimumSize().width;
        if (maxWidth > kakuroInfoAndButtons.getWidth()) kakuroInfoAndButtons.setSize(maxWidth, kakuroInfoAndButtons.getHeight());
        super.setSize(maxWidth, kakuroInfoAndButtons.getHeight() + kakuroView.getHeight());
        revalidate();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        int maxWidth = Math.max(kakuroInfoAndButtons.getWidth(), kakuroView.getWidth());
        if (maxWidth < kakuroInfoAndButtons.getMinimumSize().width) maxWidth = kakuroInfoAndButtons.getMinimumSize().width;
        return new Dimension(maxWidth, kakuroInfoAndButtons.getHeight() + kakuroView.getHeight());
    }

    public void setListener(InfoCardButtonsClickListener l) {
        listener = l;
    }
}
