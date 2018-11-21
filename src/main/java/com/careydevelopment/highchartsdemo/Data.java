package com.careydevelopment.highchartsdemo;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.HashSet;

@Entity
public class Data {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private Double[] data;
    private HashSet<String> months;

    public Data() {
        months=new HashSet<String>();
    }

    public Data(String name, Double[] data) {
        this.name = name;
        this.data = data;
        months=new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double[] getData() {
        return data;
    }

    public void setData(Double[] data) {
        this.data = data;
    }

    public HashSet<String> getMonths() {
        return months;
    }

    public void setMonths(HashSet<String> months) {
        this.months = months;
    }
}
