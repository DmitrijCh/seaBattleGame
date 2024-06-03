package org.dmitrijch.service;

import org.dmitrijch.entity.Game;
import org.dmitrijch.entity.Player;
import org.dmitrijch.repository.GameRepository;
import org.dmitrijch.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Player addPlayer(Long gameId, String name) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        Player player = new Player();
        player.setGame(game);
        player.setName(name);

        return playerRepository.save(player);
    }
}
