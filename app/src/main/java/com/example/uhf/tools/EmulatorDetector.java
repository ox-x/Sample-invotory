package com.example.uhf.tools;

import android.os.Build;

/**
 * 模拟器/虚拟设备检测工具类。
 * 用于检测当前是否运行在Android模拟器环境中，以便自动切换为模拟模式，
 * 避免因RFID硬件API不存在而导致应用崩溃。
 */
public class EmulatorDetector {

    private static Boolean sIsEmulatorCache = null;

    /**
     * 判断当前是否运行在模拟器上。
     * 结果会被缓存以提高性能。
     */
    public static boolean isEmulator() {
        if (sIsEmulatorCache != null) {
            return sIsEmulatorCache;
        }

        sIsEmulatorCache = detectEmulator();
        return sIsEmulatorCache;
    }

    private static boolean detectEmulator() {
        // 方法1: 检查 Build 特征
        if (checkBuildProperties()) {
            return true;
        }

        // 方法2: 检查特定文件/设备
        if (checkDeviceSpecific()) {
            return true;
        }

        return false;
    }

    private static boolean checkBuildProperties() {
        String fingerprint = Build.FINGERPRINT;
        if (fingerprint != null) {
            if (fingerprint.contains("generic") || fingerprint.contains("emu")) {
                return true;
            }
        }

        String model = Build.MODEL;
        if (model != null) {
            if (model.contains("google_sdk")
                    || model.contains("Emulator")
                    || model.contains("Android SDK built for x86")
                    || model.contains("sdk_gphone")
                    || model.contains("sdk_phone")
                    || model.contains("emu64")
                    || model.contains("emu32")) {
                return true;
            }
        }

        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null) {
            if (manufacturer.contains("Genymotion")
                    || manufacturer.contains("Google")
                    && model != null && model.contains("sdk")) {
                return true;
            }
        }

        String brand = Build.BRAND;
        if (brand != null && brand.contains("generic") && model != null && model.contains("generic")) {
            return true;
        }

        String product = Build.PRODUCT;
        if (product != null) {
            if (product.contains("sdk")
                    || product.contains("emu")
                    || product.contains("vbox")
                    || product.contains("generic")) {
                return true;
            }
        }

        String hardware = Build.HARDWARE;
        if (hardware != null) {
            if (hardware.contains("goldfish")
                    || hardware.contains("ranchu")
                    || hardware.contains("vbox")) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkDeviceSpecific() {
        // Android模拟器通常有特定的无线电固件版本
        String radioVersion = getRadioVersion();
        if (radioVersion != null && radioVersion.contains("Unknown")) {
            return true;
        }

        return false;
    }

    /**
     * 安全地获取无线电固件版本，不会抛出异常。
     */
    private static String getRadioVersion() {
        try {
            return Build.getRadioVersion();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 清除缓存的结果，通常不需要调用。
     */
    public static void clearCache() {
        sIsEmulatorCache = null;
    }
}
