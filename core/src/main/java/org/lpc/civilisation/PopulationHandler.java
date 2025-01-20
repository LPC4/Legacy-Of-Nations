package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.TickedTimer;

import static org.lpc.utility.Constants.POPULATION_CHANGE_DELAY_TICKS;
import static org.lpc.utility.Constants.POPULATION_FOOD_CONSUMPTION_DELAY_TICKS;

@Getter
@Setter
public class PopulationHandler {
    private static final Logger LOGGER = LogManager.getLogger(PopulationHandler.class);

    private final Civilisation civilisation;
    private int population;

    private final TickedTimer populationChangeTimer;
    private final TickedTimer foodConsumptionTimer;

    public PopulationHandler(Civilisation civilisation, int population) {
        this.civilisation = civilisation;
        this.population = population;
        this.populationChangeTimer = new TickedTimer(POPULATION_CHANGE_DELAY_TICKS);
        this.foodConsumptionTimer = new TickedTimer(POPULATION_FOOD_CONSUMPTION_DELAY_TICKS);
    }

    public void update() {
        populationChangeTimer.tick();
        foodConsumptionTimer.tick();

        if (foodConsumptionTimer.isTime()) {
            feedPopulation();
        }

        if (populationChangeTimer.isTime()) {
            changePopulation();
        }
    }

    private void feedPopulation() {
        LOGGER.info("Feeding population: {}", getFoodConsumption());
        civilisation.consumeResource(ResourceType.FOOD, getFoodConsumption());
    }

    private void changePopulation() {
        int food = civilisation.getResourceAmount(ResourceType.FOOD);

        if (hasEnoughFoodForGrowth(food)) {
            growPopulation(1); // Grow by 1% every tick
        }
    }

    private boolean hasEnoughFoodForGrowth(int food) {
        // Ensure there is enough food to support growth
        return food >= getFoodConsumption() * 2;
    }

    private int getFoodConsumption() {
        return population / 10;
    }

    private void growPopulation(int percentage) {
        LOGGER.info("Growing population: {}", percentage);
        population += (population / 100) * percentage;
    }

    public int getStartingRadius() {
        return population / 50;
    }
}
