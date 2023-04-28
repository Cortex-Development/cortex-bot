package dev.cortex.cortexbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
public class Thanked {

    @Id
    private String id;

    private String personThanked;
    private String thankBy;
    private Date when;
    private long points;
    private String reason;

}
