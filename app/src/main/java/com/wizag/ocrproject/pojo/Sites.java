package com.wizag.ocrproject.pojo;

public class Sites {
    int id;
    String name, description;

    Double lt,ld;

    public Sites() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLt() {
        return lt;
    }

    public void setLt(Double lt) {
        this.lt = lt;
    }

    public Double getLd() {
        return ld;
    }

    public void setLd(Double ld) {
        this.ld = ld;
    }
}
