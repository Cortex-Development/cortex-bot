package me.kodysimpson.cortexbot.model.infractions;

import java.time.LocalDateTime;

public class Infraction {

    private String reason;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;

    public Infraction(){

    }

    public Infraction(LocalDateTime expireDate, String reason){
        this.expireDate = expireDate;
        this.reason = reason;
        this.startDate = LocalDateTime.now();
    }

    public Infraction(LocalDateTime expireDate){
        this.expireDate = expireDate;
        this.startDate = LocalDateTime.now();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
