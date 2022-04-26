package org.example.loaders;

import java.io.IOException;

public class MyClassLoader extends ClassLoader{
    public synchronized Class<?> load (byte[] binaryClassData) throws IOException {
        Class<?> clazz = defineClass(null, binaryClassData, 0, binaryClassData.length);
        return clazz;
    }
}
