package com.melardev.xeytanj.services.data;

import java.io.File;

public class FsJdbcStorage extends InMemoryJdbcStorage {
    public FsJdbcStorage() {
        super();
    }

    @Override
    String getConnectionString() {
        return "jdbc:h2:file:" + new File(".").getAbsolutePath() + "/xeytanj.db;DB_CLOSE_DELAY=-1";
    }

    @Override
    public String getName() {
        return "fs_db";
    }
}
