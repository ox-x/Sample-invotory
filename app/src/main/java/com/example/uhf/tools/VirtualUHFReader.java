package com.example.uhf.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 虚拟 UHF RFID 阅读器 - 用于模拟器环境。
 * 当检测到运行在模拟器上时，此类提供模拟的标签数据和方法返回值，
 * 使 UI 界面可以正常测试和展示，而不依赖实际的 RFID 硬件。
 */
public class VirtualUHFReader {

    private static final String TAG = "VirtualUHFReader";
    private boolean initialized = false;
    private boolean inventorying = false;
    private final Random random = new Random();

    // 模拟的功率级别 (1-30)
    private int powerLevel = 20;

    // 模拟标签计数器
    private int simulatedTagCount = 0;

    public VirtualUHFReader() {
    }

    /**
     * 模拟初始化，总是返回 true。
     */
    public boolean init() {
        initialized = true;
        return true;
    }

    /**
     * 模拟释放资源。
     */
    public void free() {
        initialized = false;
        inventorying = false;
    }

    /**
     * 获取模拟版本号。
     */
    public String getVersion() {
        return UHFConstants.EMULATOR_VERSION;
    }

    /**
     * 获取模拟硬件版本号。
     */
    public String getHardwareVersion() {
        return UHFConstants.EMULATOR_HARDWARE_VERSION;
    }

    /**
     * 获取模拟功率。
     */
    public int getPower() {
        return powerLevel;
    }

    /**
     * 设置模拟功率。
     */
    public boolean setPower(int power) {
        if (power >= 1 && power <= 30) {
            this.powerLevel = power;
            return true;
        }
        return false;
    }

    /**
     * 模拟单次盘点 - 返回模拟的标签数据。
     */
    public UHFTAGInfoSimulator inventorySingleTag() {
        if (!initialized) return null;
        simulatedTagCount++;
        return generateMockTag();
    }

    /**
     * 模拟开始连续盘点。
     */
    public boolean startInventoryTag() {
        if (!initialized) return false;
        inventorying = true;
        return true;
    }

    /**
     * 模拟停止连续盘点。
     */
    public boolean stopInventory() {
        inventorying = false;
        return true;
    }

    /**
     * 检查是否正在盘点。
     */
    public boolean isInventorying() {
        return inventorying;
    }

    /**
     * 设置模拟模式下的回调。
     */
    public void setInventoryCallback(Object callback) {
        // 在模拟模式下忽略回调
    }

    /**
     * 模拟读取数据。
     */
    public String readData(String password, int bank, int ptr, int len) {
        if (!initialized) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len * 4; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString();
    }

    /**
     * 模拟带过滤的读取数据。
     */
    public String readData(String password, int filterBank, int filterPtr, int filterCnt,
                           String filterData, int bank, int ptr, int len) {
        return readData(password, bank, ptr, len);
    }

    /**
     * 模拟写入数据。
     */
    public boolean writeData(String password, int bank, int ptr, int len, String data) {
        return initialized;
    }

    /**
     * 模拟带过滤的写入数据。
     */
    public boolean writeData(String password, int filterBank, int filterPtr, int filterCnt,
                             String filterData, int bank, int ptr, int len, String data) {
        return initialized;
    }

    /**
     * 模拟块写入。
     */
    public boolean blockWriteData(String password, int filterBank, int filterPtr, int filterCnt,
                                  String filterData, int bank, int ptr, int len, String data) {
        return initialized;
    }

    /**
     * 模拟锁定标签。
     */
    public boolean lockMem(String password, String lockCode) {
        return initialized;
    }

    /**
     * 模拟带过滤的锁定标签。
     */
    public boolean lockMem(String password, int filterBank, int filterPtr, int filterCnt,
                           String filterData, String lockCode) {
        return initialized;
    }

    /**
     * 模拟销毁标签。
     */
    public boolean killTag(String password) {
        return initialized;
    }

    /**
     * 模拟带过滤的销毁标签。
     */
    public boolean killTag(String password, int filterBank, int filterPtr, int filterCnt, String filterData) {
        return initialized;
    }

    /**
     * 模拟永久锁定块。
     */
    public boolean uhfBlockPermalock(String password, int filterBank, int filterPtr, int filterCnt,
                                     String filterData, int readLock, int bank, int ptr, int range, byte[] mask) {
        return initialized;
    }

    // ==================== 设置类方法 ====================

    public boolean setFrequencyMode(byte mode) { return initialized; }
    public int getFrequencyMode() { return 0x08; }
    public boolean setRFLink(int link) { return initialized; }
    public int getRFLink() { return 0; }
    public boolean setTagFocus(boolean enable) { return initialized; }
    public boolean setFastID(boolean enable) { return initialized; }
    public boolean setFreHop(float value) { return initialized; }
    public boolean setProtocol(int protocol) { return initialized; }
    public boolean setEPCMode() { return initialized; }
    public boolean setEPCAndTIDMode() { return initialized; }
    public boolean setEPCAndTIDUserMode(int offset, int length) { return initialized; }
    public boolean setEPCAndTIDUserMode(Object entity) { return initialized; }
    public Object getEPCAndTIDUserMode() { return null; }
    public boolean setFilter(int bank, int ptr, int len, String data) { return initialized; }
    public boolean setDynamicDistance(int distance) { return initialized; }

    // ==================== 定位类方法 ====================

    public boolean startLocation(Object context, String epc, int bank, int offset, Object callback) {
        return initialized;
    }
    public void stopLocation() {}
    public boolean startRadarLocation(Object context, String epc, int bank, int offset, Object callback) {
        return initialized;
    }
    public boolean stopRadarLocation() { return initialized; }

    // ==================== Gen2 会话方法 ====================

    public Object getGen2() { return null; }
    public boolean setGen2(Object entity) { return initialized; }

    // ==================== 快速盘点方法 ====================

    public int getFastInventoryMode() { return 0; }
    public boolean setFastInventoryMode(boolean enable) { return initialized; }

    // ==================== 固件升级方法 ====================

    public boolean uhfJump2Boot(int type) { return initialized; }
    public boolean uhfStartUpdate() { return initialized; }
    public boolean uhfUpdating(byte[] data) { return initialized; }
    public boolean uhfStopUpdate() { return initialized; }
    public boolean factoryReset() { return initialized; }

    // ==================== 模拟标签数据生成 ====================

    private UHFTAGInfoSimulator generateMockTag() {
        String epc = generateHexString(24);
        String tid = generateHexString(24);
        String user = generateHexString(8);
        String rssi = String.valueOf(-50 - random.nextInt(40));
        int count = 1 + random.nextInt(5);
        return new UHFTAGInfoSimulator(epc, tid, user, rssi, count, random.nextInt(360));
    }

    private String generateHexString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 模拟的 UHF 标签信息数据类。
     */
    public static class UHFTAGInfoSimulator {
        private final String epc;
        private final String tid;
        private final String user;
        private final String rssi;
        private int count;
        private final int phase;

        public UHFTAGInfoSimulator(String epc, String tid, String user, String rssi, int count, int phase) {
            this.epc = epc;
            this.tid = tid;
            this.user = user;
            this.rssi = rssi;
            this.count = count;
            this.phase = phase;
        }

        public String getEPC() { return epc; }
        public String getTid() { return tid; }
        public String getUser() { return user; }
        public String getRssi() { return rssi; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public int getPhase() { return phase; }
        public String getReserved() { return null; }
    }
}
