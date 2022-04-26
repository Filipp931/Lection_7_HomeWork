package org.example;

import org.example.loaders.MyClassLoader;
import pluginRootDirectory.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PluginManager {
    private final String pluginRootDirectory;

    public PluginManager(String pluginRootDirectory) {
        this.pluginRootDirectory = pluginRootDirectory;
    }

    /**
     * Загружаем класс и возвращаем его instance если он implements Plugin
     * @param pluginName - имя плагина
     * @param pluginClassName - класс плагина
     * @return Plugin
     */
    public Plugin load(String pluginName, String pluginClassName) {
        Class clazz = null;
        File currentPluginDirectory = new File(pluginRootDirectory + "\\" + pluginName);
        try {
            String classFilePath = findClassFilePath(currentPluginDirectory, pluginClassName);
            byte[] binaryClassData = getBinaryClassData(classFilePath);
            MyClassLoader myClassLoader = new MyClassLoader();
            clazz = myClassLoader.load(binaryClassData);
        } catch (FileNotFoundException e) {
            System.err.println("Plugin " + pluginClassName+" not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Plugin plugin = null;
        if((Arrays.stream(clazz.getInterfaces()).findAny().get() == Plugin.class)){
            try {
                plugin = (Plugin) clazz.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plugin;
    }

    /**
     * Поиск файла pluginClassName в директории
     * @param directory
     * @param pluginClassName - имя файла
     * @return String путь к файлу
     * @throws FileNotFoundException если файла .class нет в директории
     */
    private String findClassFilePath(File directory, String pluginClassName) throws IOException {
        Path path = Files.walk(Paths.get(directory.getAbsolutePath()))
                .filter(file -> Files.isRegularFile(file) && file.endsWith(pluginClassName + ".class"))
                .findFirst()
                .get();
        return String.valueOf(path);
    }
    /**
     * Получение бинарного массива данных из файла по его пути
     * @param classFilePath - путь к файлу
     * @return - массив byte
     */
    private byte[] getBinaryClassData (String classFilePath) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(classFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
