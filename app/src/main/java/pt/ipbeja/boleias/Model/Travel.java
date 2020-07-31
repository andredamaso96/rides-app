package pt.ipbeja.boleias.Model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "travels")
public class Travel {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String nameUser;
    private String date;
    private String hour;
    private String numberPhone;
    private String cityFromName;
    private String cityToName;

    @Embedded
    private Coordinates from;
    @Embedded
    private CoordinatesTo to;

    @Ignore
    public Travel(){

    }

    public Travel(long id, String nameUser, String date, String hour, String numberPhone, String cityFromName, String cityToName, Coordinates from, CoordinatesTo to) {
        this.id = id;
        this.nameUser = nameUser;
        this.date = date;
        this.hour = hour;
        this.numberPhone = numberPhone;
        this.cityFromName = cityFromName;
        this.cityToName = cityToName;
        this.from = from;
        this.to = to;
    }

    @Ignore
    public Travel(String nameUser, String date, String hour, String numberPhone, String cityFromName, String cityToName, Coordinates from, CoordinatesTo to) {
        this(0, nameUser, date, hour, numberPhone, cityFromName, cityToName, from, to);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getCityFromName() {
        return cityFromName;
    }

    public void setCityFromName(String cityFromName) {
        this.cityFromName = cityFromName;
    }

    public String getCityToName() {
        return cityToName;
    }

    public void setCityToName(String cityToName) {
        this.cityToName = cityToName;
    }

    public Coordinates getFrom() {
        return from;
    }

    public void setFrom(Coordinates from) {
        this.from = from;
    }

    public CoordinatesTo getTo() {
        return to;
    }

    public void setTo(CoordinatesTo to) {
        this.to = to;
    }
}
