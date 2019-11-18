package edu.wit.mobilehealth.mobilehealthmassage;

public class point {
    private String pointName;
    private String description;

    public point(String pointName, String description) {
        this.description = description;
        this.pointName = pointName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
