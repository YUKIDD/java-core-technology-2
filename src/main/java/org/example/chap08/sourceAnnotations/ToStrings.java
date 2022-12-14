package org.example.chap08.sourceAnnotations;

import org.example.chap08.rect.Point;
import org.example.chap08.rect.Rectangle;

public class ToStrings {
    public static String toString(Point obj) {
        var result = new StringBuilder();
        result.append("[");
        result.append(toString(obj.getX()));
        result.append(",");
        result.append(toString(obj.getY()));
        result.append("]");
        return result.toString();
    }
    public static String toString(Rectangle obj) {
        var result = new StringBuilder();
        result.append("rect.Rectangle");
        result.append("[");
        result.append(toString(obj.getTopLeft()));
        result.append(",");
        result.append("width=");
        result.append(toString(obj.getWidth()));
        result.append(",");
        result.append("height=");
        result.append(toString(obj.getHeight()));
        result.append("]");
        return result.toString();
    }
    public static String toString(Object obj) {
        return java.util.Objects.toString(obj);
    }
}