package com.sweetrpg.crafttracker.common.util;

import com.google.common.collect.Lists;
import com.sweetrpg.crafttracker.common.lib.Constants;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import net.minecraftforge.registries.RegistryObject;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

}
