package org.dromara.neutrinoproxy.client.starter.util;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Scanner;
/**
 * 简易ssh客户端
 * @author: gc.x
 * @date: 2024/1/21
 */
public class SSHClient {

    private static final String COMMAND_UPLOAD = "upload";
    private Session session;

    public void connect(String remoteHost, String remoteUsername, String remotePassword) {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(remoteUsername, remoteHost, 22);
            session.setPassword(remotePassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println("Connected to " + remoteHost);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
    public void startShell() {
        if (session == null || !session.isConnected()) {
            System.out.println("Not connected to a remote host");
            return;
        }
        Channel channel;
        try {
            channel = session.openChannel("shell");
            channel.connect();
            Scanner scanner = new Scanner(System.in);
            String line;
            while (true) {
                System.out.print("$ ");
                line = scanner.nextLine();
                if (line.equals("exit")) {
                    break;
                } else if (line.startsWith(COMMAND_UPLOAD)) {
                    processUploadCommand(line);
                } else {
                    System.out.println("Executing command: " + line);
                    executeCommand(line);
                }
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }finally {
            disconnect();
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("Disconnected from remote host");
        }
    }

    private void processUploadCommand(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 3) {
            System.out.println("Invalid upload command");
            return;
        }
        String localFilePath = parts[1];
        String remoteDirectory = parts[2];
        String remoteFilename = Paths.get(localFilePath).getFileName().toString();
        if (parts.length >= 4) {
            remoteFilename = parts[3];
        }
        uploadFile(localFilePath, remoteDirectory, remoteFilename);
    }

    private void uploadFile(String localFilePath, String remoteDirectory, String remoteFilename) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(remoteDirectory);
            System.out.println("Uploading file: " + localFilePath + " to: " + remoteDirectory + "/" + remoteFilename);
            channelSftp.put(localFilePath, remoteFilename);
            System.out.println("File uploaded successfully");
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }
    private void executeCommand(String command) {
        ChannelExec channelExec = null;

        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            channelExec.connect();
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            channelExec.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
    }
}
