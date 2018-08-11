package com.ertanayanlar.erserver.util;

import com.ertanayanlar.erserver.core.model.Command;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class AutoCommandInitializer {
    private AutoCommandInitializer() { }

    private static Set<Class<?>> getCommandClasses(File jarFile, String classPath) {
        Set<Class<?>> classes = new HashSet<>();

        try (JarFile file = new JarFile(jarFile)) {
            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");

                if (name.startsWith(classPath) && name.endsWith(".class"))
                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
            }
        } catch (Exception e) {
            System.out.println("[System.OUT] Exception while getting command classes !");
        }

        return classes;
    }

    public static void initializeCommands(String classPath) {
        for (Class command : getCommandClasses(new File(Paths.get(UtilityFunctions.getServerPath()).toAbsolutePath().normalize().toString()), classPath)) {
            try {
                Class<?> commandClass = Class.forName(command.getName());
                Constructor<?> constructor = commandClass.getConstructor();
                Command commandInstance = (Command) constructor.newInstance();

                if (commandInstance.isActive()) {
                    System.out.println("[AutoCommandInitializer] Class " + commandClass.getName() + " has been initialized !");
                }
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                System.out.println("[System.OUT] Exception while auto initializing commands !");
            }
        }
    }

    /*
        public static void initializeCommands(String classPath) {
        for (Class command : getCommandClasses(new File(Paths.get(UtilityFunctions.getServerPath()).toAbsolutePath().normalize().toString()), classPath)) {
            try {
                Class<?> commandClass = Class.forName(command.getName());
                Constructor<?> constructor = commandClass.getConstructor();
                Command commandInstance = (Command) constructor.newInstance();

                if (commandInstance.isActive()) {
                    System.out.println("[AutoCommandInitializer] Class " + commandClass.getName() + " has been initialized !");
                }
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                System.out.println("[System.OUT] Exception while auto initializing commands !");
            }
        }
    }
    */
}
