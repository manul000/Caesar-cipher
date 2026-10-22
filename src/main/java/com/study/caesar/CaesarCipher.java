package com.study.caesar;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class CaesarCipher {
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789" + " .,!?;:-\n\r" + "\t\"'()";

    public static String shift(String text, int key) {
        StringBuilder out = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            int i = ALPHABET.indexOf(c);
            out.append(i < 0 ? c : ALPHABET.charAt(Math.floorMod(i + Math.floorMod(key, ALPHABET.length()), ALPHABET.length())));
        }
        return out.toString();
    }

    public void transform(Path input, Path output, int key) throws IOException {
        validate(input, output);
        try (Reader r = Files.newBufferedReader(input, StandardCharsets.UTF_8); Writer w = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            char[] buffer = new char[8192];
            int count;
            while ((count = r.read(buffer)) != -1) w.write(shift(new String(buffer, 0, count), key));
        }
    }

    private void validate(Path in, Path out) throws IOException {
        if (!Files.isRegularFile(in)) throw new IOException("Исходный файл не найден: " + in);
        if (in.toAbsolutePath().normalize().equals(out.toAbsolutePath().normalize()) || (Files.exists(out) && Files.isSameFile(in, out)))
            throw new IOException("Результат должен быть другим файлом");
        Path parent = out.toAbsolutePath().getParent();
        if (!Files.isDirectory(parent)) throw new IOException("Папка результата не существует");
    }

    public int bruteForce(Path input, Path output) throws IOException {
        String sample = sample(input);
        double best = -Double.MAX_VALUE;
        int key = 0;
        String common = " о е а и н т с р в л к м д п у я ы ь г з б ч й х ж ш ю ц щ э ф ё ";
        for (int k = 0; k < ALPHABET.length(); k++) {
            String candidate = shift(sample, -k).toLowerCase(Locale.ROOT);
            double score = 0;
            for (char c : candidate.toCharArray()) {
                if (c == ' ') score += 3;
                else if (common.indexOf(c) >= 0) score += 1;
                else if (c < 32 && c != '\n' && c != '\r' && c != '\t') score -= 5;
            }
            for (String word : List.of(" и ", " в ", " на ", " не ", "что", "это", " the ", " and ", " to "))
                score += occurrences(candidate, word) * 15;
            if (score > best) {
                best = score;
                key = k;
            }
        }
        transform(input, output, -key);
        return key;
    }

    public int statistical(Path input, Path reference, Path output) throws IOException {
        double[] expected = frequencies(sample(reference));
        String encrypted = sample(input);
        double best = Double.MAX_VALUE;
        int key = 0;
        for (int k = 0; k < ALPHABET.length(); k++) {
            double[] actual = frequencies(shift(encrypted, -k));
            double score = 0;
            for (int i = 0; i < expected.length; i++)
                score += Math.pow(actual[i] - expected[i], 2) / (expected[i] + 0.0001);
            if (score < best) {
                best = score;
                key = k;
            }
        }
        transform(input, output, -key);
        return key;
    }

    private static int occurrences(String s, String word) {
        int n = 0, p = 0;
        while ((p = s.indexOf(word, p)) >= 0) {
            n++;
            p += word.length();
        }
        return n;
    }

    private static double[] frequencies(String s) {
        double[] f = new double[ALPHABET.length()];
        int count = 0;
        for (char c : s.toCharArray()) {
            int i = ALPHABET.indexOf(c);
            if (i >= 0) {
                f[i]++;
                count++;
            }
        }
        if (count == 0) throw new IllegalArgumentException("Нет символов выбранного алфавита");
        for (int i = 0; i < f.length; i++) f[i] /= count;
        return f;
    }

    private static String sample(Path p) throws IOException {
        try (Reader r = Files.newBufferedReader(p)) {
            char[] b = new char[100_000];
            int total = 0, n;
            while (total < b.length && (n = r.read(b, total, b.length - total)) > 0) total += n;
            if (total == 0) throw new IOException("Файл пуст");
            return new String(b, 0, total);
        }
    }
}
