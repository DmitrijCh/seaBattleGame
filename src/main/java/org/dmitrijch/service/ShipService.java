package org.dmitrijch.service;

import jakarta.transaction.Transactional;
import org.dmitrijch.entity.Player;
import org.dmitrijch.entity.Ship;
import org.dmitrijch.exeption.ShipPlacementException;
import org.dmitrijch.repository.PlayerRepository;
import org.dmitrijch.repository.ShipRepository;
import org.dmitrijch.request.ShipRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {
    private final ShipRepository shipRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository, PlayerRepository playerRepository) {
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Ship placeShip(ShipRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ShipPlacementException("Игрок не найден"));

        int startX = positionXToNumber(request.getPositionX());
        int startY = request.getPositionY();
        int shipLength = getShipLength(request.getShipType());

        String validationError = isPlacementValid(player, startX, startY, shipLength, request.getOrientation());
        if (validationError != null) {
            throw new ShipPlacementException("Некорректное размещение корабля: " + validationError);
        }

        Ship ship = new Ship();
        ship.setPlayer(player);
        ship.setShipType(request.getShipType());
        ship.setPositionX(request.getPositionX());
        ship.setPositionY(request.getPositionY());
        ship.setOrientation(request.getOrientation());

        return shipRepository.save(ship);
    }

    private int positionXToNumber(String positionX) {
        return positionX.charAt(0) - 'А' + 1;
    }

    private int getShipLength(int shipType) {
        return shipType;
    }

    private String isPlacementValid(Player player, int startX, int startY, int length, String orientation) {
        // Определение длины корабля в зависимости от его типа
        int shipLength = 0;
        switch (length) {
            case 1:
                shipLength = 1;
                break;
            case 2:
                shipLength = 2;
                break;
            case 3:
                shipLength = 3;
                break;
            case 4:
                shipLength = 4;
                break;
            default:
                return "Недопустимая длина корабля.";
        }

        // Проверка на количество кораблей и их каждой длины
        List<Ship> existingShips = shipRepository.findByPlayer(player);
        int[] shipCounts = new int[4];
        int totalShips = existingShips.size();

        if (totalShips >= 10) {
            return "Достигнуто максимальное количество кораблей игрока.";
        }

        for (Ship ship : existingShips) {
            int shipType = ship.getShipType();
            if (shipType >= 1 && shipType <= 4) {
                shipCounts[shipType - 1]++;
            }
        }

        // Проверка на превышение количества кораблей каждой длины
        if (shipLength == 4 && shipCounts[3] >= 1) {
            return "Нельзя разместить более одного четырехпалубного корабля.";
        } else if (shipLength == 3 && shipCounts[2] >= 2) {
            return "Нельзя разместить более двух трехпалубных кораблей.";
        } else if (shipLength == 2 && shipCounts[1] >= 3) {
            return "Нельзя разместить более трех двухпалубных кораблей.";
        } else if (shipLength == 1 && shipCounts[0] >= 4) {
            return "Нельзя разместить более четырех однопалубных кораблей.";
        }

        // Проверка на выход за границы игрового поля
        if ("horizontal".equals(orientation)) {
            if (startX + shipLength - 1 > 10) return "Корабль выходит за границы игрового поля.";
        } else if ("vertical".equals(orientation)) {
            if (startY + shipLength - 1 > 10) return "Корабль выходит за границы игрового поля.";
        } else {
            return "Некорректная ориентация корабля.";
        }

        // Проверка на пересечение с другими кораблями и их соседними клетками
        List<Ship> otherShips = shipRepository.findByPlayer(player);
        for (Ship ship : otherShips) {
            int shipStartX = positionXToNumber(ship.getPositionX());
            int shipStartY = ship.getPositionY();
            int existingShipLength = getShipLength(ship.getShipType());

            for (int i = -1; i <= existingShipLength; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x, y;
                    if ("horizontal".equals(ship.getOrientation())) {
                        x = shipStartX + i;
                        y = shipStartY + j;
                    } else if ("vertical".equals(ship.getOrientation())) {
                        x = shipStartX + j;
                        y = shipStartY + i;
                    } else {
                        return "Некорректная ориентация существующего корабля.";
                    }

                    for (int k = 0; k < shipLength; k++) {
                        int newX, newY;
                        if ("horizontal".equals(orientation)) {
                            newX = startX + k;
                            newY = startY;
                        } else {
                            newX = startX;
                            newY = startY + k;
                        }

                        if (newX == x && newY == y) {
                            return "Корабль пересекается с другим кораблем или находится в соседней клетке.";
                        }
                    }
                }
            }
        }

        return null;
    }
}
