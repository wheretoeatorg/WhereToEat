
package com.wheretoeat.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "favorites")
public class FavoriteRestaurant extends Model {
    @Column(name = "name")
    private String name;
    @Column(name = "categories")
    private String categories;
    @Column(name = "distance")
    private double distanceMiles;
    @Column(name = "ratings")
    private float ratings;
    @Column(name = "resRef")
    private String resRef;
    @Column(name = "resId")
    private String resId;
    @Column(name = "latit")
    private double lattitude;
    @Column(name = "longi")
    private double longitude;
    @Column(name = "note")
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FavoriteRestaurant() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public double getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public String getResRef() {
        return resRef;
    }

    public void setResRef(String resRef) {
        this.resRef = resRef;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
