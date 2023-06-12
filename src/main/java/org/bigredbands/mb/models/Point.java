package org.bigredbands.mb.models;

import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Point extends Point2D.Float {
    public Point(float x, float y){
        super(x, y);
    }

    public float X() {
        return this.x;
    }

    public float Y() {
        return this.y;
    }

    public Point interpolate(Point b, float t) {
        return new Point(this.x*(1-t) + b.x*t, this.y*(1-t) + b.y*t);
    }

    public Point normalize() {
        return this.multiply(1.0f / (float) this.distance(0, 0));
    }

    public Point orthogonal() {
        return new Point(-this.y, this.x);
    }

    public Point multiply(float value) {
        return new Point(this.x * value, this.y * value);
    }

    public Point add(Point other) {
        return new Point(this.x + (float) other.getX(), this.y + (float) other.getY());
    }

    public Point subtract(Point other) {
        return this.add(other.multiply(-1.0f));
    }

    public void setPoint(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        //general comparisons
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        //field comparisons
        Point other = (Point) obj;
        if (Math.abs(x - other.x) > 0.1) {
            return false;
        }
        if (Math.abs(y - other.y) > 0.1) {
            return false;
        }
        return true;
    }
    public double distance(double x, double y){
        return Math.sqrt((this.x-x)*(this.x-x)+(this.y-y)*(this.y-y));
    }

    public Element convertToXML(Document document, String pointName) {
        //add the point parent tag
        Element pointTag = document.createElement(pointName);

        //add the x coordinate tag
        Element xTag = document.createElement(XMLConstants.X_COORDINATE);
        pointTag.appendChild(xTag);

        //add the coordinate value to the x coordinate tag
        Text xText = document.createTextNode(java.lang.Float.toString(x));
        xTag.appendChild(xText);

        //add the y coordinate tag
        Element yTag = document.createElement(XMLConstants.Y_COORDINATE);
        pointTag.appendChild(yTag);

        //add the coordinate value to the y coordinate tag
        Text yText = document.createTextNode(java.lang.Float.toString(y));
        yTag.appendChild(yText);

        return pointTag;
    }
}
