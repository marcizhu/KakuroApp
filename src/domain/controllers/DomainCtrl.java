package src.domain.controllers;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.presentation.views.KakuroInfoCardView;

import java.util.ArrayList;

public class DomainCtrl {

    public String login(String userName) {
        return "faefzcx";
    }



    // Just for testing purposes:
    // One string array per kakuro with the following strings:
    // board.toString(), kakuroID, difficulty, times played, owner, date, recordTime, state
    public ArrayList<ArrayList<String>> getMyKakurosList(String sessionID) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        String board = "";
        try {
            board = Reader.fromFile("data/kakuros/solved/jutge.kak").toString();
        } catch (Exception e) {
            System.out.println(e);
        }

        for (int i = 0; i < 14; i++) {
            ArrayList<String> partial = new ArrayList<>();
            partial.add(board);
            partial.add("Breakfast kakuro");
            partial.add("Hard");
            partial.add("12");
            partial.add("Cesc");
            partial.add("2020.3.1");
            partial.add("1:23");
            partial.add("unfinished");
            result.add(partial);
        }

        return result;
    }

    //More testing purposes, start a new game
    public GameCtrl newGameInstance(String sessionID, String kakuroID) {
        User user = new User(sessionID);
        Kakuro kakuro;
        try {
            kakuro = new Kakuro(Difficulty.HARD, Reader.fromFile("data/kakuros/unsolved/jutge.kak"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return new GameCtrl(user, kakuro);
    }
}
