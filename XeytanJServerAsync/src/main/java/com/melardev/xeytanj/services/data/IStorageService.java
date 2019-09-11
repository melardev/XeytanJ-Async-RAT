package com.melardev.xeytanj.services.data;

import com.melardev.xeytanj.errors.IOStorageException;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IService;
import com.melardev.xeytanj.services.config.ConfigService;
import com.melardev.xeytanj.services.logger.ILogger;

import java.util.List;

public interface IStorageService extends IService {
    Client save(Client client);

    void insertClient(Client client) throws IOStorageException;

    List<Client> getAllClients();

    void setLogger(ILogger logger);

    ConfigService.ConfigModel loadConfig();

    void saveConfig(ConfigService.ConfigModel configModel);
}
