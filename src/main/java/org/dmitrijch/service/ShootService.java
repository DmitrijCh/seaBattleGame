package org.dmitrijch.service;

import org.dmitrijch.entity.Ship;
import org.dmitrijch.entity.Shot;
import org.dmitrijch.repository.ShipRepository;
import org.dmitrijch.repository.ShotRepository;
import org.dmitrijch.response.ShootResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShootService {
    private final ShipRepository shipRepository;
    private final ShotRepository shotRepository;

    @Autowired
    public ShootService(ShipRepository shipRepository, ShotRepository shotRepository) {
        this.shipRepository = shipRepository;
        this.shotRepository = shotRepository;
    }

    public ShootResponse shoot(Long playerId, String x, int y) {
        // Проверка, был ли уже сделан выстрел
        if (shotRepository.existsByPlayerIdAndXAndY(playerId, x, y)) {
            return new ShootResponse("Сюда уже стреляли");
        }

        // Запись выстрела
        Shot shot = new Shot(playerId, x, y);
        shotRepository.save(shot);

        List<Ship> playerShips = shipRepository.findByPlayerId(playerId);

        // Проверка, находится ли какой-либо корабль игрока на указанной позиции
        for (Ship ship : playerShips) {
            if (isPointInsideShip(ship, x, y)) {
                ship.hit();
                shipRepository.save(ship);
                if (ship.isSunk()) {
                    // Проверка, остались ли еще корабли у игрока после потопления текущего корабля
                    boolean hasRemainingShips = shipRepository.findByPlayerId(playerId).stream().anyMatch(s -> !s.isSunk());
                    if (!hasRemainingShips) {
                        return new ShootResponse("Победа");
                    } else {
                        return new ShootResponse("Убит");
                    }
                } else {
                    return new ShootResponse("Ранен");
                }
            }
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
