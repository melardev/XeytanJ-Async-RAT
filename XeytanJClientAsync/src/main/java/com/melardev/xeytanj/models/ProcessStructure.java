package com.melardev.xeytanj.models;

import javax.swing.*;
import java.io.Serializable;

public class ProcessStructure implements Serializable {
    private Integer pid;
    private String processName;
    private String data;
    private Icon icon;
    private String processPath;

    public ProcessStructure() {
    }

    public ProcessStructure(int pid) {
        this.pid = pid;
    }

    public ProcessStructure(int pid, String data) {
        this(pid);
        this.data = data;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getProcessPath() {
        return processPath;
    }

    public void setProcessPath(String processPath) {
        this.processPath = processPath;
    }

}
