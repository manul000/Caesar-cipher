# Шифр Цезаря

Требуется JDK 22; `JAVA_HOME` должен указывать на него. Maven загрузится автоматически. Команды выполняются из папки проекта.

Установка зависимостей и сборка:
```sh
.\mvnw.cmd package
```

Запуск тестов:
```sh
.\mvnw.cmd test
```

Запуск консольного меню:
```sh
.\mvnw.cmd exec:java
```
Шифрование, расшифровка и подбор ключа:
```sh
.\mvnw.cmd exec:java "-Dexec.args=encrypt examples/input.txt encrypted.txt 17"
.\mvnw.cmd exec:java "-Dexec.args=decrypt encrypted.txt decoded.txt 17"
.\mvnw.cmd exec:java "-Dexec.args=brute encrypted.txt guessed.txt"
.\mvnw.cmd exec:java "-Dexec.args=stats encrypted.txt statistical.txt examples/input.txt"
```
Запуск графического интерфейса:
```sh
.\mvnw.cmd exec:java "-Dexec.args=--gui"
```
