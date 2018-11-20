package com.careydevelopment.highchartsdemo.controller;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SalesPerson {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private Double[] quarterSales;

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

    public Double[] getQuarterSales() {
        return quarterSales;
    }

    public void setQuarterSales(Double[] quarterSales) {
        this.quarterSales = quarterSales;
    }
}
