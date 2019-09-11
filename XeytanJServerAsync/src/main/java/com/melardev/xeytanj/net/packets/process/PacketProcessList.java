package com.melardev.xeytanj.net.packets.process;

import com.melardev.xeytanj.enums.ProcessCommand;
import com.melardev.xeytanj.models.ProcessStructure;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PacketProcessList extends PacketProcess {

    private List<ProcessStructure> processStructures;

    public static enum InfoLevel {
        BASIC, INTERMEDIATE, ADVANCED, FULL, CUSTOM
    }

    public static enum Format {
        CSV, LIST
    }

    public static enum Command {
        KILL_PROCESS, PROPERTIES_PROCESS
    }

    private String result;
    private String cmdExecuted;
    private String[] columnNames;
    private Format format = Format.LIST;
    private int cols;
    private InfoLevel level;
    private ArrayList<ImageIcon> icons;
    private Command command;
    private ProcessStructure processStructure;


    public PacketProcessList(String[] columnNames) {
        super(ProcessCommand.LIST_PROCESS);
        this.columnNames = columnNames;
        level = InfoLevel.CUSTOM;
    }

    public PacketProcessList() {
        super(ProcessCommand.LIST_PROCESS);
        level = InfoLevel.BASIC;
        columnNames = new String[]{"Name", "ProcessId", "ExecutablePath"};
    }

    public PacketProcessList(InfoLevel infoLevel) {
        super(ProcessCommand.LIST_PROCESS);
        level = infoLevel;

        setColNames(infoLevel);
    }

    public PacketProcessList(InfoLevel level, String _cmdExecuted, String _result, ArrayList<ImageIcon> icons, int cols) {
        super(ProcessCommand.LIST_PROCESS);
        cmdExecuted = _cmdExecuted;
        result = _result;
        this.icons = icons;
        this.cols = cols;
        setColNames(level);
    }

    public PacketProcessList(Command command, int pid) {
        super(ProcessCommand.LIST_PROCESS);
        this.command = command;
        processStructure = new ProcessStructure(pid);
    }

    private void setColNames(InfoLevel _level) {
        switch (_level) {
            case BASIC:
                columnNames = new String[]{"Name", "ProcessId", "ExecutablePath"};
                break;
            case INTERMEDIATE:
                columnNames = new String[]{"Name", "ProcessId", "ExecutablePath", "Description", "CommandLine",
                        "ReadOperationCount", "ReadTransferCount"};
                break;
            case ADVANCED:
                columnNames = new String[]{"Name", "ProcessId", "ExecutablePath", "Description", "CommandLine",
                        "ReadOperationCount", "ReadTransferCount", "PageFaults", "PageFileUsage", "PrivatePageCount"};
                break;
            case FULL:
                columnNames = new String[]{"Description", "CommandLine",
                        "ExecutablePath", "ExecutionState", "Handle", "HandleCount", "InstallDate", "KernelModeTime",
                        "MaximumWorkingSetSize", "MinimumWorkingSetSize", "Name", "OSName", "OtherOperationCount",
                        "OtherTransferCount", "PageFaults", "PageFileUsage", "ParentProcessId", "PeakPageFileUsage",
                        "PeakVirtualSize", "PeakWorkingSetSize", "Priority", "PrivatePageCount", "ProcessId",
                        "QuotaNonPagedPoolUsage", "QuotaPagedPoolUsage", "QuotaPeakNonPagedPoolUsage",
                        "QuotaPeakPagedPoolUsage", "ReadOperationCount", "ReadTransferCount", "SessionId", "Status",
                        "TerminationDate", "ThreadCount", "UserModeTime", "VirtualSize", "WindowsVersion", "WorkingSetSize",
                        "WriteOperationCount", "WriteTransferCount"};
                break;
            case CUSTOM:
                break;
        }
    }


    public List<ProcessStructure> getProcessStructures() {
        return processStructures;
    }

    public void setProcessStructures(List<ProcessStructure> processStructures) {
        this.processStructures = processStructures;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCmdExecuted() {
        return cmdExecuted;
    }

    public void setCmdExecuted(String cmdExecuted) {
        this.cmdExecuted = cmdExecuted;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public InfoLevel getLevel() {
        return level;
    }

    public void setLevel(InfoLevel level) {
        this.level = level;
    }

    public ArrayList<ImageIcon> getIcons() {
        return icons;
    }

    public void setIcons(ArrayList<ImageIcon> icons) {
        this.icons = icons;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public ProcessStructure getProcessStructure() {
        return processStructure;
    }

    public void setProcessStructure(ProcessStructure processStructure) {
        this.processStructure = processStructure;
    }
}
