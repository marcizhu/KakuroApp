package src.repository;

import src.domain.entities.Board;

import java.util.UUID;

public class BoardRepositoryDB implements BoardRepository {

    private final DB driver;

    public BoardRepositoryDB (DB driver) {
        this.driver = driver;
    }

    @Override
    public Board getBoard(UUID id) {
        //TODO
        return new Board(10, 10);
    }
}
