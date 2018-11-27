package com.careydevelopment.highchartsdemo.controller;

import com.careydevelopment.highchartsdemo.Sale;
import com.careydevelopment.highchartsdemo.SaleRepository;
import com.careydevelopment.highchartsdemo.Data;
import com.careydevelopment.highchartsdemo.DataRepository;
import com.careydevelopment.highchartsdemo.Security.User;
import com.careydevelopment.highchartsdemo.Security.UserService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Row;

import javax.validation.Valid;
import java.io.*;
import java.util.*;


@Controller
public class ChartController {
    @Autowired
    SaleRepository saleRepository;
    private String fileLocation;

    @Autowired
    DataRepository dataRepository;

    @Autowired
    private UserService userService;


    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }
    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors())
        {
            return "registration"; }
        else
        {  userService.saveUser(user);
            model.addAttribute("message","User Account Created");
        }
        return "login"; }
    @RequestMapping("/login")
    public String index(){
        return"login"; }

    @GetMapping("/")
    public String homepage(Model model) {

        LinkedHashSet<String> months = new LinkedHashSet<>();
        for (Sale sale : saleRepository.findAll()) {
            months.add(sale.getDate());
        }
        model.addAttribute("months",months);
        return "homepage";
    }
//    @GetMapping("/uploadExcelFile")
//    public String upload(Model model){
//
//        return "fileupload";
//    }

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
        saleRepository.deleteAll();

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

        return "redirect:/";
    }
@GetMapping("/download")
public String showDownload(){

        return "redirect:/downloads/template.xlsx";
}
//    @GetMapping("/view")
//    public String view(Model model, String quater) {
//
//        model.addAttribute("sales", saleRepository.findAllByQuarter(quater));
//
//        return "chart";
//    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public String generate(@RequestParam String date, Model model) {
        String quarter=tellQuarterFromDate(date);
        model.addAttribute("date", date);
        model.addAttribute("quarter",quarter);
        model.addAttribute("sales",saleRepository.findAllByQuarter(quarter));
        LinkedHashSet<String> months = new LinkedHashSet<>();
        for (Sale sale : saleRepository.findAll()) {

            months.add(sale.getDate());
        }
        model.addAttribute("months",months);
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

        String quarter=tellQuarterFromDate(date);
        ArrayList<Sale> sales = saleRepository.findAllByQuarter(quarter);
        HashSet<String> names = new HashSet<>();
        LinkedHashSet<String> months = new LinkedHashSet<>();
        ArrayList<Data> dataList = new ArrayList<>();
        for (Sale sale : sales) {
            names.add(sale.getName());
            months.add(sale.getDate());
        }
        String[] monthsArray=new String[3];
        Iterator<String> it = months.iterator();
        int i=0;
        while(it.hasNext()) {
            monthsArray[i]=it.next();
            i++;
        }

        for(int j=0;j<monthsArray.length;j++){
            System.out.println(monthsArray[j]);
        }
        for (String name : names) {
            Data data = new Data();
            data.setMonths(monthsArray);
            ArrayList<Sale> salesByName = saleRepository.findAllByQuarterAndName(quarter, name);
            ArrayList<Double> quarterData = new ArrayList<Double>();
            Double[] arr = new Double[3];

         for (Sale sale : salesByName) {
                data.setName(name);
                Double amount = sale.getY();
                quarterData.add(amount);
                arr = quarterData.toArray(arr);
                data.setData(arr);
            }

            dataList.add(data);
        }
        return dataList;
    }
//    @RequestMapping(value = "/view", method = RequestMethod.GET)
//    public String viewByQuarter(@RequestParam String date,Model model) {
//        String quarter= tellQuarterFromDate(date);
//
//        model.addAttribute("sales",saleRepository.findAllByQuarter(quarter));
//
//        return "chart";
//    }

    public String tellQuarterFromDate(String date){
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

        return quarter;
    }
}


