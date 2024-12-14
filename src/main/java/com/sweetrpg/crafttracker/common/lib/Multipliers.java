package com.sweetrpg.crafttracker.common.lib;

import java.util.Map;

import static java.util.Map.entry;

public class Multipliers {

    public static final Map<String, Float> namespaces = Map.ofEntries(
            entry("minecraft", 1f),
            entry("create", 1.5f)
    );

    public static final Map<String, Float> recipeTypes = Map.ofEntries(
            entry("minecraft:crafting", 1f),
            entry("minecraft:smelting", 1.1f),
            entry("minecraft:blasting", 1.2f),
            entry("create:crushing", 1.1f)
    );
}
