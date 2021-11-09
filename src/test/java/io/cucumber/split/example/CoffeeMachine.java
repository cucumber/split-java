package io.cucumber.split.example;

import io.split.client.SplitClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.emptySortedSet;

/**
 * A simple coffee machine that displays available drinks. It can offer an experimental cappuccino
 * drink that is toggled on/off with Split.
 */
public class CoffeeMachine {
    private final SplitClient splitClient;
    private final String splitKey;
    private double level;

    public CoffeeMachine(SplitClient splitClient, String splitKey) {
        this.splitClient = splitClient;
        this.splitKey = splitKey;
    }

    /**
     * Indicate how full the machine is
     *
     * @param level a number between 0 and 1
     */
    public void setLevel(double level) {
        this.level = level;
    }

    public SortedSet<SKU> getAvailableDrinks() {
        if (this.level == 0) return emptySortedSet();

        SortedSet<SKU> availableDrinks = new TreeSet<>(Comparator.comparing(SKU::getPrice).thenComparing(SKU::getName));
        availableDrinks.add(new SKU("caffe latte", 2.30));
        availableDrinks.add(new SKU("espresso", 1.90));
        if ("on".equals(this.splitClient.getTreatment(splitKey, "flat-white"))) {
            availableDrinks.add(new SKU("flat white", 2.10));
        }
        return Collections.unmodifiableSortedSet(availableDrinks);
    }
}
