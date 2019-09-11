package com.melardev.xeytanj.services.builder;

import com.melardev.xeytanj.models.BuildClientInfoStructure;
import com.melardev.xeytanj.services.config.ConfigService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommandLineClientBuilder implements IClientBuilder {

    private ConfigService config;


    void setConfig(ConfigService config) {
        this.config = config;
    }

    public boolean build(BuildClientInfoStructure buildClientInfoStructure) {
        Map<String, String> props = new HashMap<>();
        props.put("Server-Host", buildClientInfoStructure.getServerHost());
        props.put("Server-Port", buildClientInfoStructure.getServerPort());
        props.put("Connection-Retry", String.valueOf(buildClientInfoStructure.getConnectionRetry()));
        props.put("Connection-Key", buildClientInfoStructure.getKey());
        props.put("Enable-Persistence", String.valueOf(buildClientInfoStructure.isPersistenceEnabled()));


        URL ff = CommandLineClientBuilder.class.getResource("CommandLineClientBuilder.class");
        if (ff.getProtocol().contains("file")) {

            File file = new File("./xeytanj-client.jar");
            try {
                File propsFile = new File("./client_properties.MF");
                BufferedWriter fos = new BufferedWriter(new PrintWriter(new FileOutputStream(propsFile)));
                props.forEach((key, value) -> {
                    try {
                        fos.write(key + ": " + value + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                fos.write("\n");
                fos.write("\n");
                fos.close();

/*
                String defaultPath = config.getDefaultClientPath();
                String defaultName = config.getDefaultClientName();
*/

                File outputFile = buildClientInfoStructure.getOutputFile();

                if (!outputFile.getName().endsWith(".jar"))
                    outputFile = new File(outputFile.getAbsolutePath() + ".jar");

                Files.copy(file.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String jarPath = findJarPath();

                String result = execAndGetOutput(String.format("cmd /c %s uvfm %s %s", jarPath, outputFile.getAbsolutePath(), propsFile.getAbsolutePath()));
                return true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("jar");
            return true;
        }

        return false;
    }

    public static String execAndGetOutput(String command) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command);

        BufferedReader processOutputReader = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));


        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = processOutputReader.readLine()) != null) {
            sb.append(line);
        }

        while ((line = stdError.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private String findJarPath() {
        try {
            String result = execAndGetOutput("cmd /c jar --version");
            if (result.startsWith("jar "))
                return "jar"; // can be used in the terminal directly
        } catch (IOException e) {
            e.printStackTrace();
        }
        String javaPath = System.getProperty("java.home");
        File jarFilePath = new File(javaPath + "/bin/jar.exe");

        if (jarFilePath.exists())
            return jarFilePath.getAbsolutePath();

        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null) {
            jarFilePath = new File(javaHome + "/bin/jar.exe");
            if (jarFilePath.exists())
                return jarFilePath.getAbsolutePath();
        }

        try {
            String jarStringPath = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("where jar").getInputStream())).readLine();
            if (jarStringPath.contains("jar"))
                return jarStringPath;
        } catch (IOException e) {
        }

        return null;
    }

    boolean isRunningInJarFile() {
        // jar:com.melardev.... is Jar
        // file:com.melardev... is File
        URL url = CommandLineClientBuilder.class.getResource("CommandLineClientBuilder.class");
        if (url.getProtocol().startsWith("jar:"))
            return true;

        return false;
    }

    String getJarPath() {
        //String f = new File(CommandLineClientBuilder.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        String path = CommandLineClientBuilder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //String decodedPath = URLDecoder.decode(path, "UTF-8");
        return path;
    }

    public static void main(String[] args) {

        CommandLineClientBuilder builder = new CommandLineClientBuilder();
        builder.build(new BuildClientInfoStructure("localhost", "9000", 3000, "xeytaj", true,
                new File("./output.jar")));


    }


    @Override
    public void dispose() {

    }
}
