package com.careydevelopment.highchartsdemo;

import com.careydevelopment.highchartsdemo.controller.SalesPerson;
import org.springframework.data.repository.CrudRepository;

public interface SalesPersonRepository extends CrudRepository<SalesPerson,Long> {


}
