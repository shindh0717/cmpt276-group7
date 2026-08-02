package com.example.group.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Long userId;
    private int rating;
    public Rating() {}
    public Long getId()
    {
        return id;
    }
    public String getLocationName()
    {
        return locationName;
    }
    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }
    public Double getLatitude()
    {
        return latitude;
    }
    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }
    public Double getLongitude()
    {
        return longitude;
    }
    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }
    public Long getUserId()
    {
        return userId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    public Integer getRating()
    {
        return rating;
    }
    public void setRating(Integer rating)
    {
        this.rating = rating;
    }
}
