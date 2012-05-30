package models;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;

@Entity
public class User extends Model {
    public String username;
    public String password;
}
