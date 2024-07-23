package com.ancda.rtsppusher.utils;

import android.util.Log;
import com.ancda.rtsppusher.utils.log.ALog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * author  : LLhon
 * date    : 2024/6/24 16:08.
 * des     :
 */
public class PerformanceTestUtils {

    /**
     * 调用命令：adb shell top -n 1
     *
     *   Mem:  2004624K total,  1729688K used,   274936K free,    921600 buffers
     *  Swap:  1002308K total,   121856K used,   880452K free,   753396K cached
     * 400%cpu  20%user   0%nice  37%sys 337%idle   0%iow   7%irq   0%sirq   0%host
     *    PID USER         PR  NI VIRT  RES  SHR S[%CPU] %MEM     TIME+ ARGS
     *    563 system       18  -2  16G 355M 297M S 33.3  18.1 230:51.92 system_server
     *   7949 u0_a132      10 -10 2.0G 208M 126M S  0.0  10.6   0:24.84 com.ancda.rtsp+
     *
     *
     *   VIRT: 虚拟内存使用量 (VIRT)，表示进程使用的虚拟内存总量
     *   RES：常驻内存使用量 (RES)，表示进程实际使用的物理内存量
     *   SHAR：共享内存使用量 (SHR)，表示进程使用的共享内存量
     *   %CPU：CPU 使用率 (%CPU)，表示进程使用的 CPU 百分比
     *   %MEM：内存使用率 (%MEM)，表示进程使用的内存百分比
     *   TIME+：进程运行时间 (TIME+)，表示进程累计运行的时间，格式为分钟:秒.百分秒
     */

    /**
     * CPU 占用率
     * @return
     * @throws IOException
     */
    public static String topCpu() throws IOException {
        String cpuStr = "0";
        String packageName = "com.ancda.rtsp";
        Process proc = Runtime.getRuntime().exec("top -n 1");
        try {
            if (proc.waitFor() != 0) {
                Log.d("PerformanceTestUtils", "exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader( proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                ALog.INSTANCE.iToFile("topCpu", line);
                if(line.contains(packageName)) {
                    List<String> strList = Arrays.asList(line.split("\\s+"));
//                    if(strList.get(7).trim().contains("%")){
//                        cpuStr = strList.get(7).trim().replace("%", "");
//                    } else if(strList.get(6).trim().contains("%")) {
//                        cpuStr = strList.get(6).trim().replace("%", "");
//                    }
                    cpuStr = strList.get(9).trim();
                    break;
                }
            }
            in.close();
        } catch (InterruptedException e) {
            System.err.println(e);
        }finally{
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
        return cpuStr;
    }

    public static void dumpCpuInfo() throws IOException {
        String packageName = "com.ancda.rtsp";
        Process proc = Runtime.getRuntime().exec("dumpsys cpuinfo");
        try {
            if (proc.waitFor() != 0) {
                Log.d("PerformanceTestUtils", "exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader( proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                ALog.INSTANCE.iToFile("dumpCpuInfo", line);
                if(line.contains(packageName)) {
                    Log.e("dumpCpuInfo", line);
                    List<String> strList = Arrays.asList(line.split("\\s+"));
//                    if(strList.get(7).trim().contains("%")){
//                        cpuStr = strList.get(7).trim().replace("%", "");
//                    } else if(strList.get(6).trim().contains("%")) {
//                        cpuStr = strList.get(6).trim().replace("%", "");
//                    }
                }
            }
            in.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
    }

    /**
     * 内存
     *
     * @return
     * @throws IOException
     */
    public static String talHeapSize() throws IOException {
        String packageName = "com.ancda.rtsp";
        String heapStr = "0M";
        Process proc = Runtime.getRuntime().exec("top -n 1");
        try {
            if (proc.waitFor() != 0) {
                Log.d("PerformanceTestUtils", "exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains(packageName)) {
                    ALog.INSTANCE.iToFile("talHeapSize", line);
                    List<String> strList = Arrays.asList(line.split("\\s+"));
//                    if (strList.get(9).trim().contains("K")) {
//                        //1754876K
//                        heapStr = strList.get(9).trim().replace("K", "");
//                    } else if (strList.get(8).trim().contains("K")) {
//                        heapStr = strList.get(8).trim().replace("K", "");
//                    }
                    heapStr = strList.get(6).trim();
                    break;
                }
            }
            in.close();
        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
        return heapStr;
    }

    /**
     * Inter-|   Receive                                                |  Transmit
     *  face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
     *   eth0: 123456789  12345    0    0    0     0          0         0  987654321  12345    0    0    0     0       0          0
     *   wlan0: 234567891  23456    0    0    0     0          0         0  876543210  23456    0    0    0     0       0          0
     *
     *
     * Inter- | Receive | Transmit
     *  - Inter-：网络接口名称。
     *  - Receive：接收数据的统计信息。
     *  - Transmit：发送数据的统计信息。
     *
     * Receive 字段
     *  - bytes：接收到的字节数。
     *  - packets：接收到的数据包数。
     *  - errs：接收时发生的错误数。
     *  - drop：接收时丢弃的数据包数。
     *  - fifo：接收 FIFO 缓冲区溢出次数。
     *  - frame：接收的帧错误数。
     *  - compressed：接收的压缩包数（通常用于 PPP）。
     *  - multicast：接收的多播数据包数。
     *
     * Transmit 字段
     *  - bytes：发送的字节数。
     *  - packets：发送的数据包数。
     *  - errs：发送时发生的错误数。
     *  - drop：发送时丢弃的数据包数。
     *  - fifo：发送 FIFO 缓冲区溢出次数。
     *  - colls：发送时发生的碰撞数（通常用于以太网）。
     *  - carrier：载波丢失次数（通常用于以太网）。
     *  - compressed：发送的压缩包数（通常用于 PPP）
     */

    /**
     * 流量
     * @return
     * @throws IOException
     */
    public static long getWifiFlow() throws IOException {
        int Pid = android.os.Process.myPid();
        long str3 = 0;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("cat /proc/" + Pid + "/net/dev");
            try {
                if (proc.waitFor() != 0) {
                    Log.d("PerformanceTestUtils", "exit value = " + proc.exitValue());
                }
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                StringBuffer sb = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    sb.append(line + " ");
                    String str1 = line;
                    if (str1.contains("eth0:")) {
                        List<String> list = Arrays.asList(str1.split("\\s+"));
                        String rcv = list.get(2).trim();
                        String send = list.get(10).trim();
                        long a = Long.parseLong(rcv);
                        long b = Long.parseLong(send);
                        ALog.INSTANCE.iToFile("PerformanceTestUtils", "【流量数据统计】接收的数据量：" + ((a / 1024) / 1024) + "MB" + " 发送的数据量：" + ((b / 1024) / 1024) + "MB");
                        str3 = ((a + b) / 1024) / 1024;
                        break;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    proc.destroy();
                } catch (Exception e2) {
                }
            }
        } catch (Exception StringIndexOutOfBoundsException) {
        }
        return str3;
    }

    //获取PID
    public static String PID(String PackageName) throws IOException {
        String PID = null;
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec("ps | grep " + PackageName);
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + " ");
            }
            String str1 = stringBuffer.toString();
            if (str1.contains(PackageName)) {
                String str2 = str1.substring(8, 15);
                PID = str2;
                PID = PID.trim();
            } else {
                PID = null;
            }

        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
        Log.d("PerformanceTestUtils", "pid=" + PID);
        return PID;
    }

    public static double streamDouble(double pixRate) {
        return (double)Math.round(pixRate * 100) / 100;
    }
}
