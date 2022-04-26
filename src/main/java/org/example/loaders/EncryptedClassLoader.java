package org.example.loaders;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.binary.Base16;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        Class<?> clazz = null;
        try {
            String classFilePath = findClassFilePath(dir, name);
            byte[] decryptedBinaryData = getBinaryClassData(classFilePath);
            clazz = super.defineClass(null,decryptedBinaryData,0,decryptedBinaryData.length);
        } catch (Exception e){
            e.printStackTrace();
        }
        return clazz;
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
                .filter(file -> file.endsWith(pluginClassName + ".class"))
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
    public static void encrypt(Path file, Path cryptFile, String key) throws IOException {
        byte[] content = Files.readAllBytes(file);
        //TODO
        if(Files.exists(cryptFile)){
            Files.delete(cryptFile);
        }
        Files.write(cryptFile,content);
    }

}
