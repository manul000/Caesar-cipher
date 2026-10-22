package com.study.caesar;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class CipherTest {
  @TempDir Path dir;

  @Test
  void roundTripLargeFile() throws Exception {
    var c = new CaesarCipher();
    var in = dir.resolve("in");
    var encrypted = dir.resolve("enc");
    var out = dir.resolve("out");
    String text = "Это большой текст. Привет, мир! Hello 🙂\n".repeat(100_000);
    Files.writeString(in, text);
    c.transform(in, encrypted, 37);
    c.transform(encrypted, out, -37);
    assertEquals(text, Files.readString(out));
  }

  @Test
  void rejectsSameFile() throws Exception {
    var p = dir.resolve("a");
    Files.writeString(p, "a");
    assertThrows(Exception.class, () -> new CaesarCipher().transform(p, p, 1));
  }

  @Test
  void statisticalFindsShift() throws Exception {
    var c = new CaesarCipher();
    var in = dir.resolve("in");
    var enc = dir.resolve("enc");
    var out = dir.resolve("out");
    Files.writeString(
        in, "Это обычный русский текст, в котором есть слова и пробелы. ".repeat(200));
    c.transform(in, enc, 17);
    assertEquals(17, c.statistical(enc, in, out));
    assertEquals(Files.readString(in), Files.readString(out));
  }

  @Test
  void hugeKeysNormalize() {
    assertEquals(
        CaesarCipher.shift("Тест", 1),
        CaesarCipher.shift("Тест", 1 + CaesarCipher.ALPHABET.length()));
  }
}
