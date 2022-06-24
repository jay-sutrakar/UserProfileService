package models;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private String email;
    private String city;
    private String deviceId;
    private boolean makePublic;
}
