package com.study.caesar;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--gui")) {
            SwingUtilities.invokeLater(Main::gui);
            return;
        }
        if (args.length == 0) {
            try (Scanner s = new Scanner(System.in)) {
                System.out.println("Режим: encrypt / decrypt / brute / stats");
                String mode = s.nextLine();
                System.out.println("Исходный файл:");
                String in = s.nextLine();
                System.out.println("Файл результата:");
                String out = s.nextLine();
                System.out.println("Ключ (для stats — путь образца; для brute — 0):");
                run(mode, in, out, s.nextLine());
            }
        } else if (args.length >= 3) run(args[0], args[1], args[2], args.length > 3 ? args[3] : "0");
        else System.err.println("encrypt|decrypt|brute|stats input.txt output.txt [key|reference.txt]");
    }

    static void run(String mode, String in, String out, String value) {
        try {
            CaesarCipher cipher = new CaesarCipher();
            Path a = Path.of(in), b = Path.of(out);
            switch (mode) {
                case "encrypt" -> cipher.transform(a, b, Integer.parseInt(value));
                case "decrypt" ->
                        cipher.transform(a, b, -Math.floorMod(Integer.parseInt(value), CaesarCipher.ALPHABET.length()));
                case "brute" -> System.out.println("Подобран ключ: " + cipher.bruteForce(a, b));
                case "stats" -> System.out.println("Подобран ключ: " + cipher.statistical(a, Path.of(value), b));
                default -> throw new IllegalArgumentException("Неизвестный режим");
            }
            System.out.println("Готово: " + b);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка: " + e.getMessage(), e);
        }
    }

    static void gui() {
        JFrame frame = new JFrame("Шифр Цезаря");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        JComboBox<String> mode = new JComboBox<>(new String[]{"encrypt", "decrypt", "brute", "stats"});
        JTextField in = new JTextField(), out = new JTextField(), value = new JTextField("3");
        p.add(new JLabel("Режим"));
        p.add(mode);
        p.add(new JLabel("Исходный файл"));
        p.add(in);
        p.add(new JLabel("Файл результата"));
        p.add(out);
        p.add(new JLabel("Ключ / файл образца"));
        p.add(value);
        JButton go = new JButton("Выполнить");
        p.add(go);
        JLabel status = new JLabel("UTF-8");
        p.add(status);
        go.addActionListener(e -> {
            go.setEnabled(false);
            new SwingWorker<Void, Void>() {
                protected Void doInBackground() {
                    Main.run((String) mode.getSelectedItem(), in.getText(), out.getText(), value.getText());
                    return null;
                }

                protected void done() {
                    go.setEnabled(true);
                    try {
                        get();
                        status.setText("Готово");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getCause().getMessage());
                    }
                }
            }.execute();
        });
        frame.add(p);
        frame.setSize(650, 240);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
