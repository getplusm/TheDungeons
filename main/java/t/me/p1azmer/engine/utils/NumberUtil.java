package t.me.p1azmer.engine.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class NumberUtil {

    private static final DecimalFormat FORMAT_ROUND_HUMAN;
    private final static TreeMap<Integer, String> ROMAN_MAP = new TreeMap<>();

    static {
        FORMAT_ROUND_HUMAN = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.ENGLISH));

        ROMAN_MAP.put(1000, "M");
        ROMAN_MAP.put(900, "CM");
        ROMAN_MAP.put(500, "D");
        ROMAN_MAP.put(400, "CD");
        ROMAN_MAP.put(100, "C");
        ROMAN_MAP.put(90, "XC");
        ROMAN_MAP.put(50, "L");
        ROMAN_MAP.put(40, "XL");
        ROMAN_MAP.put(10, "X");
        ROMAN_MAP.put(9, "IX");
        ROMAN_MAP.put(5, "V");
        ROMAN_MAP.put(4, "IV");
        ROMAN_MAP.put(1, "I");
    }

    @NotNull
    public static String format(double value) {
        return FORMAT_ROUND_HUMAN.format(value);
    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @NotNull
    public static String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);

        int key = ROMAN_MAP.floorKey(number);
        if (number == key) {
            return ROMAN_MAP.get(number);
        }
        return ROMAN_MAP.get(key) + toRoman(number - key);
    }
    /*
    public static String toRoman(int input) {
        if (input < 1 || input > 3999) {
            return "N/A";
        }

        StringBuilder s = new StringBuilder();
        while (input >= 1000) {
            s.append("M");
            input -= 1000;
        }
        while (input >= 900) {
            s.append("CM");
            input -= 900;
        }
        while (input >= 500) {
            s.append("D");
            input -= 500;
        }
        while (input >= 400) {
            s.append("CD");
            input -= 400;
        }
        while (input >= 100) {
            s.append("C");
            input -= 100;
        }
        while (input >= 90) {
            s.append("XC");
            input -= 90;
        }
        while (input >= 50) {
            s.append("L");
            input -= 50;
        }
        while (input >= 40) {
            s.append("XL");
            input -= 40;
        }
        while (input >= 10) {
            s.append("X");
            input -= 10;
        }
        while (input >= 9) {
            s.append("IX");
            input -= 9;
        }
        while (input >= 5) {
            s.append("V");
            input -= 5;
        }
        while (input >= 4) {
            s.append("IV");
            input -= 4;
        }
        while (input >= 1) {
            s.append("I");
            input -= 1;
        }
        return s.toString();
    }*/

    @Deprecated
    public static int fromRoman(@NotNull String romanNumber) {
        int decimal = 0;
        int lastNumber = 0;
        String romanNumeral = romanNumber.toUpperCase();
        for (int x = romanNumeral.length() - 1; x >= 0; x--) {
            char convertToDecimal = romanNumeral.charAt(x);

            switch (convertToDecimal) {
                case 'M':
                    decimal = processDecimal(1000, lastNumber, decimal);
                    lastNumber = 1000;
                    break;
                case 'D':
                    decimal = processDecimal(500, lastNumber, decimal);
                    lastNumber = 500;
                    break;
                case 'C':
                    decimal = processDecimal(100, lastNumber, decimal);
                    lastNumber = 100;
                    break;
                case 'L':
                    decimal = processDecimal(50, lastNumber, decimal);
                    lastNumber = 50;
                    break;
                case 'X':
                    decimal = processDecimal(10, lastNumber, decimal);
                    lastNumber = 10;
                    break;
                case 'V':
                    decimal = processDecimal(5, lastNumber, decimal);
                    lastNumber = 5;
                    break;
                case 'I':
                    decimal = processDecimal(1, lastNumber, decimal);
                    lastNumber = 1;
                    break;
            }
        }
        return decimal;
    }

    @Deprecated
    private static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
        if (lastNumber > decimal) {
            return lastDecimal - decimal;
        }
        return lastDecimal + decimal;
    }

    public static int[] splitIntoParts(int whole, int parts) {
        int[] arr = new int[parts];
        int remain = whole;
        int partsLeft = parts;
        for (int i = 0; partsLeft > 0; i++) {
            int size = (remain + partsLeft - 1) / partsLeft; // rounded up, aka ceiling
            arr[i] = size;
            remain -= size;
            partsLeft--;
        }
        return arr;
    }

    /**
     * коррекция числа в слово
     *
     * @param amount - Число
     * @param words  - Слова конвертации (указывать именно слово в количестве 3-х штук)
     *               1 - ноль, 2 - три, 3 - пять
     * @return - конвертированное слово
     */
    public static String getCorrectWord(long amount, String... words) {
        if (words.length < 2)
            return "words is empty!";
        long amount2 = amount % 100;
        if (amount2 % 11 == 0 ||
                amount % 10 >= 5 ||
                amount % 10 == 0)
            return words[0];
        else if (amount % 10 == 1)
            return words[1];
        else
            return words[2];
    }

    /**
     * коррекция числа в слово
     *
     * @param amount - Число
     * @param words  - Слова конвертации (указывать именно слово в количестве 3-х штук)
     *               1 - ноль, 2 - три, 3 - пять
     * @return - конвертированное слово
     */
    public static String getCorrectWord(double amount, String... words) {
        if (words.length < 2)
            return "words is empty!";
        double amount2 = amount % 100;
        if (amount2 % 11 == 0 ||
                amount % 10 >= 5 ||
                amount % 10 == 0)
            return words[0];
        else if (amount % 10 == 1)
            return words[1];
        else
            return words[2];
    }

    /**
     * коррекция числа в слово
     *
     * @param amount - Число
     * @param words  - Слова конвертации (указывать именно слово в количестве 3-х штук)
     *               1 - ноль, 2 - три, 3 - пять
     * @return - конвертированное слово
     */
    public static String getCorrectWord(int amount, String... words) {
        if (words.length < 2)
            return "words is empty!";
        int amount2 = amount % 100;
        if (amount2 % 11 == 0 ||
                amount % 10 >= 5 ||
                amount % 10 == 0)
            return words[0];
        else if (amount % 10 == 1)
            return words[1];
        else
            return words[2];
    }

    public static String getCorrectTenWords(int amount, String... words) {
        if (words.length < 10)
            return "words is empty!";

        int amount2 = amount % 100;
        if (amount2 % 11 == 0 ||
                amount % 10 >= 5 ||
                amount % 10 == 0)
            return words[0];
        else if (amount % 10 == 1)
            return words[1];
        else
            return words[2];
    }


    /**
     * @param value - число, которое будем конвертировать
     * @param words - слова, для конвертации. Необходимо указать от 0 до 9
     * @return конвертированное слово
     */
    public static String convert(final int value, String... words) {
        if (words.length < 9) {
            return "words is empty";
        }
        if (value < 0) {
            return convert(-value, words);
        }

        if (value < 20) {
            return words[value];
        }

        if (value < 100) {
            return words[value / 10];
        }

        if (value < 1000) {
            return words[value / 100];
        }

        if (value < 1000000) {
            return convert(value / 1000, words);
        }

        if (value < 1000000000) {
            return convert(value / 1000000, words);
        }

        return convert(value / 1000000000, words);
    }

    public static String convent(final int value, List<String> words) {
        return convert(value, words.toArray(new String[0]));
    }

    /**
     * @param value - число, которое будем конвертировать
     * @param words - слова, для конвертации. Необходимо указать от 0 до 9
     * @return конвертированное слово
     */
    public static String convert(final short value, String... words) {
        if (words.length < 9) {
            return "words is empty";
        }
        if (value < 0) {
            return convert(-value, words);
        }

        if (value < 20) {
            return words[value];
        }

        if (value < 100) {
            return words[value / 10];
        }

        if (value < 1000) {
            return words[value / 100];
        }

        if (value < 1000000) {
            return convert(value / 1000, words);
        }

        if (value < 1000000000) {
            return convert(value / 1000000, words);
        }

        return convert(value / 1000000000, words);
    }

    public static String convent(final short value, List<String> words) {
        return convert(value, words.toArray(new String[0]));
    }
}