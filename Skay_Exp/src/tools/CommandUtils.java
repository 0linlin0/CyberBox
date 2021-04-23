package tools;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class CommandUtils {
    private static final String DEFAULT_CHARSET = "GBK";

    /**
     * 执行指定命令
     *
     * @param command 命令
     * @return 命令执行完成返回结果
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    public static String exeCommand(String command) throws IOException {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            int exitCode = exeCommand(command, out);
            if (exitCode == 0) {
                System.out.println("命令运行成功！");
            } else {
                System.out.println("命令运行失败！");
            }
            return out.toString(DEFAULT_CHARSET);
        }
    }

    /**
     * 执行指定命令，输出结果到指定输出流中
     *
     * @param command 命令
     * @param out 执行结果输出流
     * @return 执行结果状态码：执行成功返回0
     * @throws ExecuteException 失败时抛出异常，由调用者捕获处理
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    public static int exeCommand(String command, OutputStream out) throws ExecuteException, IOException {
        CommandLine commandLine = CommandLine.parse(command);
        PumpStreamHandler pumpStreamHandler = null;
        if (null == out) {
            pumpStreamHandler = new PumpStreamHandler();
        } else {
            pumpStreamHandler = new PumpStreamHandler(out);
        }

        // 设置超时时间为10秒
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10000000);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);
        executor.setWatchdog(watchdog);

        return executor.execute(commandLine);
    }

    public static void main(String[] args) {
        try {
//            String result = exeCommand("E:\\tools\\nc.exe -lvvp 9000");
//            System.out.println(result);
            CommandLine commandLine = CommandLine.parse("E:\\tools\\nc.exe -lvvp 9000");
//            CommandLine commandLine = CommandLine.parse("whoami");
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PumpStreamHandler pumpStreamHandler = null;
            if (null == out) {
                pumpStreamHandler = new PumpStreamHandler();
            } else {
                pumpStreamHandler = new PumpStreamHandler(out);
            }

            // 设置超时时间为10秒
            ExecuteWatchdog watchdog = new ExecuteWatchdog(10000000);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(pumpStreamHandler);
            executor.setWatchdog(watchdog);
            executor.execute(commandLine);

            int bytesRead = 0;
            byte[] tempBuffer = new byte[8192*2];
            while (true) {
                Thread.sleep(1000);
                out.write(tempBuffer, 0, bytesRead);
                System.out.print(out.toString(DEFAULT_CHARSET));
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
