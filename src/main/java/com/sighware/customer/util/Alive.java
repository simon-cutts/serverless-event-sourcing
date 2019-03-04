package com.sighware.customer.util;

public class Alive {
    private boolean isAlive = false;

    public Alive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
