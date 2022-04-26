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

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void PluginManagerTest()
    {
        PluginManager pluginManager = new PluginManager("C:\\Users\\admin\\Desktop\\HW7\\target\\classes\\pluginRootDirectory");
        Plugin test = pluginManager.load("HelloPlugin", "HelloPlugin.class");
        Plugin test2 = pluginManager.load("HelloPlugin", "HelloPlugin.class");
        assertEquals(test.getClass().getInterfaces()[0], Plugin.class);
    }

    @After
    public void deleteCash() throws IOException {
        Files.delete(Path.of("temp.txt"));
    }
    @Test
    public void cryptTest() throws IOException, ClassNotFoundException {
        Plugin test = new HelloPlugin();
        Files.createFile(Path.of("temp.txt"));
        Path pathToOrigin = Paths.get("C:\\Users\\admin\\Desktop\\HW7\\target\\classes\\pluginRootDirectory\\HelloPlugin\\HelloPlugin.class");
        Path pathToCryptFile = Paths.get("temp.txt");
        byte[] origin = Files.readAllBytes(pathToOrigin);
        try(BufferedWriter bw = Files.newBufferedWriter(pathToCryptFile)){
            for (byte b:origin) {
                bw.write(EncryptedClassLoader.crypt(b,"key"));
            }
        }
        EncryptedClassLoader encryptedClassLoader = new EncryptedClassLoader("key",
                new File("C:\\Users\\admin\\Desktop\\HW7\\target\\classes\\pluginRootDirectory"), ClassLoader.getSystemClassLoader() );
        Class clazz = encryptedClassLoader.findClass("HelloPlugin");
        assertTrue(clazz.getInterfaces()[0] == test.getClass().getInterfaces()[0]);
    }
}
