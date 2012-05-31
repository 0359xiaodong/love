package controllers;

import models.User;

import org.bson.types.ObjectId;

import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import utils.Secure;

public class Application extends Controller {

    public static void index() {
        render();
    }

    @Before
    public static void login() {
        Secure secure = getActionAnnotation(Secure.class);
        Logger.info("login checking , user id = " + session.get("USER_ID"));
        if (secure != null && secure.login()) {
            if (session.get("USER_ID") == null) {
                Logger.info("user id is null , need login");
                Users.needLogin();
            }
            String userId = session.get("USER_ID").toString();
            User user = User.filter("_id", new ObjectId(userId)).first();
            if (user == null) {
                Logger.info("user is null , need login");
                Users.needLogin();
            }
        }
    }
}
