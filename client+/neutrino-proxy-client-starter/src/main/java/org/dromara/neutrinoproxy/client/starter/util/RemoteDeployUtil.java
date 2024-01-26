package org.dromara.neutrinoproxy.client.starter.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
/**
 * 远程发布工具类
 * @author: gc.x
 * @date: 2024/1/21
 */
@Slf4j
public class RemoteDeployUtil {

    public static void uploadAndStartJar(List<String> localFilePaths, String remoteDirectory, String remoteStartCommand,
                                  String remoteUsername, String remoteHost, String remotePassword) {
        log.info("Start=================");
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = jsch.getSession(remoteUsername, remoteHost, 22);
            session.setPassword(remotePassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            // 创建远程目录（如果不存在）
            try {
                channelSftp.cd(remoteDirectory);
            } catch (SftpException e) {
                channelSftp.mkdir(remoteDirectory);
                channelSftp.cd(remoteDirectory);
            }
            // 上传本地文件到远程服务器指定目录
            for (String localFilePath : localFilePaths) {
                uploadFileWithProgress(channelSftp, localFilePath, remoteDirectory);
            }
            channelSftp.disconnect();
            // 执行远程命令启动jar包
            log.info("Command:"+remoteStartCommand);
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(remoteStartCommand);
            ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
            channelExec.setOutputStream(commandOutput);
            channelExec.connect();
            // 使用新线程读取并打印命令输出
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();
            // 等待命令执行完成
            while (!channelExec.isClosed()) {
                Thread.sleep(1000);
            }
            // 等待命令输出线程结束
            outputThread.join();
            channelExec.disconnect();
            log.info("=================End");
        } catch (JSchException | SftpException | InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private static void uploadFileWithProgress(ChannelSftp channelSftp, String localFilePath, String remoteDirectory) throws SftpException, IOException {
        File file = new File(localFilePath);
        String fileName = file.getName();
        String remoteFilePath = remoteDirectory + File.separator + fileName;
        AtomicLong uploadedSize = new AtomicLong();
        try (InputStream inputStream = new FileInputStream(file)) {
            // 如果远程目录已经存在同名文件，则先备份原文件
            if (fileExists(channelSftp, remoteFilePath)) {
                backupRemoteFile(channelSftp, remoteFilePath);
            }
            channelSftp.put(inputStream, remoteFilePath, new SftpProgressMonitor() {
                @Override
                public void init(int op, String src, String dest, long max) {
                    // 初始化回调函数，可以做一些准备工作
                }
                @Override
                public void end() {
                    // 上传结束回调函数，可以做一些清理工作
                }
                @Override
                public boolean count(long count) {
                    uploadedSize.addAndGet(count);
                    // 计算上传进度
                    int progress = (int) ((uploadedSize.get() * 100) / file.length());

                    // 每上传 10% 显示一次进度，可根据实际情况调整
                    if (progress % 10 == 0) {
                        log.info(fileName+"-----Upload progress: " + progress + "%");
                    }
                    // 返回 true 则继续传输，否则中止传输
                    return true;
                }
            }, ChannelSftp.RESUME);
        }
    }

    private static boolean fileExists(ChannelSftp channelSftp, String remoteFilePath) {
        try {
            channelSftp.lstat(remoteFilePath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    private static void backupRemoteFile(ChannelSftp channelSftp, String remoteFilePath) throws SftpException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String backupTime = now.format(formatter);
        String backupFilePath = remoteFilePath + "." + backupTime;
        channelSftp.rename(remoteFilePath, backupFilePath);
        log.info("Remote file already exists. Backing up the original file as: " + backupFilePath);
    }
}



