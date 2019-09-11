package com.melardev.xeytanj.services.ioc;

import java.util.List;

public interface IDependencyResolver<T> {
    List<? extends T> getAllDependencies();

    <S extends T> S lookup(Class<S> clazz);

    <S extends T> S lookup(String name);

    <S extends T> S lookup(Class<S> clazz, String qualifier);
}
