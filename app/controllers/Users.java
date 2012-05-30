package controllers;

import models.User;
import play.data.validation.Required;

public class Users extends Application {
    public static void create(@Required String username, @Required String password,
            @Required String password_confirm) {
        User user = User.filter("username", username).first();
        if (user == null && password != null && password.equals(password_confirm)) {
            User u = new User();
            u.username = username;
            u.password = password;
            u.save();
            session.put("USER_ID", u.getId());
            session.put("USERNAME", username);
        } else if (user != null) {
            forbidden("账号已存在或密码输入不匹配");
        }
        Application.index();
    }

    public static void logout() {
        session.clear();
        Application.index();
    }

    public static void login(@Required String username, @Required String password) {
        User u = User.filter("username", username).filter("password", password).first();
        if (u != null) {
            session.put("USER_ID", u.getId());
            session.put("USERNAME", username);
        }
        Application.index();
    }

    public static void checkUser(String username) {
        User user = User.filter("username", username).first();
        if (user != null) {
            renderJSON("{\"exist\":true}");
        } else {
            renderJSON("{\"exist\":false}");
        }
    }
}
