package com.careydevelopment.highchartsdemo;

import org.springframework.data.repository.CrudRepository;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.List;

public interface SaleRepository extends CrudRepository<Sale,Long>{

Sale findBySalesPersonAndDate(String name,String date);
Sale findByDate(String date);
Sale findBySalesPerson(String name);
ArrayList<Sale> findAll();
}
