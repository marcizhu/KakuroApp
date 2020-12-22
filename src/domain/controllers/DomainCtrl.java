package src.domain.controllers;

import src.domain.entities.*;
import src.repository.*;
import src.utils.Pair;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class DomainCtrl {
    UserRepository userRepository;
    KakuroRepository kakuroRepository;
    GameRepository gameRepository;

    UserCtrl userCtrl;
    KakuroCtrl kakuroCtrl;
    GameCtrl gameCtrl;
    RankingCtrl rankingCtrl;

    public DomainCtrl() {
        DB driver = new DB();

        userRepository = new UserRepositoryDB(driver);
        kakuroRepository = new KakuroRepositoryDB(driver);
        gameRepository = new GameRepositoryDB(driver);

        userCtrl = new UserCtrl(userRepository);
        kakuroCtrl = new KakuroCtrl(kakuroRepository, userRepository, gameRepository);
        gameCtrl = new GameCtrl(gameRepository, userRepository);
        rankingCtrl = new RankingCtrl(gameRepository, userRepository);
    }

    /**
     * Retreives a list of all the Users in the Database
     * @return List of KeyValues containing the information of each User + error message
     */
    public Pair<ArrayList<String>, String> getUserList() {
        try {
            ArrayList<String> userList = userCtrl.getUserList();
            return new Pair<>(userList, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Verifies that a given username has a valid profile
     * @param username  User that aims to be verified
     * @return Boolean that indicates if the operation was successful + error message
     */
    public Pair<Boolean, String> loginUser(String username) {
        try {
            boolean userExists = userCtrl.loginUser(username);
            if (!userExists) return new Pair<>(false, "Invalid user");
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Registers a new User to the Database
     * @param username  User that aims to be registered
     * @return Boolean that indicates if the operation was successful + error message
     */
    public Pair<Boolean, String> registerUser(String username) {
        try {
            boolean userRegistered = userCtrl.registerUser(username);
            if (!userRegistered) return new Pair<>(false, "User already exists");
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the list of Kakuros created by a given User
     * @param username  User that will be used as a filter
     * @return List of KeyValues containing the information of each Kakuro on the list (sorted ASC by created at) + error value
     */
    public Pair<ArrayList<Map<String, Object>>, String> getKakuroListByUser(String username) {
        try {
            User user = userCtrl.getUser(username);
            ArrayList<Map<String, Object>> data = kakuroCtrl.getKakuroListByUser(user);
            return new Pair<>(data, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the list of Kakuros for a given difficulty
     * @param username  User that will be evaluated in order to compute the Kakuro state
     * @param difficulty  Difficulty that will filter the Kakuros
     * @return List of KeyValues containing the information of each Kakuro on the list (sorted ASC by created at) + error value
     */
    public Pair<ArrayList<Map<String, Object>>, String> getKakuroListByDifficulty(String difficulty, String username) {
        try {
            User user = userCtrl.getUser(username);
            ArrayList<Map<String, Object>> data = kakuroCtrl.getKakuroListByDifficulty(Difficulty.valueOf(difficulty), user);
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the Game history for a given User
     * @param username  User that will be evaluated
     * @return List of KeyValues containing the information of each Game played by the user (sorted ASC by last played) + error value
     */
    public Pair<ArrayList<Map<String, Object>>, String> getGameHistory(String username) {
        try {
            ArrayList<Map<String, Object>> data = gameCtrl.getGameHistory(username);
            return new Pair<>(data, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the total of games played by given User in all the different difficulties
     * @param username  User that will be evaluated
     * @return KeyValue containing the number of games played in all difficulties (including USER_MADE) + error value
     */
    public Pair<Map<String, Integer>, String> getNumberOfGamesPlayed(String username) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Integer> data = gameCtrl.getNumberOfGamesPlayed(user);
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the top pointer for a given User in all the different difficulties
     * @param username  User that will be evaluated
     * @return KeyValue containing the top pointer information + error value
     */
    public Pair<Map<String, Object>, String> getTopPointer(String username) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> data = gameCtrl.getTopPointer(user);
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the top pointer for a given User in a given Difficulty
     * @param username  User that will be evaluated
     * @param difficulty  Difficulty in which the User will be evaluated
     * @return KeyValue containing the top pointer information + error value
     */
    public Pair<Map<String, Object>, String> getTopPointerInDifficulty(String username, String difficulty) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> data = gameCtrl.getTopPointerInDifficulty(user, Difficulty.valueOf(difficulty));
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves time statistics for a given User in a given Difficulty
     * @param username  User that will be evaluated
     * @param difficulty  Difficulty in which the User will be evaluated
     * @return KeyValue containing the time statistics + error value
     */
    public Pair<Map<String, Integer>, String> getTimeStatisticsInDifficulty(String username, String difficulty) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Integer> data = gameCtrl.getTimeStatisticsInDifficulty(user, Difficulty.valueOf(difficulty));
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Retrieves the points Ranking
     * @return KeyValue containing the points ranking + error message
     */
    public Pair<ArrayList<Map<String, Object>>, String> getRankingByPoints() {
        try {
            ArrayList<Map<String, Object>> data = rankingCtrl.getRankingByPoints();
            return new Pair<>(data, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    /**
     * Retrieves the games played Ranking
     * @return KeyValue containing the games played ranking + error message
     */
    public Pair<ArrayList<Map<String, Object>>, String> getRankingByGamesPlayed() {
        try {
            ArrayList<Map<String, Object>> data = rankingCtrl.getRankingByGamesPlayed();
            return new Pair<>(data, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    /**
     * Retrieves the Ranking for a given difficulty
     * @param difficulty  Difficulty that wants to be ranked
     * @return KeyValue containing the desired ranking + error message
     */
    public Pair<ArrayList<Map<String, Object>>, String> getRankingByTimeInDifficulty(String difficulty) {
        try {
            ArrayList<Map<String, Object>> data = rankingCtrl.getRankingByTimeInDifficulty(difficulty);
            return new Pair<>(data, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    /**
     * Creates a Gameplay instance for a chosen Kakuro
     * @param username  Identifier of the User that wants to play a Kakuro
     * @param kakuroname  Identifier of the Kakuro that the user wants to play
     * @return GameplayCtrl instance for the chosen Game + error message
     */
    public Pair<GameplayCtrl, String> newGameInstance(String username, String kakuroname) {
        try {
            GameInProgress game = gameCtrl.getGameInProgress(username, kakuroname);
            if (game != null) return new Pair<>(new GameplayCtrl(game, gameCtrl), null);
            User user = userCtrl.getUser(username);
            Kakuro kakuro = kakuroCtrl.getKakuro(kakuroname);
            return new Pair<>(new GameplayCtrl(user, kakuro, gameCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Imports a Kakuro from a file and creates a Gameplay instance for such
     * @param username  Identifier of the User that wants to import a Kakuro
     * @param filePath  Path of the file that contains the Kakuro that will be imported
     * @param kakuroname  Identifier of the Kakuro that will be imported
     * @return GameplayCtrl instance for the imported Game + error message
     */
    public Pair<GameplayCtrl, String> newImportedGameInstance(String username, String filePath, String kakuroname) {
        try {
            User user = userCtrl.getUser(username);
            Kakuro kakuro = kakuroCtrl.saveKakuroFromFile(user, filePath, kakuroname);
            return new Pair<>(new GameplayCtrl(user, kakuro, gameCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Starts a new creation
     * @param username  Identifier of the User that wants to import to start a new creation
     * @param rows  number of rows for the creation
     * @param columns  number of columns for the creation
     * @return KakuroCreationCtrl instance with the creation loaded + error message
     */
    public Pair<KakuroCreationCtrl, String> newCreatorInstance(String username, int rows, int columns) {
        try {
            User user = userCtrl.getUser(username);
            return new Pair<>(new KakuroCreationCtrl(user, rows, columns, kakuroCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Imports a WIP creation
     * @param username  Identifier of the User that wants to import a WIP creation
     * @param filePath  Path of the file containing the creation
     * @return KakuroCreationCtrl instance with the creation loaded + error message
     */
    public Pair<KakuroCreationCtrl, String> newImportedCreatorInstance(String username, String filePath) {
        try {
            User user = userCtrl.getUser(username);
            Board board = Reader.fromFile(filePath);
            return new Pair<>(new KakuroCreationCtrl(user, board, kakuroCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Generates a Kakuro according to a set of parameters and stores it in the DB
     * @param username  Identifier of the User that wants to generate the Kakuro
     * @param rows  number of rows for the Kakuro
     * @param columns  number of columns for the Kakuro
     * @param difficulty  difficulty of rows for the Kakuro
     * @param forceUnique  whether or not the uniqueness of the Kakuro will be forced (adding values to the board)
     * @param kakuroName  Identifier of the Kakuro
     * @return KeyValue that contains the board and information about the generation process + error message
     */
    public Pair<Map<String, Object>, String> generateKakuroFromParameters(String username, int rows, int columns, String difficulty, boolean forceUnique, String kakuroName) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> data = kakuroCtrl.saveKakuroFromGeneratorParameters(user, rows, columns, Difficulty.valueOf(difficulty), forceUnique, kakuroName);
            return new Pair<>(data, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Generates a Kakuro with a given seed and stores it in the DB
     * @param username  Identifier of the User that wants to generate the Kakuro
     * @param seed  Seed that will be used in order to generate the Kakuro
     * @param kakuroName  Identifier of the Kakuro that aims to be generated
     * @return KeyValue that contains the board and information about the generation process + error message
     */
    public Pair<Map<String, Object>, String> generateKakuroFromSeed(String username, String seed, String kakuroName) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> data = kakuroCtrl.saveKakuroFromGeneratorSeed(user, seed, kakuroName);
            return new Pair<>(data, null);
        } catch (Exception e) {
            if (e.getMessage().equals("seed_format")) return new Pair<>(null, "Seed format follows: <rows>_<columns>_<E/M/H/X>_<F/N>_<seed>");
            return new Pair<>(null, e.getMessage());
        }
    }

    /**
     * Writes the chosen Kakuro to the given file
     * @param kakuroName  Identifier of the Kakuro that aims to be exported
     * @param filePath  Path where the Kakuro will be written
     * @return Boolean that indicates if the operation was successful + error message
     */
    public Pair<Boolean, String> exportKakuro(String kakuroName, String filePath) {
        try {
            Kakuro kakuro = kakuroCtrl.getKakuro(kakuroName);
            FileWriter myWriter = new FileWriter(filePath+".txt");
            myWriter.write(kakuro.getBoard().toString() + "\n");
            myWriter.close();
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }
}
