package eu.dbortoluzzi.consumer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class GeoLocation {
    private String lat;
    private String lng;

    @JsonCreator
    public GeoLocation(@JsonProperty("lat") String lat, @JsonProperty("lng")String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" "); // Use 'space' as the delimiter
        joiner.add(lat)
                .add(lng);

        return joiner.toString();
    }
}
