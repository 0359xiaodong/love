package models;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Marker extends Model {
    public String title;
    public String lng;
    public String lat;

    public Marker(String lng, String lat) {
        this.lat = lat;
        this.lng = lng;
    }
}
