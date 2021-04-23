package tools;

import vulpayload.Payload;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @auther Skay
 * @date 2021/4/20 15:05
 * @description
 */
public class AddPlugins {
    public static int scanClassX(URI uri, String packageName, Class<?> parentClass, Class<?> annotationClass, Map<String, Class<?>> destMap) {
        String jarFileString;
        int addNum = 0;

        try {
            File file = new File(uri);
            File[] file2 = file.listFiles();

            for(int i = 0; i < file2.length; ++i) {
                File objectFile = file2[i];
                if (objectFile.isDirectory()) {
                    File[] objectFiles = objectFile.listFiles();

                    for(int j = 0; j < objectFiles.length; ++j) {
                        File objectClassFile = objectFiles[j];
                        if (objectClassFile.getPath().endsWith(".class")) {
                            try {
                                String objectClassName = String.format("%s.%s.%s", packageName, objectFile.getName(), objectClassFile.getName().substring(0, objectClassFile.getName().length() - ".class".length()));
                                Class objectClass = Class.forName(objectClassName);
                                if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                                    Annotation annotation = objectClass.getAnnotation(annotationClass);
                                    String name = (String)annotation.annotationType().getMethod("Name").invoke(annotation, (Object[])null);
                                    destMap.put(name, objectClass);
                                    ++addNum;
                                }
                            } catch (Exception var19) {
                                var19.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception var20) {
            var20.printStackTrace();
        }

        return addNum;
    }

}
