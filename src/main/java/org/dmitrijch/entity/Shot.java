package org.dmitrijch.entity;

import jakarta.persistence.*;

@Entity
public class Shot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "playerid")
    private Long playerId;

    @Column(name = "x")
    private String x;

    @Column(name = "y")
    private int y;

    public Shot(Long playerId, String x, int y) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
