package org.example;

import org.example.loaders.exceptions.ClassIsNotPluginException;
import org.example.loaders.MyClassLoader;
import org.example.loaders.exceptions.NoClassException;
import pluginRootDirectory.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PluginManager {
    private final String pluginRootDirectory;
    private HashMap<String, Class<?>> cache = new HashMap<>();
    private final MyClassLoader myClassLoader = new MyClassLoader();
    public PluginManager(String pluginRootDirectory) {
        this.pluginRootDirectory = pluginRootDirectory;
    }

    /**
     * Загружаем класс и возвращаем его instance если он implements Plugin
     * @param pluginName - имя плагина
     * @param pluginClassName - класс плагина
     * @return Plugin
     */
    public Plugin load(String pluginName, String pluginClassName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, NoClassException, ClassIsNotPluginException {
        if(cache.containsKey(pluginName)){
            return loadPluginFromCache(pluginName);
        } else {
            return loadPluginFromFileAndCache(pluginName, pluginClassName);
        }
    }

    /**
     * Получение Class из кэша по его имени
     * @param pluginName
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Plugin loadPluginFromCache(String pluginName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NullPointerException {
        Class clazz = cache.get(pluginName);
        System.out.println("===== class " + clazz.getSimpleName() + " loaded from cache  ========");
        return  (Plugin) clazz.getConstructor().newInstance();
    }

    /**
     * считывание класса из файла, добавление в кэш и получение Instance
     * @param pluginName - имя класса
     * @param pluginClassName - класс
     * @return - Plugin new instance
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private Plugin loadPluginFromFileAndCache(String pluginName, String pluginClassName) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchElementException, NoClassException, ClassIsNotPluginException {
        String classFilePath = findClassFilePath(new File(pluginRootDirectory), pluginClassName);
        byte[] binaryClassData = getBinaryClassData(classFilePath);
        Class clazz = myClassLoader.load(binaryClassData);
        Plugin plugin = null;
        if((Arrays.asList(clazz.getInterfaces()).contains(Plugin.class))) {
            cache.put(pluginName, clazz);
            System.out.println("===== class " + clazz.getSimpleName() + " loaded from file and added to the cache  ========");
            plugin = (Plugin) clazz.getConstructor().newInstance();
        } else {
            throw new ClassIsNotPluginException();
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
    private String findClassFilePath(File directory, String pluginClassName) throws IOException, NoClassException {
        List<Path> files = Files.walk(Paths.get(directory.getAbsolutePath()))
                .filter(file -> file.endsWith(pluginClassName + ".class"))
                .collect(Collectors.toList());
        if(files.isEmpty()) {
            throw new NoClassException();
        }
        return String.valueOf(files.get(0));
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
