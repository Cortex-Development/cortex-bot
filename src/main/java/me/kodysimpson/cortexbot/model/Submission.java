package me.kodysimpson.cortexbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Submission {

    @Id
    private String id;

    private String userid;
    private Date date;

}
