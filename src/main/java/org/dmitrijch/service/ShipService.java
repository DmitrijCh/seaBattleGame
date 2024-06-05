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

import java.util.Arrays;
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
        String upperPositionX = positionX.toUpperCase();
        // Проверка на допустимые буквы
        if (!Arrays.asList("А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К").contains(upperPositionX)) {
            throw new ShipPlacementException("Некорректное размещение корабля: Некорректная буквенная координата.");
        }
        return upperPositionX.charAt(0) - 'А' + 1;
    }

    private int getShipLength(int shipType) {
        return shipType;
    }

    private String isPlacementValid(Player player, int startX, int startY, int length, String orientation) {
        // Проверка на отрицательные значения
        if (startX < 1 || startY < 1) {
            return "Координаты не могут быть отрицательными или равными нулю.";
        }

        // Проверка на допустимую длину корабля
        if (length < 1 || length > 4) {
            return "Недопустимая длина корабля.";
        }

        // Проверка на количество кораблей
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
        if (length == 4 && shipCounts[3] >= 1) {
            return "Нельзя разместить более одного четырехпалубного корабля.";
        } else if (length == 3 && shipCounts[2] >= 2) {
            return "Нельзя разместить более двух трехпалубных кораблей.";
        } else if (length == 2 && shipCounts[1] >= 3) {
            return "Нельзя разместить более трех двухпалубных кораблей.";
        } else if (length == 1 && shipCounts[0] >= 4) {
            return "Нельзя разместить более четырех однопалубных кораблей.";
        }

        // Проверка на выход за границы игрового поля
        int endX = startX + (orientation.equals("horizontal") ? length - 1 : 0);
        int endY = startY + (orientation.equals("vertical") ? length - 1 : 0);

        if (startX > 11 || startY > 10 || endX > 11 || endY > 10) {
            return "Корабль выходит за границы игрового поля.";
        }

        // Проверка на пересечение с другими кораблями и их соседними клетками
        for (Ship ship : existingShips) {
            int shipStartX = positionXToNumber(ship.getPositionX());
            int shipStartY = ship.getPositionY();
            int existingShipLength = getShipLength(ship.getShipType());

            int shipEndX = shipStartX + (ship.getOrientation().equals("horizontal") ? existingShipLength - 1 : 0);
            int shipEndY = shipStartY + (ship.getOrientation().equals("vertical") ? existingShipLength - 1 : 0);

            // Проверяем, находится ли новый корабль внутри области, где уже находится другой корабль
            if (!(endX < shipStartX - 1 || startX > shipEndX + 1 || endY < shipStartY - 1 || startY > shipEndY + 1)) {
                return "Корабль пересекается с другим кораблем или находится в соседней клетке.";
            }
        }
        return null;
    }
}
