package com.melardev.xeytanj.services;

import com.melardev.xeytanj.services.logger.ILogger;

public interface IService {
    default String getName() {
        return getClass().getSimpleName();
    }

    default Class getClassType() {
        return getClass();
    }

    default void dispose() {
    }

    default void initialize() {
    }

    default void setLogger(ILogger logger) {
    }
}
