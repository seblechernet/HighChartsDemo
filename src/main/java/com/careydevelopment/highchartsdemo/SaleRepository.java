package com.careydevelopment.highchartsdemo;

import org.springframework.data.repository.CrudRepository;

import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.List;

public interface SaleRepository extends CrudRepository<Sale,Long>{

Sale findByNameAndDate(String name,String date);
ArrayList<Sale> findAllByDate(String date);
Sale findByName(String name);
ArrayList<Sale> findAll();
ArrayList<Sale> findAllByQuarter(String q);
ArrayList<Sale> findAllByQuarterAndName(String q,String name);
ArrayList<Sale> findAllByQuarterAndDate(String q,String date);

}
