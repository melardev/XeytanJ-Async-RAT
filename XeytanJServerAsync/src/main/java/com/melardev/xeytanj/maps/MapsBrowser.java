package com.melardev.xeytanj.maps;


import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;
import com.melardev.xeytanj.utils.EnvUtils;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MapsBrowser implements IGui<MapUiListener> {
    public static String indent = "\n\t\t";
    private IUiMediator mediator;
    private MapUiListener listener;
    private IAppMessages messageProvider;
    private String googleMapsKey;
    private ArrayList<Client> clientLocations;

    public String extractIfNotExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);
                Files.copy(is, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }


    @Override
    public void display() {
        try {
            BufferedReader templateMapReader;
            if (EnvUtils.runningInJar()) {
                templateMapReader = new BufferedReader(
                        new InputStreamReader(getClass().getClassLoader().getResourceAsStream(extractIfNotExists("./html/index.html"))));
            } else {
                templateMapReader = new BufferedReader(new InputStreamReader(new FileInputStream("./src/main/resources/html/index.html")));
            }
            //new InputStreamReader(new FileInputStream("D:/xampp/htdocs/xeytanMap/index.html")));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = templateMapReader.readLine()) != null)
                sb.append(line + "\n");

            Pattern pattern = Pattern.compile("(.*//TEMPLATE_BEGIN_MARKERS\\s*)(.*)(\\s*//TEMPLATE_END_MARKERS.*)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
            String originalSourceCode = sb.toString();
            Matcher matcher = pattern.matcher(originalSourceCode);
            if (!matcher.matches()) {
                mediator.showErrorMessage("The map html file has not been found");
            }
            PrintWriter outMapWriter;
            if (EnvUtils.runningInJar()) {
                outMapWriter = new PrintWriter(new File(""));
            } else {
                outMapWriter = new PrintWriter(new File("./src/main/resources/html/index_final.html"));
            }

            StringBuilder jsMarkers = new StringBuilder();
            for (Client client : clientLocations) {
                String iconPath = "imgs/" + client.getOs().toLowerCase();
                if (client.isLoadedFromDb())
                    iconPath += "_gray";

                jsMarkers.append("marker = new google.maps.Marker({" + indent + "position: {lat: "
                        + client.getGeoData().getLat() + ", lng: " + client.getGeoData().getLon() + " }," + indent + "map: map,title:'"
                        + client.getPcName() + "'," + indent + "icon: '" + iconPath + "_32.png'" + indent + "});" + indent);

                jsMarkers.append("markers.push(marker);");
                jsMarkers.append(indent + "marker.addListener('click', function() {" + indent
                        + "\tinfoWindow = new google.maps.InfoWindow({" + indent
                        + "\tmediaInstruction : '<div><h3>" + client.getPcName() + "</h3><h4>" + client.getGeoData().getCity()
                        + "(" + client.getGeoData().getCountry() + ")" + "</h4><p>OS :" + client.getOs() + "<br />Ip : "
                        + client.getGlobalIp() + "<br /> "
                        + "(Lat, Lon) : (" + client.getGeoData().getLat() + ", " + client.getGeoData().getLon() + ")<br />"
                        + "Loaded : " + client.getLoadedFrom().toString() + "</p></div>'"
                        + indent + "});" + indent
                        + "infoWindows.push(infoWindow);"
                        + "infoWindow.open(map, this);" + indent
                        + "});");
            }
            jsMarkers.append(indent);

            Pattern pattern2 = Pattern.compile(".*(GOOGLE_API_KEY).*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher2 = pattern2.matcher(sb.toString());

// key=GOOGLE_API_KEY&
            String modified = originalSourceCode.replace(matcher.group(2), jsMarkers.toString());
            //modified = modified.replace(matcher2.group(1), googleMapsKey);

            outMapWriter.print(modified);
            outMapWriter.close();

            //Should call .replace otherwise it won't find it
            //System.out.println(new File("html/index_final.html").getAbsolutePath().replace("\\", "/"));
            Desktop.getDesktop().browse(new URI(new File("./src/main/resources/html/index_final.html").getAbsolutePath().replace("\\", "/")));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void addListener(MapUiListener mapUiListener) {
        this.listener = mapUiListener;
    }

    @Override
    public MapUiListener getListener() {
        return listener;
    }

    @Override
    public IUiMediator getMediator() {
        return mediator;
    }

    @Override
    public void setMediator(IUiMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void resetState() {

    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public void dispose() {

    }

    public void setGoogleMapsKey(String googleMapsKey) {
        this.googleMapsKey = googleMapsKey;
    }

    public void setClientLocations(ArrayList<Client> locations) {
        this.clientLocations = locations;
    }
}
