package com.duskrainfall.betterminecart.tools;

import com.duskrainfall.betterminecart.BetterMinecart;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesEditor {
    public final static String saveTypeField = "savetype";
    private static String saveType;

    public static String getSaveType() {
        return saveType;
    }

    public static void initProperties(){
        Properties properties = new Properties();

        properties.setProperty(saveTypeField, "null");

        try (FileOutputStream outputStream = new FileOutputStream(BetterMinecart.PLUGIN_PATH + '/' + BetterMinecart.PLUGIN_PROPERTIES)) {
            properties.store(outputStream, """
                     For savetype
                    # if null, no saving
                    # if data, save as .dat
                    # if mysql, save as database
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
