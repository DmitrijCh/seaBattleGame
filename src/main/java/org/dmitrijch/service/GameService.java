package org.dmitrijch.service;

import org.dmitrijch.entity.Game;
import org.dmitrijch.repository.GameRepository;
import org.dmitrijch.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final ShipRepository shipRepository;

    @Autowired
    public GameService(GameRepository gameRepository, ShipRepository shipRepository) {
        this.gameRepository = gameRepository;
        this.shipRepository = shipRepository;
    }

    public Game startNewGame() {
        Game game = new Game();
        return gameRepository.save(game);
    }
}
