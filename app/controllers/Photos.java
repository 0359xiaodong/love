package controllers;

import java.io.File;

import models.Marker;
import models.Photo;

import org.bson.types.ObjectId;

import play.Logger;
import utils.FileUtils;
import utils.Secure;



public class Photos extends Application {
    @Secure(login = true)
    public static void upload(File Filedata, String sessionId, String markerId) throws Exception {
        String fileName = System.currentTimeMillis() + "_" + Filedata.getName();
        // 存储到数据库
        Logger.info(markerId);
        Marker marker = Marker.filter("_id", new ObjectId(markerId)).first();
        Photo photo = new Photo(marker, fileName);
        photo.save();
        // 存储文件
        File file = new File(FileUtils.getApplicationPath("data") + fileName);
        Filedata.renameTo(file);
        Filedata.delete();
    }

    @Secure(login = true)
    public static void delete(String name, String markerId) {
        Marker m = Marker.filter("_id", new ObjectId(markerId)).first();
        Photo p = Photo.filter("name", name).first();
        new File(FileUtils.getApplicationPath("data") + name).delete();
        String nextImage = "";
        Photo next = Photo.filter("marker", m).filter("name > ", name).order("name").first();
        Photo prev = Photo.filter("marker", m).filter("name < ", name).order("-name").first();
        if (next != null) {
            nextImage = next.name;
        } else if (prev != null) {
            nextImage = prev.name;
        }
        p.delete();
        renderJSON("{\"name\":\"" + nextImage + "\"}");
    }

    public static void prev(String name, String markerId) {
        Marker m = Marker.filter("_id", new ObjectId(markerId)).first();
        Photo prev = Photo.filter("marker", m).filter("name < ", name).order("-name").first();
        if (prev == null) {
            renderJSON("{\"name\":\"" + name + "\"}");
        } else {
            renderJSON("{\"name\":\"" + prev.name + "\"}");
        }
    }

    public static void next(String name, String markerId) {
        Marker m = Marker.filter("_id", new ObjectId(markerId)).first();
        Photo next = Photo.filter("marker", m).filter("name > ", name).order("name").first();
        if (next == null) {
            renderJSON("{\"name\":\"" + name + "\"}");
        } else {
            renderJSON("{\"name\":\"" + next.name + "\"}");
        }

    }
}
