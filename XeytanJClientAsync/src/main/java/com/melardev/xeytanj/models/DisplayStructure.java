package com.melardev.xeytanj.models;

import java.awt.Point;
import java.awt.Rectangle;

public class DisplayStructure {

	public static boolean allDisplays = false;
	public String name;
	public int minX;
	public int maxX;
	public int minY;
	public int maxY;
	public boolean active;
	public int width;
	public int height;

	public DisplayStructure(String name, int minX, int minY, int maxX, int maxY) {
		super();
		this.name = name;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		width = maxX - minX;
		height = maxY - minY;
		active = false;
	}

	public DisplayStructure(String _name, Point minPoint, Point maxPoint) {
		this(_name, minPoint.x, minPoint.y, maxPoint.x, maxPoint.y);

	}

	public DisplayStructure(String _name, Rectangle rectangle) {
		this(_name, (int) rectangle.getMinX(), (int) rectangle.getMinY(), (int) rectangle.getMaxX(),
				(int) rectangle.getMaxY());

	}

}
