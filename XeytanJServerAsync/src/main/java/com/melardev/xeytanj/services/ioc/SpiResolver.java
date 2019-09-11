package com.melardev.xeytanj.services.ioc;

import com.melardev.xeytanj.errors.NoSuchDependencyException;
import com.melardev.xeytanj.services.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

class SpiResolver implements IAppDependencyResolver {
    private static SpiResolver self;
    private ArrayList<IService> services;


    @Override
    public List<? extends IService> getAllDependencies() {
        services = new ArrayList<>();
        ServiceLoader<IService> loader = ServiceLoader.load(IService.class);

        for (IService implClass : loader) {
            System.out.println(implClass.getClass().getSimpleName());
            services.add(implClass);
            implClass.initialize();
        }

        return services;
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz) {
        IService service = (S) services.stream().filter(s -> clazz.isAssignableFrom(s.getClassType())).findFirst().orElseThrow(NoSuchDependencyException::new);
        try {
            return (S) service;
        } catch (ClassCastException ex) {
            throw new NoSuchDependencyException();
        }
    }

    public <S extends IService> S lookup(String name) {
        IService service = services.stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow(NoSuchDependencyException::new);
        try {
            return (S) service;
        } catch (ClassCastException ex) {
            throw new NoSuchDependencyException();
        }
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz, String qualifier) {
        IService service = services.stream().filter(s -> s.getName().equals(qualifier) && clazz.isAssignableFrom(s.getClassType()))
                .findFirst().orElseThrow(NoSuchDependencyException::new);
        try {
            return (S) service;
        } catch (ClassCastException ex) {
            throw new NoSuchDependencyException();
        }
    }


    public synchronized static SpiResolver getInstance() {
        if (self == null) {
            self = new SpiResolver();
            self.getAllDependencies();
        }
        return self;
    }

}
