package com.github.kayjamlang.executor.cli;

import com.github.kayjamlang.executor.Executor;
import com.github.kayjamlang.executor.libs.main.MainLibrary;
import com.github.kayjamlang.executor.libs.Library;
import org.apache.commons.cli.*;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option file = new Option("f", "file", true, "KayJam file path");
        file.setRequired(true);
        options.addOption(file);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("kayjam-cli", options);

            System.exit(1);
            return;
        }

        Executor executor = new Executor();
        executor.addLibrary(new MainLibrary());
        File libDir = new File(Paths.get(System.getProperty("user.dir"),
                "./libs/").normalize().toString());
        if(libDir.exists()||libDir.createNewFile()){
            File[] files = libDir.listFiles();
            if(files!=null)
                for(File libJar: files){
                    if(libJar.getName().endsWith(".jar"))
                        loadJarLibrary(executor, libJar);
                }
        }

        File executeFile = new File(Paths.get(System.getProperty("pathFile"),
                cmd.getOptionValue("file")).normalize().toString());
        executor.execute("{"+read(executeFile)+"}");
    }

    private static void loadJarLibrary(Executor executor, File file) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + file.getPath()+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }

            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class<?> c = cl.loadClass(className);
            if(Library.class.isAssignableFrom(c))
                executor.addLibrary((Library) c.newInstance());
        }
    }

    private static String read(File file) throws IOException {
        Scanner reader = new Scanner(file, UniversalDetector.detectCharset(file));

        StringBuilder value = new StringBuilder();
        while (reader.hasNextLine()) {
            value.append(reader.nextLine()).append("\n");
        }
        reader.close();


        //Filter
        value = new StringBuilder(value.toString().replaceAll("\r", "")
                .replaceAll("\t", ""));
        if (value.toString().startsWith("\uFEFF")) {
            value = new StringBuilder(value.substring(1));
        }

        return value.toString();
    }
}
