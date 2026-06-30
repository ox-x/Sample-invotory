package com.example.uhf.tools;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * 应用内操作日志管理器
 * 记录关键操作（借还、入库、Kitting等）并支持应用内查看及无线导出
 */
public class OperationLogManager {

    private static final String TAG = "OpLogManager";
    private static final int MAX_MEMORY_LOGS = 500;
    private static OperationLogManager instance;

    private final List<LogEntry> logEntries = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private OperationLogManager() {
        // 启动时自动加载今日历史日志到内存
        loadTodayLogs();
    }

    /** 加载今日日志文件中所有条目到内存 */
    private void loadTodayLogs() {
        try {
            File file = getTodayLogFile();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    // 解析格式: [timestamp] [type] message
                    LogEntry entry = parseLogLine(line);
                    if (entry != null) {
                        logEntries.add(entry);
                    }
                }
                br.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "加载历史日志失败", e);
        }
        // （不再加载UHF_exportData中已有的xls导出文件）
    }

    /** 加载 UHF_exportData 目录中的已有 xls 文件为日志条目 */
    private void loadXlsFiles() {
        File exportDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + "UHF_exportData");
        File[] xlsFiles = exportDir.listFiles((d, name) -> name.endsWith(".xls"));
        if (xlsFiles == null || xlsFiles.length == 0) return;

        // 按修改时间倒序排列，只加载最新的5个文件
        Arrays.sort(xlsFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        int maxFiles = Math.min(xlsFiles.length, 5);
        for (int i = 0; i < maxFiles; i++) {
            parseXlsFile(xlsFiles[i]);
        }
    }

    /** 解析单个 xls 文件为 LogEntry */
    private void parseXlsFile(File file) {
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            if (rows <= 1) return; // 只有表头，无数据

            String fileName = file.getName();
            String ts = fileName.length() >= 14
                    ? fileName.substring(0, 4) + "-" + fileName.substring(4, 6) + "-" + fileName.substring(6, 8)
                        + " " + fileName.substring(8, 10) + ":" + fileName.substring(10, 12) + ":" + fileName.substring(12, 14)
                    : dateFormat.format(new Date(file.lastModified()));
            int dataRows = rows - 1; // 去掉表头

            // 添加文件摘要条目
            logEntries.add(new LogEntry(ts, "导出文件", fileName + " 共" + dataRows + "条"));

            // 添加每行标签数据（最多50行）
            int maxRows = Math.min(rows, 51);
            for (int r = 1; r < maxRows; r++) {
                Cell[] row = sheet.getRow(r);
                if (row == null || row.length < 2) continue;
                String epc = row.length > 0 ? row[0].getContents().trim() : "";
                String tid = row.length > 1 ? row[1].getContents().trim() : "";
                String rssi = row.length > 4 ? row[4].getContents().trim() : "";
                logEntries.add(new LogEntry(ts, "RFID标签",
                        "EPC:" + epc + " TID:" + tid + " RSSI:" + rssi));
            }
            if (dataRows > 50) {
                logEntries.add(new LogEntry(ts, "导出文件",
                        "..." + (dataRows - 50) + "条记录已省略"));
            }
        } catch (Exception e) {
            Log.e(TAG, "解析xls失败: " + file.getName(), e);
        }
    }

    /** 解析单行日志文本为 LogEntry 对象 */
    private LogEntry parseLogLine(String line) {
        try {
            if (!line.startsWith("[") || line.length() < 3) return null;
            int closeBracket1 = line.indexOf("] ");
            if (closeBracket1 < 0) return null;
            String timestamp = line.substring(1, closeBracket1);

            String rest = line.substring(closeBracket1 + 2).trim();
            if (!rest.startsWith("[") || rest.length() < 3) return null;
            int closeBracket2 = rest.indexOf("] ");
            if (closeBracket2 < 0) return null;
            String type = rest.substring(1, closeBracket2);
            String message = rest.substring(closeBracket2 + 2).trim();

            return new LogEntry(timestamp, type, message);
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized OperationLogManager getInstance() {
        if (instance == null) {
            instance = new OperationLogManager();
        }
        return instance;
    }

    /** 记录一条日志 */
    public void log(String type, String message) {
        String now = dateFormat.format(new Date());
        LogEntry entry = new LogEntry(now, type, message);
        synchronized (logEntries) {
            logEntries.add(entry);
            if (logEntries.size() > MAX_MEMORY_LOGS) {
                logEntries.remove(0);
            }
        }
        appendToFile(entry);
        Log.i(TAG, "[" + type + "] " + message);
    }

    /** 获取内存中所有日志（副本） */
    public List<LogEntry> getLogs() {
        synchronized (logEntries) {
            return new ArrayList<>(logEntries);
        }
    }

    /** 清空内存日志 */
    public void clearLogs() {
        synchronized (logEntries) {
            logEntries.clear();
        }
    }

    /** 追加日志到文件 */
    private void appendToFile(LogEntry entry) {
        try {
            String dirPath = getLogDirPath();
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            File logFile = new File(dir, "operation_" + today + ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.write(entry.toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            Log.e(TAG, "写日志文件失败", e);
        }
    }

    /** 日志目录路径 */
    public String getLogDirPath() {
        return Environment.getExternalStorageDirectory()
                + File.separator + "UHF_exportData" + File.separator + "logs";
    }

    /** 获取今日日志文件 */
    public File getTodayLogFile() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return new File(getLogDirPath(), "operation_" + today + ".txt");
    }

    /** 日志条目 */
    public static class LogEntry {
        public final String timestamp;
        public final String type;
        public final String message;

        LogEntry(String timestamp, String type, String message) {
            this.timestamp = timestamp;
            this.type = type;
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] [" + type + "] " + message;
        }
    }
}
