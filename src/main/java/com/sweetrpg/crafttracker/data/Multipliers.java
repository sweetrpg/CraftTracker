package com.sweetrpg.crafttracker.data;

import java.util.Map;

import static java.util.Map.entry;

public class Multipliers {

    public static final Map<String, Float> namespaces = Map.ofEntries(
            entry("minecraft", 1f),
            entry("create", 1.5f),
            entry("ars_nouveau", 1.1f),
            entry("celestialexploration", 1.7f)
    );

    public static final Map<String, Float> recipeTypes = Map.ofEntries(
            entry("crafting", 1f),
            entry("smelting", 1.1f),
            entry("blasting", 1.2f),
            entry("crushing", 1.1f)
    );
}
