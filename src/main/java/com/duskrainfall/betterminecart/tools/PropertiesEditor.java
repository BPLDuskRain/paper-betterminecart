package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.BetterMinecart;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesEditor {
    public final static String saveTypeField = "savetype";
    private static String saveType;

    public final static String monitorField = "monitor";
    private static String monitor;

    public static String getSaveType() {
        return saveType;
    }
    public static String getMonitor() {
        return monitor;
    }

    public static void initProperties(){
        Properties properties = new Properties();

        properties.setProperty(saveTypeField, "data");
        properties.setProperty(monitorField, "close");

        try (FileOutputStream outputStream = new FileOutputStream(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_PROPERTIES)) {
            properties.store(outputStream, """
                     For savetype
                    # if null, no saving
                    # if data, save as .dat
                    # if mysql, save as database
                    
                    # For monitor
                    # if open, open the monitor
                    # if close, close the monitor
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void read(){
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_PROPERTIES)) {

            properties.load(inputStream);
            saveType = properties.getProperty(saveTypeField);
            monitor = properties.getProperty(monitorField);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
