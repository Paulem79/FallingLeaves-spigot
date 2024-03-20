package io.github.paulem.fallingleaves.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NMSHandler {
    public static String minecraftVersion;

    /**
     * Returns the actual running Minecraft version, e.g. 1.20 or 1.16.5
     *
     * @return Minecraft version
     */
    public static String getMinecraftVersion() {
        if (minecraftVersion != null) {
            return minecraftVersion;
        } else {
            String bukkitGetVersionOutput = Bukkit.getVersion();
            Matcher matcher = Pattern.compile("\\(MC: (?<version>[\\d]+\\.[\\d]+(\\.[\\d]+)?)\\)").matcher(bukkitGetVersionOutput);
            if (matcher.find()) {
                return minecraftVersion = matcher.group("version");
            } else {
                throw new RuntimeException("Could not determine Minecraft version from Bukkit.getVersion(): " + bukkitGetVersionOutput);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ILeavesColor getLeavesColorImpl() throws RuntimeException {
        String clazzName = "io.github.paulem.nms_" + getMinecraftVersion()
                .replace(".", "_") + ".LeavesColorImpl";
        try {
            Class<? extends ILeavesColor> clazz = (Class<? extends ILeavesColor>) Class.forName(clazzName);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Can't instantiate NMSHandlerImpl for version " + getMinecraftVersion() +
                    " (class " + clazzName + " not found. This usually means that this Minecraft version is not " +
                    "supported by this version of the plugin.)", exception);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("Can't instantiate NMSHandlerImpl for version " + getMinecraftVersion() +
                    " (constructor in class " + clazzName + " threw an exception)", exception);
        } catch (InstantiationException exception) {
            throw new RuntimeException("Can't instantiate NMSHandlerImpl for version " + getMinecraftVersion() +
                    " (class " + clazzName + " is abstract)", exception);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Can't instantiate NMSHandlerImpl for version " + getMinecraftVersion() +
                    " (no-args constructor in class " + clazzName + " is not accessible)", exception);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("Can't instantiate NMSHandlerImpl for version " + getMinecraftVersion() +
                    " (no no-args constructor found in class " + clazzName + ")", exception);
        }
    }
}
