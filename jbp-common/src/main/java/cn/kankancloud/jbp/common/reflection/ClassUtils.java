package cn.kankancloud.jbp.common.reflection;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

    public static final String CLASS_SUFFIX = ".class";

    private ClassUtils() {
    }

    /**
     * 从包package中获取所有的Class FQN
     *
     * @param packageName package name
     * @param recursive   是否递归查找
     * @return 类名集合
     */
    public static Set<String> getClassNames(String packageName, boolean recursive, String... exclusions) throws IOException {

        Set<String> classes = new LinkedHashSet<>();

        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');

        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;


        dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol(); // 得到协议的名称
            if ("file".equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件 并添加到集合中
                findClassesInPackageByFile(packageName, filePath, recursive, classes, exclusions);
            } else if ("jar".equals(protocol)) {
                JarFile jar;
                jar = ((JarURLConnection) url.openConnection()).getJarFile();
                // 从此jar包 得到一个枚举类
                Enumeration<JarEntry> entries = jar.entries();
                findClassesInPackageByJar(packageName, entries, packageDirName, recursive, classes, exclusions);
            }
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    private static void findClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<String> classes, String... exclusions) {
        if (shouldExcluded(exclusions, packageName)) {
            return;
        }

        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] files = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(CLASS_SUFFIX)));

        if (files == null) {
            return;
        }

        // 循环所有文件
        for (File file : files) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                classes.add(packageName + '.' + className);
            }
        }
    }

    /**
     * 以jar的形式来获取包下的所有Class=
     */
    private static void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries, String packageDirName, final boolean recursive, Set<String> classes, String... exclusions) {
        if (shouldExcluded(exclusions, packageName)) {
            return;
        }

        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }

            // 如果前半部分和定义的包名相同
            if (!name.startsWith(packageDirName)) {
                return;
            }

            int idx = name.lastIndexOf('/');
            // 如果以"/"结尾 是一个包
            if (idx != -1) {
                // 获取包名 把"/"替换成"."
                packageName = name.substring(0, idx).replace('/', '.');
            }

            // 如果可以迭代下去 并且是一个包
            if ((idx != -1) || recursive) {

                // 如果是一个.class文件 而且不是目录
                if (!name.endsWith(CLASS_SUFFIX) || entry.isDirectory()) {
                    continue;
                }

                // 去掉后面的".class" 获取真正的类名
                String className = name.substring(packageName.length() + 1, name.length() - 6);
                classes.add(packageName + '.' + className);
            }
        }
    }

    private static boolean shouldExcluded(String[] exclusions, String packageName) {
        if (exclusions == null || exclusions.length == 0) {
            return false;
        }

        for (String exclusion : exclusions) {
            if (packageName.startsWith(exclusion)) {
                return true;
            }
        }

        return false;
    }

}
