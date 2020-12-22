package src.presentation.screens;

import src.presentation.controllers.DashboardScreenCtrl;
import src.presentation.controllers.StatisticsScreenCtrl;
import src.presentation.utils.Palette;
import src.presentation.views.BarChartView;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsScreen extends AbstractScreen {

    private Font titleFnt;
    private Font subtitleFnt;
    private Font bodyFnt;
    private Font importantBodyFnt;
    private Font smallFnt;

    private String userOfInterest;

    public StatisticsScreen(StatisticsScreenCtrl ctrl) { super(ctrl); }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setLayout(new BorderLayout());

        titleFnt = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        subtitleFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        bodyFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        importantBodyFnt = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        smallFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 9);

        userOfInterest = ((StatisticsScreenCtrl)ctrl).interestPlayer();

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.X_AXIS));

        JPanel leftSection = buildLeftSection();
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

    private JPanel buildLeftSection() {
        JPanel overallPanel = new JPanel();
        overallPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("OVERALL", titleFnt, SwingConstants.CENTER);

        JLabel rankLbl = buildLabel("Ranking", subtitleFnt, SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.BLACK);

        String interest = ((StatisticsScreenCtrl)ctrl).interestPlayer();
        ArrayList<Pair<Integer, Pair<String, String>>> ranking = ((StatisticsScreenCtrl)ctrl).getTopRanking();

        JPanel topRankPanel = new JPanel();
        topRankPanel.setLayout(new GridLayout(ranking.size(), 2));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < ranking.size(); i++) {
            Pair<Integer, Pair<String, String>> item = ranking.get(i);
            Font itemFnt = bodyFnt;
            if (item.second.first.equals(interest)) itemFnt = new Font(Font.SANS_SERIF, Font.BOLD, 12);

            constraints.gridx = 0;
            constraints.gridy = i;
            topRankPanel.add(buildLabel("#" + (item.first+1) + ": " + item.second.first, itemFnt, SwingConstants.LEFT));

            constraints.gridx = 1;
            topRankPanel.add(buildLabel(item.second.second, itemFnt, SwingConstants.RIGHT));
        }
        topRankPanel.setOpaque(false);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.BLACK);

        JLabel topPointerLbl = buildLabel("Your top pointer", subtitleFnt, SwingConstants.LEFT);

        Map<String, Object> topPointerInfo = ((StatisticsScreenCtrl)ctrl).getTopPointer();
        JPanel topPointerPanel = new JPanel();
        topPointerPanel.setLayout(new GridBagLayout());

        if (topPointerInfo.size() == 0) {
            constraints.insets = new Insets(65,0,65,0);
            constraints.gridx = 0;
            constraints.gridy = 0;
            topPointerPanel.add(buildLabel("No games played yet.", bodyFnt, SwingConstants.CENTER), constraints);
        } else {
            KakuroView topPointerKak = new KakuroView((String) topPointerInfo.get("board"), (Integer) topPointerInfo.get("color"), false);
            topPointerKak.setSize(150, 150);

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridheight = 5;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(0,5,0,25);
            topPointerPanel.add(topPointerKak, constraints);
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets.right = 5;
            constraints.insets.bottom = 10;
            constraints.gridheight = 1;
            topPointerPanel.add(buildLabel((String) topPointerInfo.get("score"), subtitleFnt, SwingConstants.RIGHT), constraints);
            constraints.insets.bottom = 2;
            constraints.gridy = 1;
            topPointerPanel.add(buildLabel((String) topPointerInfo.get("name"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 2;
            topPointerPanel.add(buildLabel("Difficulty: " + topPointerInfo.get("difficulty"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 3;
            topPointerPanel.add(buildLabel("Size: " + topPointerInfo.get("height") + "x" + topPointerInfo.get("width"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 4;
            constraints.insets.bottom = 0;
            topPointerPanel.add(buildLabel("Time: " + secondsToStringTime((int) topPointerInfo.get("timeSpent")), bodyFnt, SwingConstants.RIGHT), constraints);
        }
        topPointerPanel.setOpaque(false);

        JSeparator sep3 = new JSeparator();
        sep3.setForeground(Color.BLACK);

        JLabel playedLbl = buildLabel("Total kakuros played", subtitleFnt, SwingConstants.LEFT);

        Map<String, Integer> kakurosPlayedInfo = ((StatisticsScreenCtrl)ctrl).getGamesPlayed();

        BarChartView playedChartPanel = new BarChartView();
        playedChartPanel.setBarValueFont(subtitleFnt);
        playedChartPanel.setBarLabelFont(smallFnt);
        playedChartPanel.addBar("EASY",     kakurosPlayedInfo.get("easy"),     Palette.HintGreen);
        playedChartPanel.addBar("MEDIUM",   kakurosPlayedInfo.get("medium"),   Palette.HintOrange);
        playedChartPanel.addBar("HARD",     kakurosPlayedInfo.get("hard"),     Palette.WarningLightRed);
        playedChartPanel.addBar("EXTREME",  kakurosPlayedInfo.get("extreme"),  Palette.SelectionBlue);
        playedChartPanel.addBar("BY USERS", kakurosPlayedInfo.get("userMade"), new Color(0xAAAAAA));
        playedChartPanel.setPreferredHeight(ranking.size() == 3 ? 144 : 128);
        playedChartPanel.layoutHistogram();
        playedChartPanel.setBackground(Color.WHITE);
        playedChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        constraints.insets = new Insets(5,5,5,5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        overallPanel.add(title, constraints);

        constraints.gridy = 1;
        overallPanel.add(topPointerLbl, constraints);

        constraints.gridy = 2;
        overallPanel.add(rankLbl, constraints);

        constraints.gridy = 3;
        overallPanel.add(sep, constraints);

        constraints.gridy = 4;
        overallPanel.add(topRankPanel, constraints);

        constraints.gridy = 5;
        overallPanel.add(sep2, constraints);

        constraints.gridy = 6;
        overallPanel.add(topPointerLbl, constraints);

        constraints.gridy = 7;
        overallPanel.add(topPointerPanel, constraints);

        constraints.gridy = 8;
        overallPanel.add(sep3, constraints);

        constraints.gridy = 9;
        overallPanel.add(playedLbl, constraints);

        constraints.gridy = 10;
        constraints.fill = GridBagConstraints.BOTH;
        overallPanel.add(playedChartPanel, constraints);

        overallPanel.setBackground(Color.LIGHT_GRAY);
        overallPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return overallPanel;
    }

    private JPanel buildRightSection(int width) {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel rightTop = new JPanel();
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.X_AXIS));

        Map<String, String> userTimesEasy = ((StatisticsScreenCtrl)ctrl).getUserTimesInDifficulty("EASY");
        JPanel easyStatsPanel = buildStatisticsDifficultyTile(
                "EASY",
                ((StatisticsScreenCtrl)ctrl).getTopRankingByDifficulty("EASY"),
                ((StatisticsScreenCtrl)ctrl).getTopPointerInDifficulty("EASY"),
                userTimesEasy.get("bestTime"),
                userTimesEasy.get("avgTime"),
                ((StatisticsScreenCtrl)ctrl).getEasyGamesPlayed(),
                width*3/10
        );

        Map<String, String> userTimesMedium = ((StatisticsScreenCtrl)ctrl).getUserTimesInDifficulty("MEDIUM");
        JPanel mediumStatsPanel = buildStatisticsDifficultyTile(
                "MEDIUM",
                ((StatisticsScreenCtrl)ctrl).getTopRankingByDifficulty("MEDIUM"),
                ((StatisticsScreenCtrl)ctrl).getTopPointerInDifficulty("MEDIUM"),
                userTimesMedium.get("bestTime"),
                userTimesMedium.get("avgTime"),
                ((StatisticsScreenCtrl)ctrl).getMediumGamesPlayed(),
                width*3/10
        );

        rightTop.add(easyStatsPanel);
        rightTop.add(Box.createRigidArea(new Dimension(20,20)));
        rightTop.add(mediumStatsPanel);

        JPanel rightBottom = new JPanel();
        rightBottom.setLayout(new BoxLayout(rightBottom, BoxLayout.X_AXIS));

        Map<String, String> userTimesHard = ((StatisticsScreenCtrl)ctrl).getUserTimesInDifficulty("HARD");
        JPanel hardStatsPanel = buildStatisticsDifficultyTile(
                "HARD",
                ((StatisticsScreenCtrl)ctrl).getTopRankingByDifficulty("HARD"),
                ((StatisticsScreenCtrl)ctrl).getTopPointerInDifficulty("HARD"),
                userTimesHard.get("bestTime"),
                userTimesHard.get("avgTime"),
                ((StatisticsScreenCtrl)ctrl).getHardGamesPlayed(),
                width*3/10
        );

        Map<String, String> userTimesExtreme = ((StatisticsScreenCtrl)ctrl).getUserTimesInDifficulty("EXTREME");
        JPanel extremeStatsPanel = buildStatisticsDifficultyTile(
                "EXTREME",
                ((StatisticsScreenCtrl)ctrl).getTopRankingByDifficulty("EXTREME"),
                ((StatisticsScreenCtrl)ctrl).getTopPointerInDifficulty("EXTREME"),
                userTimesExtreme.get("bestTime"),
                userTimesExtreme.get("avgTime"),
                ((StatisticsScreenCtrl)ctrl).getExtremeGamesPlayed(),
                width*3/10
        );

        rightBottom.add(hardStatsPanel);
        rightBottom.add(Box.createRigidArea(new Dimension(20,20)));
        rightBottom.add(extremeStatsPanel);

        right.add(rightTop);
        right.add(Box.createRigidArea(new Dimension(20,20)));
        right.add(rightBottom);

        return right;
    }

    private JPanel buildStatisticsDifficultyTile(String title, ArrayList<Pair<Integer, Pair<String, String>>> ranking, Map<String, Object> topPointer, String bestTime, String avgTime, int gamesPlayed, int width) {
        JPanel tile = new JPanel();
        tile.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel titleLbl = buildLabel(title, titleFnt, SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.BLACK);

        JPanel tileContent = new JPanel();
        tileContent.setLayout(new BoxLayout(tileContent, BoxLayout.X_AXIS));

        JPanel leftTile = new JPanel();
        leftTile.setLayout(new GridBagLayout());

        JLabel rankingLbl = buildLabel("Ranking", subtitleFnt, SwingConstants.LEFT);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.BLACK);

        JPanel topRankPanel = new JPanel();
        topRankPanel.setLayout(new GridLayout(ranking.size(), 2));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < ranking.size(); i++) {
            Pair<Integer, Pair<String, String>> item = ranking.get(i);
            Font itemFnt = bodyFnt;
            if (item.second.first.equals(userOfInterest)) itemFnt = new Font(Font.SANS_SERIF, Font.BOLD, 12);

            constraints.gridx = 0;
            constraints.gridy = i;
            topRankPanel.add(buildLabel("#" + (item.first+1) + ": " + item.second.first, itemFnt, SwingConstants.LEFT));

            constraints.gridx = 1;
            topRankPanel.add(buildLabel(item.second.second, itemFnt, SwingConstants.RIGHT));
        }
        topRankPanel.setOpaque(false);

        JSeparator sep3 = new JSeparator();
        sep3.setForeground(Color.BLACK);

        JLabel bestTimeLbl = buildLabel("Best time: " + bestTime, bodyFnt, SwingConstants.LEFT);
        JLabel avgTimeLbl = buildLabel("Avg. time: " + avgTime, bodyFnt, SwingConstants.LEFT);
        JLabel timesPlayedLbl = buildLabel("Games played: " + gamesPlayed, bodyFnt, SwingConstants.LEFT);

        JSeparator sep4 = new JSeparator();
        sep4.setOrientation(JSeparator.VERTICAL);
        sep4.setForeground(Color.BLACK);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5,5,5,5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        leftTile.add(rankingLbl, constraints);
        constraints.gridy = 1;
        leftTile.add(sep2, constraints);
        constraints.gridy = 2;
        leftTile.add(topRankPanel, constraints);
        constraints.gridy = 3;
        leftTile.add(sep3, constraints);
        constraints.gridy = 4;
        leftTile.add(bestTimeLbl, constraints);
        constraints.gridy = 5;
        leftTile.add(avgTimeLbl, constraints);
        constraints.gridy = 6;
        leftTile.add(timesPlayedLbl, constraints);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 7;
        leftTile.add(sep4, constraints);
        constraints.gridheight = 1;
        leftTile.setOpaque(false);

        JPanel topPointerPanel = new JPanel();
        topPointerPanel.setLayout(new GridBagLayout());

        if (topPointer.size() == 0) {
            constraints.insets = new Insets(65,40,65,40);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.BOTH;
            topPointerPanel.add(buildLabel("No games played yet.", bodyFnt, SwingConstants.CENTER), constraints);
        } else {
            KakuroView topPointerKak = new KakuroView((String) topPointer.get("board"), (Integer) topPointer.get("color"), false);
            topPointerKak.setSize(150, 150);

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridheight = 5;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(0,5,0,5);
            topPointerPanel.add(topPointerKak, constraints);
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets.right = 5;
            constraints.insets.bottom = 10;
            constraints.gridheight = 1;
            constraints.gridy = 1;
            topPointerPanel.add(buildLabel((String) topPointer.get("score"), subtitleFnt, SwingConstants.RIGHT), constraints);
            constraints.insets.bottom = 2;
            constraints.gridy = 2;
            topPointerPanel.add(buildLabel((String) topPointer.get("name"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 3;
            topPointerPanel.add(buildLabel("Size: " + topPointer.get("height") + "x" + topPointer.get("width"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 4;
            constraints.insets.bottom = 0;
            topPointerPanel.add(buildLabel("Time: " + secondsToStringTime((int) topPointer.get("timeSpent")), bodyFnt, SwingConstants.RIGHT), constraints);
        }
        topPointerPanel.setOpaque(false);

        tileContent.add(leftTile);
        tileContent.add(topPointerPanel);
        tileContent.setOpaque(false);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10,10,3,10);
        tile.add(titleLbl, constraints);
        constraints.gridy = 1;
        constraints.insets.top = 2;
        constraints.insets.bottom = 8;
        tile.add(sep, constraints);
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        tile.add(tileContent, constraints);
        constraints.gridy = 3;
        constraints.insets.top = 0;
        constraints.insets.bottom = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        tile.add(Box.createRigidArea(new Dimension(width, 1)), constraints);

        tile.setBackground(Color.LIGHT_GRAY);
        tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
    private String secondsToStringTime(int time) {
        int hours = time/3600;
        int minutes = time/60 - hours*60;
        int seconds = time - minutes*60 - hours*3600;
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
