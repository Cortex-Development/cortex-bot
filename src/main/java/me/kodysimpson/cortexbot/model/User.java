package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class User {

    /**
     * Unique database identifier
     */
    @Id
    private String id;

    /**
     * Unique username of the User
     */
    @NotBlank(message = "Username is empty")
    private String username;
    /**
     * Encoded password of the user
     */
    @NotBlank(message = "Password is empty")
    @Length(min = 8, message = "The password must be at least 8 characters")
    private String password;

    /**
     * Authorities(Roles) for Authorization
     */
    private List<String> authorities = new ArrayList<>();

    /**
     * First name of the account
     */
    @NotBlank(message = "First Name is empty")
    private String firstName;
    /**
     * Last name of the account
     */
    @NotBlank(message = "Last Name is empty")
    private String lastName;

    /**
     * Unique email of the account
     */
    @Email(message = "The provided email is invalid")
    @NotBlank(message = "Email is empty")
    private String email;

    /**
     * Unique ID of the user's discord account for discord account integration/connection
     */
    private String discordID;

    public User() {
        authorities.add("DEFAULT");
    }

}
