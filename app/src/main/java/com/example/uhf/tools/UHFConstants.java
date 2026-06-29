package com.example.uhf.tools;

/**
 * UHF RFID 相关常量定义。
 * 从 com.rscja.deviceapi.RFIDWithUHFUART / IUHF 复制常量值，
 * 避免在模拟器环境中直接引用硬件库类导致类加载异常。
 */
public final class UHFConstants {

    // ==================== 存储区 Bank 常量 ====================
    /** EPC 存储区 */
    public static final int BANK_EPC = 1;
    /** TID 存储区 */
    public static final int BANK_TID = 2;
    /** USER 存储区 */
    public static final int BANK_USER = 3;
    /** RESERVED 存储区 */
    public static final int BANK_RESERVED = 0;

    // ==================== 默认值 ====================
    /** 默认访问密码 */
    public static final String DEFAULT_PASSWORD = "00000000";
    /** 默认功率 */
    public static final int DEFAULT_POWER = 20;
    /** 模拟模式版本号 */
    public static final String EMULATOR_VERSION = "Simulator v1.0";
    /** 模拟模式硬件版本号 */
    public static final String EMULATOR_HARDWARE_VERSION = "Virtual HW v1.0";

    private UHFConstants() {
        // 工具类，禁止实例化
    }
}
