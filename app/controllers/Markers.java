package controllers;

import java.util.List;

import models.Marker;
import models.Photo;

import org.bson.types.ObjectId;

import play.Logger;
import utils.Secure;


public class Markers extends Application {
    @Secure(login = true)
    public static void add(String lng, String lat) {
        Logger.info(lng + " " + lat);
        Marker marker = new Marker(lng, lat);
        marker.save();
    }

    public static void list() {
        List<Marker> markers = Marker.findAll();
        StringBuilder builder = new StringBuilder("[");
        for (Marker marker : markers) {
            builder.append("{\"lng\":\"" + marker.lng + "\",\"lat\":\"" + marker.lat
                    + "\",\"title\":\"" + marker.title + "\"},");
        }
        builder.append("]");
        renderJSON(builder.toString().replace("},]", "}]"));
    }

    public static void open(String lng, String lat) {
        Marker m = Marker.filter("lng", lng).filter("lat", lat).first();
        Photo photo = Photo.filter("marker", m).first();
        String markerId = "\"id\":\"" + m.getId() + "\"";
        String photoName = photo == null ? "" : photo.name;
        String data =
                "{" + markerId + ",\"photo\":\"" + photoName + "\",\"title\":\"" + m.title + "\"}";
        Logger.info(data);
        renderJSON(data);
    }

    @Secure(login = true)
    public static void saveTitle(String title, String markerId) {
        Marker m = Marker.filter("_id", new ObjectId(markerId)).first();
        m.title = title;
        m.save();
    }
}
