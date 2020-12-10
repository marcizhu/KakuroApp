package src.repository;

import src.domain.entities.Board;

import java.util.UUID;

public interface BoardRepository {
    Board getBoard (UUID id);
}
