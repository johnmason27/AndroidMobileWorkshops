package uk.ac.wlv.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    private String suspect;

    public Crime() {
        this.id = UUID.randomUUID();
        this.date = new Date();
    }

    public Crime(UUID id) {
        this.id = id;
        this.date = new Date();
    }

    public UUID getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return this.solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    public String getSuspect() {
        return this.suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }
}
