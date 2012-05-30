package models;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity
public class Photo extends Model {
    public String name;
    @Reference
    public Marker marker;

    public Photo(Marker marker, String name) {
        this.marker = marker;
        this.name = name;
    }
}
