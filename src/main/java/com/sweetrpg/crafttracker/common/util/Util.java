package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.common.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Miscellaneous utilities.
 */
public class Util {

    public static final Path STORAGE_DIR = FMLPaths.GAMEDIR.get().resolve("craft_tracker");

    private static final DecimalFormat dfShort = new DecimalFormat("0.0");
    private static final DecimalFormat dfShortDouble = new DecimalFormat("0.00");

    /**
     * Get the storage path to read and write local files.
     *
     * @return The {@link Path} to use for storage
     */
    public static Path getStoragePath() {
        var addressPath = "";

        var server = Minecraft.getInstance().getCurrentServer();
        if(server != null) {
            addressPath = server.ip
                    .replace(".", "_")
                    .replace(":", "_")
                    .replace(File.pathSeparator, "_")
                    .trim()
                    .toLowerCase(Locale.ROOT);
        }
        else {
            addressPath = Minecraft.getInstance()
                    .getSingleplayerServer()
                    .getServerDirectory().getName()
                    .replace(".", "_")
                    .replace(":", "_")
                    .replace(" ", "_")
                    .replace(File.pathSeparator, "_")
                    .trim()
                    .toLowerCase(Locale.ROOT);
        }

        return STORAGE_DIR.resolve(addressPath).normalize();
    }

    /**
     * @param name The path of the resource
     */
    public static ResourceLocation getResource(String name) {
        return getResource(Constants.MOD_ID, name);
    }

    public static ResourceLocation getResource(String modId, String name) {
        return new ResourceLocation(modId, name);
    }

    public static String getResourcePath(String name) {
        return getResourcePath(Constants.MOD_ID, name);
    }

    public static ResourceLocation modLoc(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    public static ResourceLocation mcLoc(String name) {
        return new ResourceLocation(name);
    }

    /**
     * @param modId The namespace
     * @param name  The path
     * @return The total path of the resource e.g "minecraft:air"
     */
    public static String getResourcePath(String modId, String name) {
        return getResource(modId, name).toString();
    }

    public static int parseColor(String colorValue, int radix, int defaultValue) {
        try {
            return Integer.parseInt(colorValue, radix);
        }
        catch (Exception e) {
            // ignore
        }

        return defaultValue;
    }

    public static double getConfigValueOrDefault(ForgeConfigSpec.DoubleValue value, double defaultValue) {
        if(value != null) {
            return value.get();
        }

        return defaultValue;
    }

}
