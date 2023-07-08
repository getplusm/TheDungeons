package t.me.p1azmer.engine;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum Version {

    // KEEP VERSIONS LIST FROM LOWER TO HIGHER
    V1_16_R3("1.16.5"),
    V1_18_R2("1.18.2"),
    V1_19_R3("1.19.4"),
    V1_20_R1("1.20"),
    V1_20_R2("1.20.1")
    ;

    private final boolean deprecated;
    private final String  localized;

    Version(@NotNull String localized) {
        this(localized, false);
    }

    Version(@NotNull String localized, boolean deprecated) {
        this.localized = localized;
        this.deprecated = deprecated;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @NotNull
    public String getLocalized() {
        return localized;
    }

    public static final Version CURRENT;

    static {
        String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        String versionRaw = split[split.length - 1];

        try {
            CURRENT = Version.valueOf(versionRaw.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public boolean isLower(@NotNull Version version) {
        return this.ordinal() < version.ordinal();
    }

    public boolean isHigher(@NotNull Version version) {
        return this.ordinal() > version.ordinal();
    }

    public static boolean isAtLeast(@NotNull Version version) {
        return version.isCurrent() || CURRENT.isHigher(version);
    }

    public static boolean isAbove(@NotNull Version version) {
        return CURRENT.isHigher(version);
    }

    public static boolean isBehind(@NotNull Version version) {
        return CURRENT.isLower(version);
    }

    public boolean isCurrent() {
        return this == Version.CURRENT;
    }
}
