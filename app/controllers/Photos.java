package controllers;

import java.io.File;

import models.Marker;
import models.Photo;
import models.User;

import org.bson.types.ObjectId;

import play.libs.Images;
import utils.FileUtils;
import utils.Secure;



public class Photos extends Application {
    /**
     * 需要独立的权限认证(swfupload使用flash和服务器端进行socket通信，session数据丢失)
     */
    public static void upload(File Filedata, String userId, String markerId) throws Exception {
        String fileName = System.currentTimeMillis() + "_" + Filedata.getName();
        User user = User.filter("_id", new ObjectId(userId)).first();
        // 存储到数据库
        Marker marker = Marker.filter("_id", new ObjectId(markerId)).first();
        if (marker != null && user != null) {
            Photo photo = new Photo(marker, fileName);
            photo.save();
            // 存储文件
            File file = new File(FileUtils.getApplicationPath("data") + fileName);
            Images.resize(Filedata, file, -1, 550);
            Filedata.delete();
        } else {
            Users.needLogin();
        }
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
