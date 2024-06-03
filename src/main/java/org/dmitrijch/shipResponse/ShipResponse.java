package org.dmitrijch.shipResponse;

import org.dmitrijch.entity.Ship;

public class ShipResponse {
    private Ship ship;
    private String message;

    public ShipResponse(Ship ship, String message) {
        this.ship = ship;
        this.message = message;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
