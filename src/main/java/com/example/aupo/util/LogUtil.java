package com.example.aupo.util;

import lombok.experimental.UtilityClass;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@UtilityClass
public class LogUtil {

    private final String filename = "logs.txt";

    private void writeToFile(String s) {
        try (FileWriter fw = new FileWriter(filename, true)){
            fw.write(s + "\n");
        }
        catch (IOException e){
            System.out.println("Не удалось сохранить логи в файл");
        }
    }

    private void log(String level, String message){
        message = String.format("%s (%s): %s", level, LocalDateTime.now(), message);
        System.out.println(message);
        writeToFile(message);
    }

    public void info(String message){
        log("INFO", message);
    }

    public void warning(String message){
        log("WARNING", message);
    }

    public void error(String message){
        log("ERROR", message);
    }

}
