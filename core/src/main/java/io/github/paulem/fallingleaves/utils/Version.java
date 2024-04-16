package io.github.paulem.fallingleaves.utils;

import org.bukkit.Bukkit;

public record Version(int major, int minor, int revision) {
    /**
     * 1.19.4 -> 1
     *
     * @return the major
     */
    @Override
    public int major() {
        return major;
    }

    /**
     * 1.19.4 -> 19
     *
     * @return the minor
     */
    @Override
    public int minor() {
        return minor;
    }

    /**
     * 1.19.4 -> 4
     *
     * @return the revision
     */
    @Override
    public int revision() {
        return revision;
    }

    public static Version getVersion() {
        int major, minor, revision;

        String version = Bukkit.getVersion();

        // Extraire la version de Minecraft
        String mcVersion = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);
        String[] mcParts = mcVersion.split("\\.");

        major = Integer.parseInt(mcParts[0]);
        minor = Integer.parseInt(mcParts[1]);
        revision = Integer.parseInt(mcParts[2]);
        return new Version(major, minor, revision);
    }

    @Override
    public String toString() {
        return major() + "." + minor() + "." + revision();
    }
}
