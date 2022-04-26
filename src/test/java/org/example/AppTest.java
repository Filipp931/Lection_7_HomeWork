package org.example;

import org.example.loaders.EncryptedClassLoader;
import org.example.loaders.MyClassLoader;
import org.junit.After;
import org.junit.Test;
import pluginRootDirectory.HelloPlugin.HelloPlugin;
import pluginRootDirectory.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    /**
     * Проверка pluginManager на загрузку классов
     */
    @Test
    public void PluginManagerTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException, NoSuchElementException {
        PluginManager pluginManager = new PluginManager("target\\classes\\pluginRootDirectory");
        Plugin test = pluginManager.load("HelloPlugin", "HelloPlugin");
        Plugin test2 = pluginManager.load("HelloPlugin", "HelloPlugin");
        Plugin test3 = pluginManager.load("RandomPlugin", "RandomPlugin");
        Plugin test4 = pluginManager.load("RandomPlugin", "RandomPlugin");
        Plugin test5 = pluginManager.load("NotPlugin", "NotPlugin");
        assertEquals(test.getClass().getInterfaces()[0], Plugin.class);
    }

    @After
    public void deleteCash() throws IOException {
        if (Files.exists(Path.of("temp.txt"))) {
            Files.delete(Path.of("temp.txt"));
        }
    }
    @Test
    public void cryptTest() throws IOException {
        Plugin test = new HelloPlugin();
        Files.createFile(Path.of("temp.txt"));
        Path pathToOrigin = Paths.get("C:\\Users\\Fil\\Desktop\\JavaSber\\Lection_7_HomeWork\\Lection_7_HomeWork\\target\\classes\\pluginRootDirectory\\HelloPlugin\\HelloPlugin.class");
        Path pathToCryptFile = Paths.get("temp.txt");
        EncryptedClassLoader.encrypt(pathToOrigin,pathToCryptFile,"key");
        EncryptedClassLoader encryptedClassLoader = new EncryptedClassLoader("key",
                new File("target\\classes\\pluginRootDirectory"), ClassLoader.getSystemClassLoader() );
        Class clazz = encryptedClassLoader.findClass("HelloPlugin");
        assertTrue(clazz.getInterfaces()[0] == test.getClass().getInterfaces()[0]);
    }
}
