package t.me.p1azmer.engine.utils;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.random.Rnd;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtil {
    public static final Pattern PATTERN_HEX = Pattern.compile("#([A-Fa-f0-9]{6})");
    public static final Pattern PATTERN_GRADIENT = Pattern.compile("<gradient:" + PATTERN_HEX.pattern() + ">(.*?)</gradient:" + PATTERN_HEX.pattern() + ">");

    public static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m");

    /**
     * Cached result of all legacy colors.
     *
     * @since 1.0.0
     */
    private static final Map<java.awt.Color, ChatColor> COLORS = ImmutableMap.<java.awt.Color, ChatColor>builder()
            .put(new java.awt.Color(0), ChatColor.getByChar('0'))
            .put(new java.awt.Color(170), ChatColor.getByChar('1'))
            .put(new java.awt.Color(43520), ChatColor.getByChar('2'))
            .put(new java.awt.Color(43690), ChatColor.getByChar('3'))
            .put(new java.awt.Color(11141120), ChatColor.getByChar('4'))
            .put(new java.awt.Color(11141290), ChatColor.getByChar('5'))
            .put(new java.awt.Color(16755200), ChatColor.getByChar('6'))
            .put(new java.awt.Color(11184810), ChatColor.getByChar('7'))
            .put(new java.awt.Color(5592405), ChatColor.getByChar('8'))
            .put(new java.awt.Color(5592575), ChatColor.getByChar('9'))
            .put(new java.awt.Color(5635925), ChatColor.getByChar('a'))
            .put(new java.awt.Color(5636095), ChatColor.getByChar('b'))
            .put(new java.awt.Color(16733525), ChatColor.getByChar('c'))
            .put(new java.awt.Color(16733695), ChatColor.getByChar('d'))
            .put(new java.awt.Color(16777045), ChatColor.getByChar('e'))
            .put(new java.awt.Color(16777215), ChatColor.getByChar('f'))
            .build();

    @NotNull
    public static java.awt.Color getColor(@NotNull String colorRaw) {
        String[] rgb = colorRaw.split(",");
        int red = StringUtil.getInteger(rgb[0], 0);
        if (red < 0) red = Rnd.get(255);

        int green = rgb.length >= 2 ? StringUtil.getInteger(rgb[1], 0) : 0;
        if (green < 0) green = Rnd.get(255);

        int blue = rgb.length >= 3 ? StringUtil.getInteger(rgb[2], 0) : 0;
        if (blue < 0) blue = Rnd.get(255);

        return new java.awt.Color(red, green, blue);
    }

    @NotNull
    public static java.awt.Color getColor(int red, int green, int blue) {
        if (red < 0) red = Rnd.get(255);

        if (green < 0) green = Rnd.get(255);

        if (blue < 0) blue = Rnd.get(255);

        return new java.awt.Color(red, green, blue);
    }

    @NotNull
    private static String injectColors(@NotNull String source, ChatColor[] colors) {
        StringBuilder specialColors = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        String[] characters = source.split("");
        int outIndex = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].equals("&") || characters[i].equals("§")) {
                if (i + 1 < characters.length) {
                    if (characters[i + 1].equals("r")) {
                        specialColors.setLength(0);
                    } else {
                        specialColors.append(characters[i]);
                        specialColors.append(characters[i + 1]);
                    }
                    i++;
                } else stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
            } else stringBuilder.append(colors[outIndex++]).append(specialColors).append(characters[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * Colors a String with rainbow colors.
     *
     * @param string     The string which should have rainbow colors
     * @param saturation The saturation of the rainbow colors
     * @since 1.0.3
     */
    @Nonnull
    public static String rainbow(@Nonnull String string, float saturation) {
        StringBuilder specialColors = new StringBuilder();
        for (String color : SPECIAL_COLORS) {
            if (string.contains(color)) {
                specialColors.append(color);
                string = string.replace(color, "");
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        ChatColor[] colors = createRainbow(string.length(), saturation);
        String[] characters = string.split("");
        for (int i = 0; i < string.length(); i++) {
            stringBuilder.append(colors[i]).append(specialColors).append(characters[i]);
        }
        return stringBuilder.toString();
    }

    private static ChatColor[] createGradient(@NotNull java.awt.Color start, @NotNull java.awt.Color end, int length) {
        ChatColor[] colors = new ChatColor[length];
        for (int index = 0; index < length; index++) {
            double percent = (double) index / (double) length;

            int red = (int) (start.getRed() + percent * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + percent * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + percent * (end.getBlue() - start.getBlue()));

            java.awt.Color color = new java.awt.Color(red, green, blue);
            colors[index] = ChatColor.valueOf(color.toString());
        }
        return colors;
    }

    /**
     * Returns a rainbow array of chat colors.
     *
     * @param step       How many colors we return
     * @param saturation The saturation of the rainbow
     * @return The array of colors
     * @since 1.0.3
     */
    @Nonnull
    private static ChatColor[] createRainbow(int step, float saturation) {
        ChatColor[] colors = new ChatColor[step];
        double colorStep = (1.00 / step);
        for (int i = 0; i < step; i++) {
            java.awt.Color color = java.awt.Color.getHSBColor((float) (colorStep * i), saturation, saturation);
            colors[i] = ChatColor.valueOf(color.toString());
        }
        return colors;
    }

    /**
     * Returns the closest legacy color from an rgb color
     *
     * @param color The color we want to transform
     * @since 1.0.0
     */
    @Nonnull
    private static ChatColor getClosestColor(java.awt.Color color) {
        java.awt.Color nearestColor = null;
        double nearestDistance = Integer.MAX_VALUE;

        for (java.awt.Color constantColor : COLORS.keySet()) {
            double distance = Math.pow(color.getRed() - constantColor.getRed(), 2) + Math.pow(color.getGreen() - constantColor.getGreen(), 2) + Math.pow(color.getBlue() - constantColor.getBlue(), 2);
            if (nearestDistance > distance) {
                nearestColor = constantColor;
                nearestDistance = distance;
            }
        }
        return COLORS.get(nearestColor);
    }

    @NotNull
    public static <T extends Enum<T>> Optional<T> getEnum(@NotNull String str, @NotNull Class<T> clazz) {
        try {
            return Optional.of(Enum.valueOf(clazz, str.toUpperCase()));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @NotNull
    public static String capitalizeUnderscored(@NotNull String str) {
        return capitalizeFully(str.replace("_", " "));
    }

    @NotNull
    public static String lowerCaseUnderscore(@NotNull String str) {
        return Colorizer.restrip(str).toLowerCase().replace(" ", "_");
    }

    /**
     * Removes all color codes from the provided String, including IridiumColorAPI patterns.
     *
     * @param string The String which should be stripped
     * @return The stripped string without color codes
     * @since 1.0.5
     */
    @Nonnull
    public static String stripColorFormatting(@Nonnull String string) {
        return string.replaceAll("[&§][a-f0-9lnokm]|<[/]?\\w{5,8}(:[0-9A-F]{6})?>", "");
    }

    @NotNull
    public static String oneSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

    @NotNull
    public static String noSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", "");
    }

    @NotNull
    @Deprecated
    public static String color(@NotNull String str) {
        return Colorizer.apply(str);
    }

    @NotNull
    @Deprecated
    public static String colorGradient(@NotNull String string) {
        return Colorizer.gradient(string);
    }

    @NotNull
    @Deprecated
    public static String colorHex(@NotNull String str) {
        return Colorizer.hex(str);
    }

    @NotNull
    @Deprecated
    public static String colorHexRaw(@NotNull String str) {
        return Colorizer.plainHex(str);
    }

    @NotNull
    @Deprecated
    public static String colorRaw(@NotNull String str) {
        return Colorizer.plain(str);
    }

    @NotNull
    @Deprecated
    public static String colorOff(@NotNull String str) {
        return Colorizer.strip(str);
    }

    @NotNull
    @Deprecated
    public static List<String> color(@NotNull List<String> list) {
        return Colorizer.apply(list);
    }

    @NotNull
    @Deprecated
    public static Set<String> color(@NotNull Set<String> list) {
        return Colorizer.apply(list);
    }


    @NotNull
    public static List<String> replace(@NotNull List<String> orig, @NotNull String placeholder, boolean keep, String... replacer) {
        return StringUtil.replace(orig, placeholder, keep, Arrays.asList(replacer));
    }

    @NotNull
    public static List<String> replace(@NotNull List<String> orig, @NotNull String placeholder, boolean keep, List<String> replacer) {
        List<String> replaced = new ArrayList<>();
        for (String line : orig) {
            if (line.contains(placeholder)) {
                if (!keep) {
                    replaced.addAll(replacer);
                } else {
                    replacer.forEach(lineRep -> replaced.add(line.replace(placeholder, lineRep)));
                }
                continue;
            }
            replaced.add(line);
        }

        return replaced;
    }

    @NotNull
    public static String replaceEach(@NotNull String text, @NotNull List<Pair<String, Supplier<String>>> replacements) {
        if (text.isEmpty() || replacements.isEmpty()) {
            return text;
        }

        final int searchLength = replacements.size();
        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i]) {
                continue;
            }
            tempIndex = text.indexOf(replacements.get(i).getFirst());

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else if (textIndex == -1 || tempIndex < textIndex) {
                textIndex = tempIndex;
                replaceIndex = i;
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;
        final StringBuilder buf = new StringBuilder();
        while (textIndex != -1) {
            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacements.get(replaceIndex).getSecond().get());

            start = textIndex + replacements.get(replaceIndex).getFirst().length();

            textIndex = -1;
            replaceIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i]) {
                    continue;
                }
                tempIndex = text.indexOf(replacements.get(i).getFirst(), start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        return buf.toString();
    }


    public static double getDouble(@NotNull String input, double def) {
        return getDouble(input, def, false);
    }

    public static double getDouble(@NotNull String input, double def, boolean allowNegative) {
        try {
            double amount = Double.parseDouble(input);
            return (amount < 0D && !allowNegative ? def : amount);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static int getInteger(@NotNull String input, int def) {
        return getInteger(input, def, false);
    }

    public static int getInteger(@NotNull String input, int def, boolean allowNegative) {
        return (int) getDouble(input, def, allowNegative);
    }

    public static int[] getIntArray(@NotNull String str) {
        String[] split = noSpace(str).split(",");
        int[] array = new int[split.length];
        for (int index = 0; index < split.length; index++) {
            try {
                array[index] = Integer.parseInt(split[index]);
            } catch (NumberFormatException e) {
                array[index] = 0;
            }
        }
        return array;
    }

    @NotNull
    public static String capitalizeFully(@NotNull String str) {
        if (str.length() != 0) {
            str = str.toLowerCase();
            return capitalize(str);
        }
        return str;
    }

    @NotNull
    public static String capitalize(@NotNull String str) {
        if (str.length() != 0) {
            int strLen = str.length();
            StringBuilder buffer = new StringBuilder(strLen);
            boolean capitalizeNext = true;

            for (int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (Character.isWhitespace(ch)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }
            return buffer.toString();
        }
        return str;
    }

    @NotNull
    public static String capitalizeFirstLetter(@NotNull String original) {
        if (original.isEmpty()) return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    /**
     * @param original List to remove empty lines from.
     * @return A list with no multiple empty lines in a row.
     */
    @NotNull
    public static List<String> stripEmpty(@NotNull List<String> original) {
        List<String> stripped = new ArrayList<>();
        for (int index = 0; index < original.size(); index++) {
            String line = original.get(index);
            if (line.isEmpty()) {
                String last = stripped.isEmpty() ? null : stripped.get(stripped.size() - 1);
                if (last == null || last.isEmpty() || index == (original.size() - 1)) continue;
            }
            stripped.add(line);
        }
        return stripped;
    }


    /**
     * Kinda half-smart completer like in IDEA by partial word matches.
     *
     * @param results A list of all completions.
     * @param input   A string to find partial matches for.
     * @param steps   Part's size.
     * @return A list of completions that has partial matches to the given string.
     */
    @NotNull
    public static List<String> getByPartialMatches(@NotNull List<String> results, @NotNull String input, int steps) {
        StringBuilder builder = new StringBuilder();
        for (char letter : input.toLowerCase().toCharArray()) {
            builder.append(Pattern.quote(String.valueOf(letter))).append("(?:.*)");
        }

        Pattern pattern = Pattern.compile(builder.toString());
        List<String> list = new ArrayList<>(results.stream().filter(orig -> pattern.matcher(orig.toLowerCase()).find()).collect(Collectors.toList()));
        Collections.sort(list);
        return list;
    }

    @NotNull
    public static Color parseColor(@NotNull String colorRaw) {
        String[] rgb = colorRaw.split(",");
        int red = StringUtil.getInteger(rgb[0], 0);
        if (red < 0) red = Rnd.get(255);

        int green = rgb.length >= 2 ? StringUtil.getInteger(rgb[1], 0) : 0;
        if (green < 0) green = Rnd.get(255);

        int blue = rgb.length >= 3 ? StringUtil.getInteger(rgb[2], 0) : 0;
        if (blue < 0) blue = Rnd.get(255);

        return Color.fromRGB(red, green, blue);
    }

    @NotNull
    public static String extractCommandName(@NotNull String command) {
        String commandName = Colorizer.strip(command).split(" ")[0].substring(1);
        String[] pluginPrefix = commandName.split(":");
        if (pluginPrefix.length == 2) {
            commandName = pluginPrefix[1];
        }

        return commandName;
    }

    public static boolean isCustomBoolean(@NotNull String str) {
        String[] customs = new String[]{"0", "1", "on", "off", "true", "false", "yes", "no"};
        return Stream.of(customs).collect(Collectors.toSet()).contains(str.toLowerCase());
    }

    public static boolean parseCustomBoolean(@NotNull String str) {
        if (str.equalsIgnoreCase("0") || str.equalsIgnoreCase("off") || str.equalsIgnoreCase("no")) {
            return false;
        }
        if (str.equalsIgnoreCase("1") || str.equalsIgnoreCase("on") || str.equalsIgnoreCase("yes")) {
            return true;
        }
        return Boolean.parseBoolean(str);
    }
}