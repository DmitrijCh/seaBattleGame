package org.dmitrijch.service;

import org.dmitrijch.entity.Ship;
import org.dmitrijch.repository.ShipRepository;
import org.dmitrijch.response.ShootResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShootService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShootService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public ShootResponse shoot(Long playerId, String x, int y) {
        List<Ship> playerShips = shipRepository.findByPlayerId(playerId);
        boolean allShipsSunk = true;

        // Проверка, находится ли какой-либо корабль игрока на указанной позиции
        for (Ship ship : playerShips) {
            // Проверяем, находится ли точка (x, y) внутри корабля
            if (isPointInsideShip(ship, x, y)) {
                ship.hit();
                shipRepository.save(ship);
                if (ship.isSunk()) {
                    return new ShootResponse("Убит");
                } else {
                    return new ShootResponse("Ранен");
                }
            } else if (!ship.isSunk()) {
                // Если хотя бы один корабль не потоплен, устанавливается флаг allShipsSunk в false
                allShipsSunk = false;
            }
        }

        if (allShipsSunk) {
            return new ShootResponse("Победа");
        }
        return new ShootResponse("Мимо");
    }

    // Проверка, является ли корабль горизонтальным или вертикальным
    private boolean isPointInsideShip(Ship ship, String x, int y) {
        if (ship.getOrientation().equals("horizontal")) {
            // Если корабль горизонтальный, проверяется, находится ли точка между начальной и конечной координатами корабля по оси X
            if (ship.getPositionX().charAt(0) <= x.charAt(0) && x.charAt(0) <= (char) (ship.getPositionX().charAt(0) + ship.getShipType() - 1)) {
                return ship.getPositionY() == y;
            }
        } else {
            // Если корабль вертикальный, проверяется, находится ли точка между начальной и конечной координатами корабля по оси Y
            if (ship.getPositionY() <= y && y <= ship.getPositionY() + ship.getShipType() - 1) {
                return ship.getPositionX().equals(x);
            }
        }
        return false;
    }
}
