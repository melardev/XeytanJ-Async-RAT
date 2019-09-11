package com.melardev.xeytanj.maps;

/*
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.melardev.xeytaj.XeytanApplication;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;

public class MapsFX extends Application implements MapComponentInitializedListener {

	private XeytanApplication appInstance;
	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	protected DirectionsPane directions;

	private Button btnZoomIn;
	private Button btnZoomOut;
	private Label lblZoom;
	private Label lblCenter;
	private ComboBox<MapTypeIdEnum> mapTypeCombo;

	private Button btnShowAll;
	private Button btnShowOnline;
	private static ArrayList<ClientGeoStructure> clientCoordinates;

	//Not used but if not created we will getProperty an errors
	public MapsFX() {

	}

	public MapsFX(XeytanApplication xeytanApplication) {
		appInstance = xeytanApplication;
	}

	@Override
	public void start(Stage stage) {

		mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), null);
		mapComponent.addMapInializedListener(this);

		BorderPane bp = new BorderPane();
		ToolBar tb = new ToolBar();

		btnZoomIn = new Button("Zoom In");
		btnZoomIn.setOnAction(e -> {
			map.zoomProperty().set(map.getZoom() + 1);
		});
		btnZoomIn.setDisable(true);

		btnZoomOut = new Button("Zoom Out");
		btnZoomOut.setOnAction(e -> {
			map.zoomProperty().set(map.getZoom() - 1);
		});
		btnZoomOut.setDisable(true);

		lblZoom = new Label();
		lblCenter = new Label();

		mapTypeCombo = new ComboBox<>();
		mapTypeCombo.setOnAction(e -> {
			map.setMapType(mapTypeCombo.getSelectionModel().getSelectedItem());
		});
		mapTypeCombo.setDisable(true);

		Button btnType = new Button("Map type");
		btnType.setOnAction(e -> {
			map.setMapType(MapTypeIdEnum.HYBRID);
		});

		btnShowAll = new Button("Show All");
		btnShowAll.setOnAction(e -> {
			showAll();
		});

		btnShowOnline = new Button("Online Only");
		btnShowOnline.setOnAction(e -> {
			showMarkersOnline();
		});

		tb.getItems().addAll(btnZoomIn, btnZoomOut, mapTypeCombo, new Label("Zoom: "), lblZoom, new Label("Center: "),
				lblCenter, btnShowAll, btnShowOnline);

		bp.setTop(tb);

		bp.setCenter(mapComponent);

		Scene scene = new Scene(bp);
		stage.setScene(scene);
		stage.show();
	}

	private void showMarkersOnline() {

	}

	private void showAll() {
		System.out.println();

	}

	@Override
	public void mapInitialized() {
		LatLong center = new LatLong(40.4172, -3.684);
		mapComponent.addMapReadyListener(() -> {
			// This call will fail unless the map is completely ready.
			checkCenter(center);
		});

		MapOptions options = new MapOptions();
		options.center(center).mapMarker(true).zoom(9).overviewMapControl(false).panControl(false).rotateControl(false)
				.scaleControl(false).streetViewControl(false).zoomControl(false).mapType(MapTypeIdEnum.TERRAIN);

		map = mapComponent.createMap(options, false);
		directions = mapComponent.getDirec();
		//map.showDirectionsPane();
		map.setHeading(123.2);
		//        System.out.println("Heading is: " + map.getHeading() );

		if (clientCoordinates != null) {
			for (ClientGeoStructure client : clientCoordinates) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLong(client.getLat(), client.getLon())).title(client.getTitle())
						.animation(Animation.DROP).visible(true);
				Marker marker = new Marker(markerOptions);

				client.setMarkerOptions(markerOptions);
				client.setMarker(marker);

				map.addMarker(client.getMarker());
			}
		}
		map.fitBounds(new LatLongBounds(new LatLong(30, 120), center));
		//        System.out.println("Bounds : " + map.getBounds());

		lblCenter.setText(map.getCenter().toString());
		map.centerProperty().addListener((ObservableValue<? extends LatLong> obs, LatLong o, LatLong n) -> {
			lblCenter.setText(n.toString());
		});

		lblZoom.setText(Integer.toString(map.getZoom()));
		map.zoomProperty().addListener((ObservableValue<? extends Number> obs, Number o, Number n) -> {
			lblZoom.setText(n.toString());
		});

		btnZoomIn.setDisable(false);
		btnZoomOut.setDisable(false);
		mapTypeCombo.setDisable(false);

		mapTypeCombo.getItems().addAll(MapTypeIdEnum.ALL);
	}

	private void checkCenter(LatLong center) {

	}

	public void display(ArrayList<ClientGeoStructure> coordinates) {
		if (coordinates != null)
			clientCoordinates = coordinates;

		Application.launch(null);
	}
}
*/