package org.lpc.terrain.buildings;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class BaseBuilding {
    private final BuildingFunctionality type;
    private final int maxHealth;
    @Setter
    int health;

    public BaseBuilding(BuildingFunctionality functionality, int health) {
        this.type = functionality;
        this.health = health;
        this.maxHealth = health;
    }

    public abstract void update();

    public boolean isDestroyed() {
        return health <= 0;
    }

    public void damage(int damage) {
        health -= damage;
    }

    public void repair(int repair) {
        health += repair;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
}
