package com.melardev.xeytanj.marshallers;

public interface INetMarshaller {

    boolean canRead(byte[] bytes, Class clazz);

    <T> T read(byte[] bytes);

    boolean canWrite(Class clazz);

    <T> byte[] write(T obj);
}
