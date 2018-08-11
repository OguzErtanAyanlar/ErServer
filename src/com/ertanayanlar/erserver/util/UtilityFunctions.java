package com.ertanayanlar.erserver.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

public final class UtilityFunctions {
    private UtilityFunctions() {
    }

    public static String getRunningProcesses() throws IOException { // TODO
        String line;
        String allProcesses = "";

        Process taskList = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

        BufferedReader input = new BufferedReader(new InputStreamReader(taskList.getInputStream()));

        while ((line = input.readLine()) != null) {
            allProcesses += line + "\n";
        }

        input.close();

        return allProcesses;
    }

    public static String getServerPath() {
        String serverPath;

        try {
            serverPath = Class.forName("com.ertanayanlar.erserver.core.model.Server").getProtectionDomain().getCodeSource().getLocation().toURI().toString();
            serverPath = serverPath.substring(6).replace("%20", " ");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "URISyntaxException !";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Class name not found !";
        }

        return serverPath;
    }

    public static String getServerFileName() {
        String appNameWithDirectory;

        try {
            appNameWithDirectory = Class.forName("com.ertanayanlar.erserver.core.model.Server").getProtectionDomain().getCodeSource().getLocation().toURI().toString().split("/")[0];
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "Class name not found !";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Class name not found !";
        }

        return appNameWithDirectory;
    }

    public static String getServerDirectory() {
        String serverDirectory;

        try {
            serverDirectory = Class.forName("com.ertanayanlar.erserver.core.model.Server").getProtectionDomain().getCodeSource().getLocation().toURI().toString();
            serverDirectory = serverDirectory.substring(6, serverDirectory.length() - getServerFileName().length());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "Class name not found !";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Class name not found !";
        }

        return serverDirectory;
    }

    public static boolean isProcessRunning(String processName) throws IOException { // TODO
        String allProcesses = getRunningProcesses();

        if (processName.endsWith(".jar")) {
            if (allProcesses.contains("java.exe") || allProcesses.contains("javaw.exe")) {
                return true;
            }
        }

        return allProcesses.contains(processName);
    }

    // Will download the file, save it to the java temp directory,  with the option to run
    public static boolean downloadFile(String url, boolean execute, String savePath) {
        System.setProperty("http.agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

        String[] splitURL = url.split("/");
        String fileNameWithExtension = splitURL[splitURL.length - 1];

        String localPath;

        if (savePath == null || savePath.isEmpty()) {
            localPath = System.getProperty("java.io.tmpdir") + fileNameWithExtension;
        } else {
            localPath = savePath + fileNameWithExtension;
        }

        try (BufferedInputStream bis = new BufferedInputStream((new URL(url)).openStream());
             FileOutputStream fos = new FileOutputStream(new File(localPath));
             BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);) {
            byte[] arrayOfByte = new byte[1024];
            int i = 0;

            while ((i = bis.read(arrayOfByte, 0, 1024)) >= 0) {
                bos.write(arrayOfByte, 0, i);
            }

            if (execute) {
                if (executeFile(localPath)) {
                    System.out.println("Executing " + localPath);
                    return true;
                }
            }

            return true;
        } catch (IOException e) {
            System.out.println("[System.OUT] Could not download the file !");
        }

        return false;
    }

    public static boolean executeFile(String location) {
        if (System.getProperty("os.name").contains("Windows")) {
            try {
                String command = "cmd /c start " + location;
                Runtime.getRuntime().exec(command);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Runtime.getRuntime().exec(location);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Will auto restart, and move the nev file to startup directory.
    public static boolean updateServer(String url) {
        if (downloadFile(url, true, "")) { // startUpDirectory
            System.out.println("[System.OUT] Updating the client !");

            return true;
        }
        return false;
    }
}
