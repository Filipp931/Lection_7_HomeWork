package org.example.loaders;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.binary.Base16;
import org.example.loaders.exceptions.NoClassException;
import org.w3c.dom.ls.LSOutput;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EncryptedClassLoader extends ClassLoader{
    private final String key;
    private final File dir;

    public EncryptedClassLoader(String key, File dir, ClassLoader parent) {
        super(parent);
        this.key = key;
        this.dir = dir;
    }

    @Override
    public Class<?> findClass(String name) {
        String classFilePath;
        try {
            classFilePath = findClassFilePath(dir, name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        byte[] decryptedBinaryData;
        decryptedBinaryData = getBinaryClassData(classFilePath);
        Class<?> clazz = super.defineClass(null,decryptedBinaryData,0,decryptedBinaryData.length);
        return clazz;
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
        return decrypt(data);
    }
    private byte[] decrypt(byte[] crypt) {
        byte[] byteKey = key.getBytes(); //-128 до 127
        byte[] result = new byte[crypt.length - byteKey.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = crypt[i];
        }
        return result;
    }
    public static void encrypt(Path file, Path cryptFile, String key) throws IOException {
        byte[] content = Files.readAllBytes(file);
        byte[] byteKey = key.getBytes(); //-128 до 127
        byte[] result = new byte[content.length + byteKey.length];
        for (int i = 0; i < content.length; i++) {
            result[i] = content[i];
        }
        for (int i = 0; i < byteKey.length ; i++) {
            int current = content.length + i;
            result[current] = byteKey[i];
        }
        if(Files.exists(cryptFile)){
            Files.delete(cryptFile);
        }
        Files.write(cryptFile, result);
    }

}
