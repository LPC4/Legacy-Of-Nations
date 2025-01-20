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
            int excessFoodPercentage = (food - population) / population;

            growPopulation(excessFoodPercentage / 10);
        } else if (doesNotHaveEnoughFood(food)) {
            int foodShortagePercentage = (population - food) / population;

            shrinkPopulation(foodShortagePercentage / 10);
        }
    }

    private boolean hasEnoughFoodForGrowth(int food) {
        // Ensure there is enough food to support growth
        return food >= population * 2;
    }

    private boolean doesNotHaveEnoughFood(int food) {
        // Ensure there is not enough food to support the population
        return food <  population;
    }

    private int getFoodConsumption() {
        return population;
    }

    private void growPopulation(int percentage) {
        LOGGER.info("Growing population: {}", percentage);
        // Ensure at least 1 person grows if the percentage calculation is too small
        int growth = Math.max((population * percentage) / 100, 1);
        population += growth;
    }

    private void shrinkPopulation(int percentage) {
        LOGGER.info("Shrinking population: {}", percentage);
        // Ensure at least 1 person decreases if the percentage calculation is too small
        int decline = Math.max((population * percentage) / 100, 1);
        population -= decline;
    }

    public int getStartingRadius() {
        return population / 50;
    }


}
