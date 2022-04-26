package org.example.loaders;

import pluginRootDirectory.Plugin;

import java.io.IOException;
import java.util.HashMap;

public class MyClassLoader extends ClassLoader{
    private HashMap<String, Class<?>> cache = new HashMap<>();


    public synchronized Class<?> load (byte[] binaryClassData) throws IOException {
        Class<?> clazz = defineClass(null, binaryClassData, 0, binaryClassData.length);
        cache.put(clazz.getName(), clazz);
        System.out.println("===== class " + clazz.getName() + " loaded in cache  ========");
        return clazz;
    }
}
