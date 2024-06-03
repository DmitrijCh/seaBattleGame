package org.dmitrijch.controller;

import org.dmitrijch.entity.Game;
import org.dmitrijch.entity.Player;
import org.dmitrijch.entity.Ship;
import org.dmitrijch.request.PlayerRequest;
import org.dmitrijch.request.ShipRequest;
import org.dmitrijch.response.ShipResponse;
import org.dmitrijch.service.GameService;
import org.dmitrijch.service.PlayerService;
import org.dmitrijch.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private final GameService gameService;
    private final PlayerService playerService;
    private final ShipService shipService;

    @Autowired
    public Controller(GameService gameService, PlayerService playerService, ShipService shipService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.shipService = shipService;
    }

    @PostMapping("/startGame")
    public Game startGame() {
        return gameService.startNewGame();
    }

    @PostMapping("/addPlayer")
    public Player addPlayer(@RequestBody PlayerRequest request) {
        return playerService.addPlayer(request.getGameId(), request.getName());
    }

    @PostMapping("/putShip")
    public ResponseEntity<ShipResponse> placeShip(@RequestBody ShipRequest request) {
        Ship ship = shipService.placeShip(request);
        ShipResponse response = new ShipResponse(ship, "Корабль успешно размещен");
        return ResponseEntity.ok(response);
    }
}
