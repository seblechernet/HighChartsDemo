package com.careydevelopment.highchartsdemo.controller;

import com.careydevelopment.highchartsdemo.Sale;
import com.careydevelopment.highchartsdemo.SaleRepository;
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
import java.util.Arrays;
import java.util.List;


@Controller
public class ChartController {
    @Autowired
    SaleRepository saleRepository;
    private String fileLocation;

    @GetMapping("/")
    public String upload(){


        return "fileupload";
    }



    @PostMapping("/uploadExcelFile")
    public String uploadFile(Model model,@RequestParam MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());


        String excelFilepath= file.getOriginalFilename();
        FileInputStream input = new FileInputStream(new File(excelFilepath));

        Workbook workbook=new XSSFWorkbook(input);
        Sheet sheet=workbook.getSheetAt(0);

        Row row;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            Sale sale=new Sale();
            long id = (long) row.getCell(0).getNumericCellValue();
            sale.setId(id);
            String salesPerson = row.getCell(1).getStringCellValue();
            sale.setSalesPerson(salesPerson);
            String item = row.getCell(2).getStringCellValue();
            sale.setItem(item);
            int quantity = (int) row.getCell(3).getNumericCellValue();
            sale.setQuantity(quantity);
            double amount = row.getCell(4).getNumericCellValue();
            sale.setAmount(amount);
            String date=row.getCell(5).getStringCellValue();
            sale.setDate(date);
            System.out.println(id +"\t\t\t"+ salesPerson +"\t\t\t"+item+ "\t\t\t"+quantity + "\t\t\t"+amount+"\t\t\t"+date);
            saleRepository.save(sale);
        }

        input.close();
        System.out.println("Success import excel to mysql table");

        return "redirect:/view";
    }
    @GetMapping("/view")
    public String view(Model model){
        model.addAttribute("sales",saleRepository.findAll());


        return "view";
    }

    @RequestMapping(value = "/chart", method= RequestMethod.GET)
    public String chart(Model model,String date) {
        
//        //first, add the regional sales
//        Integer northeastSales = 17089;
//        Integer westSales = 10603;
//        Integer midwestSales = 5223;
//        Integer southSales = 10111;
//        Integer some =1245678;
//
//        model.addAttribute("northeastSales", northeastSales);
//        model.addAttribute("southSales", southSales);
//        model.addAttribute("midwestSales", midwestSales);
//        model.addAttribute("westSales", westSales);

        model.addAttribute("marty",saleRepository.findBySalesPersonAndDate("Marty",date));
        model.addAttribute("mai",saleRepository.findBySalesPersonAndDate("Mai",date));
        model.addAttribute("seble",saleRepository.findBySalesPersonAndDate("Seble",date));
        model.addAttribute("shristi",saleRepository.findBySalesPersonAndDate("Shristi",date));
        model.addAttribute("rediet",saleRepository.findBySalesPersonAndDate("Rediet",date));

        //now add sales by lure type
        List<Integer> inshoreSales = Arrays.asList(4074, 3455, 4112);
        List<Integer> nearshoreSales = Arrays.asList(3222, 3011, 3788);
        List<Integer> offshoreSales = Arrays.asList(7811, 7098, 6455);

        model.addAttribute("inshoreSales", inshoreSales);
        model.addAttribute("nearshoreSales", nearshoreSales);
        model.addAttribute("offshoreSales", offshoreSales);
        
        return "chart";
    }
    
    
    //redirect to demo if user hits the root
//    @RequestMapping("/")
//    public String home(Model model) {
//        return "redirect:chart";
//    }
}
