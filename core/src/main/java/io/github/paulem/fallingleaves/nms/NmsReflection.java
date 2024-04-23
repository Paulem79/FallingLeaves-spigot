package io.github.paulem.fallingleaves.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class NmsReflection {
    private final String name = Bukkit.getServer().getClass().getPackage().getName();
    private final String version = name.substring(name.lastIndexOf('.') + 1);

    private final DecimalFormat format = new DecimalFormat("##.##");

    private Object serverInstance;
    private Field tpsField;

    public void initReflection(){
        try {
            serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
            tpsField = serverInstance.getClass().getField("recentTps");
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the TPS using reflection
     *
     * @param time 0 = last minute, 1 = 5min, 2 = 15min
     * @return The TPS
     */
    public Double getTPS(int time) {
        try {
            double[] tps = ((double[]) tpsField.get(serverInstance));
            return Double.valueOf(format.format(tps[time]));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
