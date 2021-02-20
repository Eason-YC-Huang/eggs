package com.github.hexffff0.eggs.utils;

public class SystemInfo {

    public static final String CLASS_PATH_DELIMITER;

    public static final String OS;

    static {
        String osInfo = System.getProperty("os.name", "unknown").toLowerCase();
        if (osInfo.contains("windows")) {
            OS = "windows";
            CLASS_PATH_DELIMITER = ";";
        } else if (osInfo.contains("mac")) {
            OS = "mac";
            CLASS_PATH_DELIMITER = ":";
        }else if (osInfo.contains("linux")) {
            OS = "linux";
            CLASS_PATH_DELIMITER = ":";
        }else {
            OS = "other";
            CLASS_PATH_DELIMITER = ":";
        }
    }

    public static void main(String[] args) {
        System.err.println(System.getProperty("os.name"));
    }

}
