package com.melardev.xeytanj.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Manifest;

public class ManifestConfigService implements IConfigService {
    public Properties properties;

    public ManifestConfigService() {

        try {
            properties = new Properties();

            if (isInJar()) {
                Manifest manifest = new Manifest(this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
                properties.putAll(manifest.getMainAttributes());
            } else {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test/test.txt");
                if (inputStream == null)
                    System.exit(0);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length <= 1)
                        continue;
                    properties.put(parts[0].trim(), parts[1].trim());
                }
            }

            Set<Map.Entry<Object, Object>> vp = properties.entrySet();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Object, Object> e : vp) {
                System.out.println(e.getKey() + " " + e.getValue());
                sb.append(e.getKey() + " " + e.getValue() + "\n");
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private boolean isInJar() {

        // jar:com.melardev.... is Jar
        // file:com.melardev... is File
        URL url = getClass().getResource("IConfigService.class");
        if (url.getProtocol().startsWith("jar:"))
            return true;

        return false;
    }

    @Override
    public String getLoginKey() {
        return "Connection-Key";
    }

    @Override
    public String getServerHost() {
        return properties.getProperty("Server-Host");
    }

    @Override
    public String getServerPort() {
        return properties.getProperty("Server-Port");
    }

    @Override
    public boolean isPersistenceEnabled() {
        return Boolean.parseBoolean((String) properties.getOrDefault("Enable-Persistence", false));
    }
}
