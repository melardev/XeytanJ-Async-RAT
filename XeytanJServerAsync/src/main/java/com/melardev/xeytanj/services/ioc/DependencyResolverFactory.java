package com.melardev.xeytanj.services.ioc;

public class DependencyResolverFactory {

    private static IAppDependencyResolver serviceLocator;
    private final static Object resolverLock = new Object();

    public static IAppDependencyResolver getDependencyResolver() {
        synchronized (resolverLock) {
            if (serviceLocator == null) {
                serviceLocator = SpiResolver.getInstance();
                // serviceLocator = SpringBeansResolver.getInstance();
            }
        }
        return serviceLocator;
    }
}
