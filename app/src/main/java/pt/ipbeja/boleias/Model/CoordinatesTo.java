package pt.ipbeja.boleias.Model;

import androidx.room.Ignore;

public class CoordinatesTo {

    private double latitudeTo;
    private double longitudeTo;

    public CoordinatesTo(double latitudeTo, double longitudeTo) {
        this.latitudeTo = latitudeTo;
        this.longitudeTo = longitudeTo;
    }

    @Ignore
    public CoordinatesTo(){}

    public double getLatitudeTo() {
        return latitudeTo;
    }

    public void setLatitudeTo(double latitudeTo) {
        this.latitudeTo = latitudeTo;
    }

    public double getLongitudeTo() {
        return longitudeTo;
    }

    public void setLongitudeTo(double longitudeTo) {
        this.longitudeTo = longitudeTo;
    }

    public boolean isValid() {
        return (latitudeTo >= -90 && latitudeTo <= 90) && (longitudeTo >= -180 && longitudeTo <= 180);
    }
}
