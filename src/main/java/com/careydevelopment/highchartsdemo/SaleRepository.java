package com.careydevelopment.highchartsdemo;

import org.springframework.data.repository.CrudRepository;

import javax.sql.rowset.CachedRowSet;

public interface SaleRepository extends CrudRepository<Sale,Long> {
Sale findBySalesPersonAndDate(String name,String date);
}
