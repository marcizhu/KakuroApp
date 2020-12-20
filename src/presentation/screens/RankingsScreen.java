package src.presentation.screens;

import src.presentation.controllers.RankingsScreenCtrl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class RankingsScreen extends AbstractScreen {

    private Font titleFnt;
    private Font subtitleFnt;
    private Font bodyFnt;
    private Font importantBodyFnt;

    private String userOfInterest;

    public RankingsScreen(RankingsScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setLayout(new BorderLayout());

        titleFnt = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        subtitleFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        bodyFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        importantBodyFnt = new Font(Font.SANS_SERIF, Font.BOLD, 12);

        userOfInterest = ((RankingsScreenCtrl)ctrl).getUser();

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.X_AXIS));

        JPanel leftSection = buildLeftSection(width);
        JPanel rightSection = buildRightSection(width);

        contentWrapper.add(leftSection);
        contentWrapper.add(Box.createRigidArea(new Dimension(20,20)));
        contentWrapper.add(rightSection);


        contents.add(contentWrapper, BorderLayout.CENTER);
        contents.add(Box.createRigidArea(new Dimension(width, 20)), BorderLayout.NORTH);
        contents.add(Box.createRigidArea(new Dimension(width, 20)), BorderLayout.SOUTH);
        contents.add(Box.createRigidArea(new Dimension(20, height-40)), BorderLayout.EAST);
        contents.add(Box.createRigidArea(new Dimension(20, height-40)), BorderLayout.WEST);
    }

    private JPanel buildLeftSection(int width) {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        // by points
        ArrayList<Map<String, Object>> infoPoints = ((RankingsScreenCtrl)ctrl).getRankingByPoints();
        JPanel pointPanel = buildRankingTile(
                "POINT-SYSTEM RANKING",
                infoPoints,
                new String[] {"Name", "Easy", "Medium", "Hard", "Extreme", "Total points"},
                new String[] {"name", "easyPts", "mediumPts", "hardPts", "extremePts", "totalPts"},
                width/2 - 100
        );

        // by games
        ArrayList<Map<String, Object>> infoGames = ((RankingsScreenCtrl)ctrl).getRankingByGames();
        JPanel gamesPanel = buildRankingTile(
                "GAMES PLAYED",
                infoGames,
                new String[] {"Name", "Easy", "Medium", "Hard", "Extreme", "Total games"},
                new String[] {"name", "easyGames", "mediumGames", "hardGames", "extremeGames", "totalGames"},
                width/2 - 100
        );

        left.add(pointPanel);
        left.add(Box.createRigidArea(new Dimension(20,20)));
        left.add(gamesPanel);

        return left;
    }

    private JPanel buildRightSection(int width) {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel title = buildLabel("AVG. TIMES BY DIFFICULTY", titleFnt, SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.BLACK);

        JPanel rightTop = new JPanel();
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.X_AXIS));

        // easy times
        ArrayList<Map<String, Object>> infoEasyTimes = ((RankingsScreenCtrl)ctrl).getRankingByTimeEasy();
        JPanel easyTimesPanel = buildRankingTile(
                "EASY",
                infoEasyTimes,
                new String[] {"Name", "Avg time"},
                new String[] {"name", "avgTime"},
                width/4 - 80
        );
        easyTimesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        easyTimesPanel.getInsets().left = 10;

        // medium times
        ArrayList<Map<String, Object>> infoMediumTimes = ((RankingsScreenCtrl)ctrl).getRankingByTimeMedium();
        JPanel mediumTimesPanel = buildRankingTile(
                "MEDIUM",
                infoMediumTimes,
                new String[] {"Name", "Avg time"},
                new String[] {"name", "avgTime"},
                width/4 - 80
        );
        mediumTimesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        rightTop.add(easyTimesPanel);
        rightTop.add(Box.createRigidArea(new Dimension(20,20)));
        rightTop.add(mediumTimesPanel);

        JPanel rightBottom = new JPanel();
        rightBottom.setLayout(new BoxLayout(rightBottom, BoxLayout.X_AXIS));

        // hard times
        ArrayList<Map<String, Object>> infoHardTimes = ((RankingsScreenCtrl)ctrl).getRankingByTimeHard();
        JPanel hardTimesPanel = buildRankingTile(
                "HARD",
                infoHardTimes,
                new String[] {"Name", "Avg time"},
                new String[] {"name", "avgTime"},
                width/4 - 80
        );
        hardTimesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // extreme times
        ArrayList<Map<String, Object>> infoExtremeTimes = ((RankingsScreenCtrl)ctrl).getRankingByTimeExtreme();
        JPanel extremeTimesPanel = buildRankingTile(
                "EXTREME",
                infoExtremeTimes,
                new String[] {"Name", "Avg time"},
                new String[] {"name", "avgTime"},
                width/4 - 80
        );
        extremeTimesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        rightBottom.add(hardTimesPanel);
        rightBottom.add(Box.createRigidArea(new Dimension(20,20)));
        rightBottom.add(extremeTimesPanel);

        right.add(title);
        right.add(sep);
        right.add(Box.createRigidArea(new Dimension(10,10)));
        right.add(rightTop);
        right.add(Box.createRigidArea(new Dimension(20,20)));
        right.add(rightBottom);
        right.add(Box.createRigidArea(new Dimension(width/2-100,1)));

        return right;
    }

    private JPanel buildRankingTile(String title, ArrayList<Map<String, Object>> allUserInfo, String[] titles, String[] keys, int width) {
        // at position 0 there should be user names so the BOLD marking works.
        JPanel tile = new JPanel();
        tile.setLayout(new GridBagLayout());

        JLabel titleLbl = buildLabel(title, titleFnt, SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.BLACK);

        JPanel titlesPanel = new JPanel();
        titlesPanel.setLayout(new GridBagLayout());
        titlesPanel.setOpaque(false);
        GridBagConstraints ctrs = new GridBagConstraints();

        ctrs.insets = new Insets(5, 15, 0, 15);
        ctrs.fill = GridBagConstraints.HORIZONTAL;
        ctrs.gridy = 0;
        for (int i = 0; i < titles.length; i++) {
            ctrs.gridx = i;
            titlesPanel.add(buildLabel(titles[i], subtitleFnt, SwingConstants.CENTER), ctrs);
        }
        ctrs.gridx = 0;
        ctrs.gridy = 1;
        ctrs.gridwidth = titles.length;
        titlesPanel.add(Box.createRigidArea(new Dimension(width, 1)), ctrs);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(allUserInfo.size(), titles.length, 2, 2));

        for (int i = 0; i < allUserInfo.size(); i++) {
            Map<String, Object> userInfo = allUserInfo.get(i);
            boolean isOfInterest = userInfo.get(keys[0]).equals(userOfInterest);
            Font infoFont = bodyFnt;
            if (isOfInterest) infoFont = importantBodyFnt;

            JPanel infoLine = new JPanel();
            infoLine.setLayout(new GridBagLayout());
            ctrs.gridy = 0;
            ctrs.gridwidth = 1;

            for (int j = 0; j < keys.length; j++) {
                ctrs.gridx = j;
                Object toAdd = userInfo.get(keys[j]);
                String toAddStr;
                if (toAdd instanceof Float) {
                    if (keys[j].contains("time") || keys[j].contains("Time"))
                        toAddStr = secondsToStringTime((float) toAdd);
                    else if(Math.floor((float)toAdd) == Math.ceil((float)toAdd)) {
                        // Number is integer. Remove decimals or display "---" if zero
                        int val = Math.round((float)toAdd);
                        toAddStr = (val == 0 ? "---" : "" + val);
                    } else
                        // round to 2 decimal places
                        toAddStr = "" + Math.round((float) toAdd * 100.0f) / 100.0f;
                } else if (!(toAdd instanceof String)) {
                    toAddStr = toAdd.toString();
                } else {
                    toAddStr = (String) toAdd;
                }
                JLabel itemLbl = buildLabel(toAddStr, infoFont, SwingConstants.CENTER);
                infoLine.add(itemLbl, ctrs);
            }

            if (isOfInterest) infoLine.setBackground(Color.LIGHT_GRAY);

            infoPanel.add(infoLine);
        }

        JScrollPane infoScroll = new JScrollPane(infoPanel);
        infoScroll.getVerticalScrollBar().setUnitIncrement(20);
        infoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        ctrs.insets = new Insets(3, 5, 2, 5);
        ctrs.fill = GridBagConstraints.HORIZONTAL;
        ctrs.gridx = 0;
        ctrs.gridy = 0;
        tile.add(titleLbl, ctrs);

        ctrs.gridy = 1;
        tile.add(sep, ctrs);

        ctrs.gridy = 2;
        tile.add(titlesPanel, ctrs);

        ctrs.gridy = 3;
        ctrs.fill = GridBagConstraints.BOTH;
        tile.add(infoScroll, ctrs);

        ctrs.gridy = 4;
        ctrs.fill = GridBagConstraints.HORIZONTAL;
        tile.add(Box.createRigidArea(new Dimension(width, 2)), ctrs);

        return tile;
    }

    private JLabel buildLabel(String text, Font font, int horizontalAlign) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.BLACK);
        label.setOpaque(false);
        label.setHorizontalAlignment(horizontalAlign);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private String secondsToStringTime(float time) {
        if(Float.isNaN(time)) return "---";

        int hours = (int)time/3600;
        int minutes = (int)time/60 - hours*60;
        int seconds = (int)time - minutes*60 - hours*3600;
        String timeStr = "";
        if (hours > 0) {
            timeStr += hours+":";
            if (minutes < 10) timeStr += "0";
        }
        timeStr += minutes+":";
        if (seconds < 10) timeStr += "0";
        timeStr += seconds;

        return timeStr;
    }

    @Override
    public void onShow() {}
    @Override
    public void onHide() {}
    @Override
    public void onDestroy() {}
}
