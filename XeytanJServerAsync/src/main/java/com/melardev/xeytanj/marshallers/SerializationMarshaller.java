package com.melardev.xeytanj.marshallers;

import com.melardev.xeytanj.net.packets.Packet;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;

public class SerializationMarshaller implements INetMarshaller {

    ObjectInputStream objectInputStream;

    public SerializationMarshaller() {

    }

    @Override
    public boolean canRead(byte[] bytes, Class clazz) {

        // If not serializable then return false
        if (!(clazz instanceof Serializable))
            return false;

        // TODO: I have to improve with bytes buffer length check
        // if we have enough to serialize
        if (bytes.length < 8)
            return false;


        if (bytes[0] == (byte) 0xAC && bytes[1] == (byte) 0xED && bytes[2] == (byte) 0
                && bytes[3] == (byte) 0x05) {
            int classNameLenght = bytes[7];
            if (bytes.length < (8 + classNameLenght))
                return false;

            String className = new String(bytes, 8, classNameLenght);
            return className.equals(Packet.class.getCanonicalName());
        }

        return false;
    }


    @Override
    public <T> T read(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
            return (T) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


    @Override
    public boolean canWrite(Class clazz) {
        return Packet.class.isAssignableFrom(clazz) && clazz instanceof Serializable;
    }

    @Override
    public <T> byte[] write(T obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;
        try {
            objectOutput = new ObjectOutputStream(outputStream);

            objectOutput.writeObject(obj);
            objectOutput.flush();
            byte[] result = SerializationUtils.serialize((Serializable) obj);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutput != null)
                    objectOutput.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
