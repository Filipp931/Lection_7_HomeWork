package org.example.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            byte[] decryptedBinaryData = getDecryptedBinaryClassData(classFilePath);
            clazz = super.defineClass(null,decryptedBinaryData,0,decryptedBinaryData.length);
        } catch (Exception e){
            e.printStackTrace();
        }
        return clazz;
    }


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
    private byte[] getDecryptedBinaryClassData (String classFilePath) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(classFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = decrypt(data[i]);
        }
        return data;
    }
    private byte decrypt(byte b) {
        byte count = (byte) key.getBytes().length;
            b -= count;
        return b;
    }
    public static byte crypt(byte b, String key){
        byte count = (byte) key.getBytes().length;
        b += count;
        return b;
    }

}
