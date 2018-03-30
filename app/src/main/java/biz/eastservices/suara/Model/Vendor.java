package biz.eastservices.suara.Model;

/**
 * Created by reale on 3/19/2018.
 */

public class Vendor {
    private String businessName;
    private String businessDescription;
    private String phone;
    private String category;
    private String avatarUrl;
    private String website;

    private double lat,lng;

    public Vendor() {
    }

    public Vendor(String businessName, String businessDescription, String phone, String category, String avatarUrl, String website, double lat, double lng) {
        this.businessName = businessName;
        this.businessDescription = businessDescription;
        this.phone = phone;
        this.category = category;
        this.avatarUrl = avatarUrl;
        this.website = website;
        this.lat = lat;
        this.lng = lng;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
