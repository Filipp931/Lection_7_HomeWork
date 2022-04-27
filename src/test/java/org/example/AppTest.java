package org.example;

import org.example.loaders.exceptions.ClassIsNotPluginException;
import org.example.loaders.EncryptedClassLoader;
import org.example.loaders.exceptions.NoClassException;
import org.junit.After;
import org.junit.Test;
import pluginRootDirectory.HelloPlugin.HelloPlugin;
import pluginRootDirectory.Plugin;

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
    public void PluginManagerTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException, NoSuchElementException, NoClassException, ClassIsNotPluginException {
        PluginManager pluginManager = new PluginManager("target\\classes\\pluginRootDirectory");
        Plugin test = pluginManager.load("HelloPlugin", "HelloPlugin");
        Plugin test2 = pluginManager.load("HelloPlugin", "HelloPlugin");
        Plugin test3 = pluginManager.load("RandomPlugin", "RandomPlugin");
        Plugin test4 = pluginManager.load("RandomPlugin", "RandomPlugin");
        assertEquals(test.getClass().getInterfaces()[0], Plugin.class);
    }

    /**
     * проверка на загрузку несуществующего класса
     */
    @Test(expected = NoClassException.class)
    public void PluginManagerIncorrectPluginTest() throws NoSuchMethodException, InstantiationException, NoClassException, IllegalAccessException, InvocationTargetException, IOException, ClassIsNotPluginException {
        PluginManager pluginManager = new PluginManager("target\\classes\\pluginRootDirectory");
        Plugin test6 = pluginManager.load("NotP11111lugin", "NotPlug11111in");
    }

    /**
     * Проверка на загрузку класса, не являющегося плагином
     */
    @Test(expected = ClassIsNotPluginException.class)
    public void PluginManagerNotPluginClassTest() throws NoSuchMethodException, InstantiationException, NoClassException, IllegalAccessException, InvocationTargetException, IOException, ClassIsNotPluginException {
        PluginManager pluginManager = new PluginManager("target\\classes\\pluginRootDirectory");
        Plugin test5 = pluginManager.load("NotPlugin", "NotPlugin");
    }

    /**
     * проверка EncryptedClassLoader на корректное кодирование/декодирование класса
     * @throws IOException
     */
    @Test
    public void cryptTest() throws IOException {
        Plugin test = new HelloPlugin();
        Files.createFile(Path.of("target\\classes\\pluginRootDirectory\\HelloCryptPlugin.class"));
        Path pathToOrigin = Paths.get("target\\classes\\pluginRootDirectory\\HelloPlugin\\HelloPlugin.class");
        Path pathToCryptFile = Paths.get("target\\classes\\pluginRootDirectory\\HelloCryptPlugin.class");
        EncryptedClassLoader.encrypt(pathToOrigin,pathToCryptFile,"key"); //кодирование и запись в файл
        EncryptedClassLoader encryptedClassLoader = new EncryptedClassLoader("key",
                new File("target\\classes\\pluginRootDirectory"), ClassLoader.getSystemClassLoader() );
        Class clazz = encryptedClassLoader.findClass("HelloCryptPlugin"); //декодирование
        assertTrue(clazz.getName() == test.getClass().getName());
    }
    @After
    public void deleteTempFile() throws IOException {
        if (Files.exists(Path.of("target\\classes\\pluginRootDirectory\\HelloPlugin\\HelloPlugin.class"))) {
            Files.delete(Path.of("target\\classes\\pluginRootDirectory\\HelloPlugin\\HelloPlugin.class"));
        }
    }
}
