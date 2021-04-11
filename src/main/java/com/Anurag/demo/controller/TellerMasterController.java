package com.Anurag.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.Anurag.demo.dao.AdminMasterRepo;
import com.Anurag.demo.dao.TellerMasterDao;
import com.Anurag.demo.dao.TellerMasterRepo;
import com.Anurag.demo.dto.Teller2Response;
import com.Anurag.demo.dto.TellerAndLocationResponse;

@Controller
public class TellerMasterController {
	TellerAndLocationResponse GlobalTellerAndLocationDetails;
	
	ArrayList<Teller2Response> TellerReportExcel;
	
	@Autowired
	TellerMasterDao dao;
	
	@Autowired
	TellerMasterRepo repo;
	
	@Autowired
	AdminMasterRepo repo2;
	
	
	@RequestMapping("/")
	public String LoginPage() {
		
		return "Login.jsp";
	}
	
	/*----------------------------------------TellerLogin------------------------------------------*/
	@RequestMapping("/TellerLogin")         
	public ModelAndView TellerLogin(@RequestParam String tid,@RequestParam String tpass,HttpSession session) {
		
		if(dao.findByTeller(tid, tpass)>0) {

			
			TellerAndLocationResponse TellerAndLocationDetails=dao.getTellerAndLocationInfo(tid); 
			ModelAndView mv=new ModelAndView("TellerLogin.jsp");
			
			
			  System.out.println(TellerAndLocationDetails); 
			  GlobalTellerAndLocationDetails=TellerAndLocationDetails; 
			  
			  session.setAttribute("TellerAndLocationDetails",TellerAndLocationDetails);

			  char LocationLetter=TellerAndLocationDetails.getLid().charAt(0);
			  System.out.println(LocationLetter);
			  
			  session.setAttribute("LocationLetter",LocationLetter);	
			  session.setAttribute("tid",tid);
			  session.setAttribute("repo", repo);
			  session.setAttribute("repo2", repo2);
			  session.setAttribute("location_lid", TellerAndLocationDetails.getLid());
			  return mv;
		}
		else {
			String msg="Try Again";
			ModelAndView mv=new ModelAndView("redirect:Login.jsp");
			mv.addObject(msg);
			return mv;
		}
		
	}
	
	/*----------------------------------------TellerBill------------------------------------------*/
	              
	
	@RequestMapping("/TellerBill")
	public String bill(@RequestParam String cname,@RequestParam String cpno,@RequestParam(value = "pack", required = false) String pack,@RequestParam(value = "single", required = false) String single,@RequestParam(value = "discount", defaultValue = "0",required = false) int discount,@RequestParam(value = "gst", defaultValue = "0",required = false) int gst, @RequestParam(value = "optionalGST",required = false) String optionalGST, HttpServletRequest request,HttpSession session) {
	
		ArrayList<String> servicesProvided = new ArrayList<String>();
		
		System.out.println(cname+" "+cpno+" "+pack+" "+single+" "+discount);
		
		session.setAttribute("cname", formatter(cname));
		session.setAttribute("cpno", cpno);
		session.setAttribute("tname", formatter(GlobalTellerAndLocationDetails.getTname()));
		session.setAttribute("lpno", GlobalTellerAndLocationDetails.getLpno());
		session.setAttribute("teller", GlobalTellerAndLocationDetails);
		
		 repo.customerInsert(cname, cpno,GlobalTellerAndLocationDetails.getTid());
		 
		 System.out.println("last value is "+ repo.getCid());
		 int cid=repo.getCid();
		 
		 
		Date date=dao.getDate(cid);
		System.out.println("date is" +date);
		
		session.setAttribute("date", date);
		
		 
		    if(pack!=null) { 
			  
		      repo.historyInsert(pack, cid, discount,gst);
			  servicesProvided.add(pack);
			  
			  }else {
			  
			  String[] selected = request.getParameterValues("single");
			  
			  for(String i :selected) {
			  
			  repo.historyInsert(i, cid, discount,gst);
			  servicesProvided.add(i);
			  
			  } }
		
		    if(gst==0) {
		    	session.setAttribute("optionalGST", optionalGST);
		    }else {
		    	session.setAttribute("optionalGST", "G12345");
		    }
		    
		    session.setAttribute("servicesProvided", servicesProvided);
		    session.setAttribute("discount", discount);
		    session.setAttribute("gst", gst);
		    session.setAttribute("lname", formatter(GlobalTellerAndLocationDetails.getLname()));
		    
		return "TellerBill.jsp";
	}
	public String formatter(String str){  
	    String words[]=str.split("\\s");  
	    String capitalizeWord="";  
	    for(String w:words){  
	        String first=w.substring(0,1);  
	        String afterfirst=w.substring(1);  
	        capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	    }  
	    return capitalizeWord.trim();  
	
	}	
	
