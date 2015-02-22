package com.pathogenstudios.fitbark;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String name;

    public User(JSONObject json) throws JSONException {
        id = json.getInt("id");
        username = json.getString("username");
        firstName = json.getString("first_name");
        lastName = json.getString("last_name");
        name = json.getString("name");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
}
