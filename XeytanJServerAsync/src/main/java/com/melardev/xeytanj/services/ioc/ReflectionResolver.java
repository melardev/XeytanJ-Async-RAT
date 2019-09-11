package com.melardev.xeytanj.services.ioc;

import com.melardev.xeytanj.services.IService;

import java.util.List;

class ReflectionResolver implements IAppDependencyResolver {

    //TODO: Implement this

    @Override
    public List<? extends IService> getAllDependencies() {
        return null;
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz) {
        return null;
    }

    @Override
    public <S extends IService> S lookup(String name) {
        return null;
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz, String qualifier) {
        return null;
    }
}