	/*---------------------------------GoBack------------------------------------------------*/
	
	@RequestMapping("/GoBack")
	public String GoBack() {
		
		return "TellerLogin.jsp";
	}

	/*---------------------------------logout------------------------------------------------*/
	
	@RequestMapping("/logout")
	public String LogOut() {
		
		return "Login.jsp";
	}
	
	/*------------------------------------------TellerReport------------------------------------------*/
	
	@RequestMapping("/TellerReport")
	public String Teller2(@RequestParam String date1,@RequestParam String date2,HttpSession session) {
		
		System.out.println(date1+"    "+date2);
		ArrayList<Teller2Response> TellerReportDates=repo.getTeller2(date1,date2);
		TellerReportExcel=TellerReportDates;
		
		
		for(Teller2Response i: TellerReportDates) {
			System.out.println(i);
		}
	
		session.setAttribute("repo",repo);
		session.setAttribute("TellerReportDates", TellerReportDates);
		return "TellerReport.jsp";
		
	}
	
	@RequestMapping("/update")
	public String update(@RequestParam String new1,@RequestParam String confirm) {
		
		if(new1.equals(confirm)) {
			System.out.println("yes buddy they are same ... ");
			String id=GlobalTellerAndLocationDetails.getTid();
			repo.updatePassword(id, new1);
		}
		
	return "Login.jsp";	
	}
	
	
	
	@RequestMapping("/TellerReportExcel")
	public String TellerReportExcel() {
		
		
		//--------------------------------------------------
		
		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		   LocalDateTime now = LocalDateTime.now();
		   String appendingDate=dtf.format(now);
		   System.out.println();		
		
		
		//---------------------------------------------------
		
		File home = FileSystemView.getFileSystemView().getHomeDirectory();
		String path=home.getAbsolutePath();
		String[] list1=path.replaceAll(Pattern.quote("\\"),"\\\\").split("\\\\");
		String stri="";
		for(String i :list1) {
			stri=stri+i+"/";
		}
		stri=stri+GlobalTellerAndLocationDetails.getTname()+" Report "+appendingDate+".xlsx";
		System.out.println(stri);
		
		
		try {
			
			//create an .xlsx format workbook
			Workbook workbook=new XSSFWorkbook();
			
			//create a new sheet in workbook
			Sheet sh=workbook.createSheet("ExcelTellerReport");
			
			//create top row with column headings
			String[] columnHeaders= {"Customer Id","Customer Name","Customer Phno","JobName","JobPrice","Discount","GST","Amount","Date"};
			
			//want to make header bold with foreground color
			Font headerFont=workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short)12);
			headerFont.setColor(IndexedColors.BLACK.index);
			
			
			//create a cellstyle with a font
			CellStyle headerStyle=workbook.createCellStyle();
			headerStyle.setFont(headerFont);
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
			
			//create a row
			Row headerRow=sh.createRow(0);
			
			//iterate over the column headings to crate columns
			for(int i=0;i<columnHeaders.length;i++) {
				Cell cell=headerRow.createCell(i);
				cell.setCellValue(columnHeaders[i]);
				cell.setCellStyle(headerStyle);
			}
			
			//get data and fill the data 
			ArrayList<Teller2Response> a=TellerReportExcel;
			System.out.println(a);
			int rownum=1;
			for(Teller2Response i:a) {
				Row row=sh.createRow(rownum++);
				row.createCell(0).setCellValue(i.getCid());
				row.createCell(1).setCellValue(i.getCname());
				row.createCell(2).setCellValue(i.getCpno());
				row.createCell(3).setCellValue(i.getJobname());
				row.createCell(4).setCellValue(i.getJobprice());
				row.createCell(5).setCellValue(i.getDiscount());
				row.createCell(6).setCellValue(i.getGst());
				row.createCell(7).setCellValue(i.getAmount());
								
				String[] temp=i.getDate().toString().split("-");
				String final1=temp[2]+"-"+temp[1]+"-"+temp[0];
				row.createCell(8).setCellValue(final1);
				
			}
			
			for(int i=0;i<columnHeaders.length;i++) {
				sh.autoSizeColumn(i);
			}
			Sheet sh2=workbook.createSheet("second");
			//write output to the file
			FileOutputStream fileout=new FileOutputStream(stri);
			workbook.write(fileout);
			fileout.close();
			workbook.close();
			System.out.println("completed");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		//------------------------------------------------------
		
		return "TellerReport.jsp";
		
	}
	
}
