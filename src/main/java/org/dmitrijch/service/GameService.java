package org.dmitrijch.service;

import org.dmitrijch.entity.Game;
import org.dmitrijch.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game startNewGame() {
        Game game = new Game();
        return gameRepository.save(game);
    }
}
