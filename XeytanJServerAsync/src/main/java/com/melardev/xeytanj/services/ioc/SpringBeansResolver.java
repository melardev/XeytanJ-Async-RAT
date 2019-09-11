package com.melardev.xeytanj.services.ioc;

import com.melardev.xeytanj.services.IService;
import com.melardev.xeytanj.services.config.ConfigService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: Refactor this

class SpringBeansResolver implements IAppDependencyResolver {
    private static SpringBeansResolver self;
    private AnnotationConfigApplicationContext ctx;
    private Map<String, IService> cache;

    public synchronized static SpringBeansResolver getInstance() {
        if (self == null) {
            self = new SpringBeansResolver();
            self.init();
        }
        return self;
    }

    public List<Object> init() {
        ctx = new AnnotationConfigApplicationContext();
        ctx.register(SpringAppConfig.class);
        ctx.refresh();

        ctx.getBean(ConfigService.class);
        Map<String, IService> beans = ctx.getBeansOfType(IService.class);
        return new ArrayList<>(beans.values());
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz) {
        return ctx.getBean(clazz);
    }

    @Override
    public <S extends IService> S lookup(String name) {
        return (S) ctx.getBean(name);
    }

    @Override
    public <S extends IService> S lookup(Class<S> clazz, String qualifier) {
        return ctx.getBean(qualifier, clazz);
    }

    @Override
    public List<? extends IService> getAllDependencies() {
        return new ArrayList<>(cache.values());
    }

    public void dispose() {
        cache.values().forEach(IService::dispose);
        System.out.println("Clean up Function called");
    }
}
