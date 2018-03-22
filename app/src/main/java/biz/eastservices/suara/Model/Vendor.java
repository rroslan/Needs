package biz.eastservices.suara.Model;

/**
 * Created by reale on 3/19/2018.
 */

public class Vendor {
    private String businessName;
    private String businessDescription;
    private boolean isWorking;
    private String category;
    private String avatarUrl;

    public Vendor() {
    }

    public Vendor(String businessName, String businessDescription, boolean isWorking, String category, String avatarUrl) {
        this.businessName = businessName;
        this.businessDescription = businessDescription;
        this.isWorking = isWorking;
        this.category = category;
        this.avatarUrl = avatarUrl;
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

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
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
}
