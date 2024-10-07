package br.edu.fatecsaocaetano.hystera.navegabilidade;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class LoggerUtils {
    public static Logger configurarLogger(String className) {
        Logger logger = Logger.getLogger(className);
        try {
            String logFilePath = "..\\hysteraapp.log";
            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao configurar o FileHandler", e);
        }
        return logger;
    }
}
