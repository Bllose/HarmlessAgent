package org.bllose.tools;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import org.bllose.content.Constants;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadJar4Test {

    public static void main(String[] args) {
        String absJarPath = "D:\\etc\\.m2\\repository\\com\\dycjr\\xiakuan\\xk-basic\\1.0.0\\xk-basic-1.0.0.jar";
        String feignPath = "D:\\etc\\.m2\\repository\\org\\springframework\\cloud\\spring-cloud-openfeign-core\\3.1.8\\spring-cloud-openfeign-core-3.1.8.jar";
        try {
            File jarFile = new File(absJarPath); // 替换为你的jar文件路径
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            URL jarUrl = new File(absJarPath).toURI().toURL();
            URL feignUrl = new File(feignPath).toURI().toURL();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new LoaderClassPath(new java.net.URLClassLoader(new URL[]{jarUrl, feignUrl})));

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // .matches(".+Xk[^/]+Client")
                if (name.endsWith(".class") && name.endsWith("Client.class")) {
                    // 移除文件名中的.class和可能的'/'（取决于jar包内的结构）
                    try {
                        String className = name.substring(0, name.length() - 6).replace('/', '.');
                        CtClass cc = pool.get(className);
                        ClassFile cf = cc.getClassFile2();
                        ConstPool cp = cf.getConstPool();
                        FeignAnnotationUtil.putUrlInThisAnnotationOnClassFile(cf, cp, Constants.ANNOTATION_FEIGN);
                    }catch (NotFoundException e) {
                        pool.insertClassPath(new LoaderClassPath(new java.net.URLClassLoader(new URL[]{jarUrl})));
                    }
                }
            }

            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readClassFromJar(String jarFilePath, String className) throws IOException {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            // 注意：className需要是JAR包内的路径格式，例如"com/example/MyClass.class"
            JarEntry entry = jarFile.getJarEntry(className.replace('.', '/') + ".class");
            if (entry == null) {
                throw new IOException("Class not found in JAR: " + className);
            }

            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return byteArrayOutputStream.toByteArray();
            }
        }
    }
}
