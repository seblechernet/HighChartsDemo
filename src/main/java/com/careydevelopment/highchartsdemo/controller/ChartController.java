package com.careydevelopment.highchartsdemo.controller;

import com.careydevelopment.highchartsdemo.Sale;
import com.careydevelopment.highchartsdemo.SaleRepository;
import com.careydevelopment.highchartsdemo.Data;
import com.careydevelopment.highchartsdemo.DataRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Row;
import java.io.*;
import java.util.*;


@Controller
public class ChartController {
    @Autowired
    SaleRepository saleRepository;
    private String fileLocation;

    @Autowired
    DataRepository dataRepository;

    @GetMapping("/")
    public String upload() {

        return "fileupload";
    }


    @PostMapping("/uploadExcelFile")
    public String uploadFile(Model model, @RequestParam MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());


        InputStream input = file.getInputStream();
        File directory = new File(".");
        String path = directory.getAbsolutePath();
        fileLocation = path.substring(0,path.length() - 1) + file.getOriginalFilename();
        FileOutputStream f = new FileOutputStream(new File(fileLocation));

/*        String excelFilepath = file.getOriginalFilename();
        FileInputStream input = new FileInputStream(new File(excelFilepath));*/

        Workbook workbook = new XSSFWorkbook(input);
        Sheet sheet = workbook.getSheetAt(0);

        Row row;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            row = sheet.getRow(i);
            Sale sale = new Sale();
            long id = (long) row.getCell(0).getNumericCellValue();
            sale.setId(id);
            String salesPerson = row.getCell(1).getStringCellValue();
            sale.setName(salesPerson);
            String item = row.getCell(2).getStringCellValue();
            sale.setItem(item);
            int quantity = (int) row.getCell(3).getNumericCellValue();
            sale.setQuantity(quantity);
            double amount = row.getCell(4).getNumericCellValue();
            sale.setY(amount);
            String date = row.getCell(5).getStringCellValue();
            sale.setDate(date);
            if (date.equalsIgnoreCase("january") || date.equalsIgnoreCase("february") || date.equalsIgnoreCase("march")) {
                sale.setQuarter("q1");

            }
            if (date.equalsIgnoreCase("april") || date.equalsIgnoreCase("may") || date.equalsIgnoreCase("june")) {
                sale.setQuarter("q2");
            }
            if (date.equalsIgnoreCase("july") || date.equalsIgnoreCase("august") || date.equalsIgnoreCase("september")) {
                sale.setQuarter("q3");
            }
            if (date.equalsIgnoreCase("october") || date.equalsIgnoreCase("november") || date.equalsIgnoreCase("december")) {
                sale.setQuarter("q4");
            }
//            System.out.println(id + "\t\t\t" + salesPerson + "\t\t\t" + item + "\t\t\t" + quantity + "\t\t\t" + amount + "\t\t\t" + date);
            saleRepository.save(sale);
        }

        input.close();
        System.out.println("Success import excel to mysql table");

        return "redirect:/view";
    }

    @GetMapping("/view")
    public String view(Model model) {

        model.addAttribute("sales", saleRepository.findAll());

        return "view";
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public String generate(@RequestParam String date, Model model) {

//        List<Integer> martysales = Arrays.asList(4074, 3455, 4112);
//        List<Integer> maisales = Arrays.asList(3222, 3011, 3788);
//        List<Integer> seblesales = Arrays.asList(7811, 7098, 6455);
//        List<Integer> shristisales = Arrays.asList(7811, 7098, 6455);
//        List<Integer> redietsales = Arrays.asList(7811, 7098, 6455);
//
//        model.addAttribute("MartySales", martysales);
//        model.addAttribute("MaiSales", maisales);
//        model.addAttribute("SebleSales", martysales);
//        model.addAttribute("ShristiSales", martysales);
//        model.addAttribute("RedietSales", martysales);
////

        model.addAttribute("date", date);

        return "chart";
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public @ResponseBody
    ArrayList<Sale> monthlyReport(@RequestParam String date) {


        ArrayList<Sale> salesByMonth = saleRepository.findAllByDate(date);


        return salesByMonth;
    }

    @RequestMapping(value = "/quarter", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Data> quarterReport(@RequestParam String date) {
        String quarter="";
        if (date.equalsIgnoreCase("january") || date.equalsIgnoreCase("february") || date.equalsIgnoreCase("march")) {
            quarter="q1";

        }
        if (date.equalsIgnoreCase("april") || date.equalsIgnoreCase("may") || date.equalsIgnoreCase("june")) {
            quarter="q2";
        }
        if (date.equalsIgnoreCase("july") || date.equalsIgnoreCase("august") || date.equalsIgnoreCase("september")) {
            quarter="q3";
        }
        if (date.equalsIgnoreCase("october") || date.equalsIgnoreCase("november") || date.equalsIgnoreCase("december")) {
            quarter="q4";
        }

        ArrayList<Sale> sales = saleRepository.findAllByQuarter(quarter);

        HashSet<String> names = new HashSet<>();
        HashSet<String> months = new HashSet<>();
        ArrayList<Data> dataList = new ArrayList<>();
        for (Sale sale : sales) {
            names.add(sale.getName());
            months.add(sale.getDate());
        }

        for (String name : names) {
            Data data = new Data();
            ArrayList<Sale> salesByName = saleRepository.findAllByQuarterAndName(quarter, name);
            ArrayList<Double> quarterData = new ArrayList<Double>();
            Double[] arr = new Double[3];
//            ArrayList<String> theMonths = new ArrayList<String>();


            for (Sale sale : salesByName) {
                data.setName(name);
                Double amount = sale.getY();
                quarterData.add(amount);
                arr = quarterData.toArray(arr);
                data.setData(arr);

            }
            data.setMonths(months);

            dataList.add(data);

        }

        return dataList;
    }

}


