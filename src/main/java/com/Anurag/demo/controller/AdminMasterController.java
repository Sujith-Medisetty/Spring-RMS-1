package com.Anurag.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
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

import com.Anurag.demo.dao.AdminMasterDao;
import com.Anurag.demo.dao.AdminMasterRepo;
import com.Anurag.demo.dto.AdminAnalysisDetails;
import com.Anurag.demo.dto.AllResponse;
import com.Anurag.demo.dto.DropDownAdminList;

@Controller
public class AdminMasterController {

	ArrayList<DropDownAdminList> admins;
	ArrayList<AllResponse> allDetails;
	ArrayList<AllResponse>  complete1;
	String aaid;
	
	@Autowired
	AdminMasterRepo repo;
	
	@Autowired
	AdminMasterDao dao;
	
	
	
	@RequestMapping("/AdminLogin")
	public String AdminLogin(@RequestParam String aid, @RequestParam String apass, HttpSession session) {
		
		if(dao.findByAdminMaster(aid, apass)>0) {
		
		this.aaid=aid;
			
		
		
		session.setAttribute("repo", repo);
		this.admins=repo.getDropDownAdminList(aid);
		this.allDetails=repo.getAllDetails(admins.get(0).getLocation_lid());
		
		session.setAttribute("location_lid",admins.get(0).getLocation_lid());
		session.setAttribute("aid", aid);
		System.out.println("details are "+aid+" and "+admins.get(0).getLocation_lid());
		session.setAttribute("allDetails", allDetails);
		System.out.println("admin login Success");
		
		return "TellerLogin.jsp";
		
		}else {
		
			return "redirect:Login.jsp";
			
		}
		
	}
	
	@RequestMapping("/AdminTellerReport")
	public String AdminTellerReport(@RequestParam(value = "tellerid", required = false) String tellerid,@RequestParam(value = "date1", required = false) String date1,@RequestParam(value = "date2", required = false) String date2,HttpSession session) {
		
		ArrayList<AllResponse>  complete=repo.getCompleteTellerDetails(admins.get(0).getLocation_lid(),tellerid,date1,date2);
		complete1=complete;
				
		session.setAttribute("complete", complete);
		
		return "AdminTellerReport.jsp";
	}
	
	@RequestMapping("/AdminLoginBack")
	public String AdminLoginBack() {
		
		return "redirect:AdminLogin.jsp";
	}
	
	@RequestMapping("/AdminAddTeller")
	public String SuperUserAddTeller(@RequestParam String tid,@RequestParam String tname,@RequestParam String tpno,@RequestParam String tpass,@RequestParam String location_lid,HttpServletResponse res,HttpSession session) {
		

		
		repo.tellerInsert(tid, tname, tpno, tpass, location_lid);
		System.out.println("Insert Success");
		
		 return "redirect:AdminLogin.jsp";
			
	}
	
	@RequestMapping("/AdminLogOut")
	public String AdminLogOut() {
		
		return "redirect:Login.jsp";
	}
	
	@RequestMapping("/showDetails")
	public String showDetails(@RequestParam(value = "tid", required = false) String tid,@RequestParam(value = "date1", required = false) String date1,@RequestParam(value = "date2", required = false) String date2,HttpSession session) {
		
		session.setAttribute("repo", repo);
		session.setAttribute("tid", tid);
        session.setAttribute("date1", date1);
        session.setAttribute("date2", date2);
		session.setAttribute("lid",admins.get(0).getLocation_lid());
		String lid=admins.get(0).getLocation_lid();

		ArrayList<AdminAnalysisDetails> details;
		
		if(tid!=null && date1!=null && date2!=null) {
			
			details=repo.getAdminAnalysisDetails3(lid, tid, date1, date2);
			
		}else if(tid!=null && date1==null && date2==null) {
			
			details=repo.getAdminAnalysisDetails2(lid, tid);
			
		}else {
			
			details=repo.getAdminAnalysisDetails1(lid, date1, date2);
			
		}
		
		session.setAttribute("details", details);
		
		return "showDetails.jsp";
	}
	
	@RequestMapping("/showDetails2")
	public String showDetails2(HttpSession session) {
		session.setAttribute("repo", repo);
		session.setAttribute("lid",admins.get(0).getLocation_lid());
		return "showDetails2.jsp";
	}
	
	@RequestMapping("/AdminTellerCompleteReport")
	public String AdminTellerCompleteReport(HttpSession session) {
		
		session.setAttribute("allDetails", allDetails);
		
		
		return "AdminTellerCompleteReport.jsp";
	}
	
	@RequestMapping("/ExcelAdminCompleteReport")
	public String ExcelAdminCompleteReport() {
		
		
		
		//--------------------------------------------------
		
		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm:ss");
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
		stri=stri+admins.get(0).getAname()+" Complete Report "+appendingDate+".xlsx";
		System.out.println(stri);
		
		
		try {
			
			//create an .xlsx format workbook
			Workbook workbook=new XSSFWorkbook();
			
			//create a new sheet in workbook
			Sheet sh=workbook.createSheet("Invoices");
			
			//create top row with column headings
			String[] columnHeaders= {"Teller Id","Teller Name","Teller Phno","Customer Id","Customer Name","Customer Phno","JobName","JobPrice","Discount","GST","Amount","Location Name","Location Phno","Date"};
			
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
			ArrayList<AllResponse> a=allDetails;
			System.out.println(a);
			int rownum=1;
			for(AllResponse i:a) {
				Row row=sh.createRow(rownum++);
				row.createCell(0).setCellValue(i.getTid());
				row.createCell(1).setCellValue(i.getTname());
				row.createCell(2).setCellValue(i.getTpno());
				row.createCell(3).setCellValue(i.getCid());
				row.createCell(4).setCellValue(i.getCname());
				row.createCell(5).setCellValue(i.getCpno());
				row.createCell(6).setCellValue(i.getJobname());
				row.createCell(7).setCellValue(i.getJobprice());
				row.createCell(8).setCellValue(i.getDiscount());
				row.createCell(9).setCellValue(i.getGst());
				row.createCell(10).setCellValue(i.getAmount());
				row.createCell(11).setCellValue(i.getLname());
				row.createCell(12).setCellValue(i.getLpno());
				
				String[] temp=i.getDate().toString().split("-");
				String final1=temp[2]+"-"+temp[1]+"-"+temp[0];
				row.createCell(13).setCellValue(final1);
				
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
		
		
		
		return "AdminTellerCompleteReport.jsp";
	}
	
	@RequestMapping("/update1")
	public String Update1(@RequestParam String new1,@RequestParam String confirm)
	{
		if(new1.equals(confirm)) {
			repo.updatePassword(aaid, new1);
		}
		
		return "Login.jsp";
	}
	

	
	
	@RequestMapping("/AdminTellerReportExcel")
	public String AdminTellerReportExcel() {
		
		
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
		stri=stri+admins.get(0).getAname()+" Report "+appendingDate+".xlsx";
		System.out.println(stri);
		
		
		try {
			
			//create an .xlsx format workbook
			Workbook workbook=new XSSFWorkbook();
			
			//create a new sheet in workbook
			Sheet sh=workbook.createSheet("Invoices");
			
			//create top row with column headings
			String[] columnHeaders= {"Teller Id","Teller Name","Teller Phno","Customer Id","Customer Name","Customer Phno","JobName","JobPrice","Discount","GST","Amount","Location Name","Location Phno","Date"};
			
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
			ArrayList<AllResponse> a=complete1;
			System.out.println(a);
			int rownum=1;
			for(AllResponse i:a) {
				Row row=sh.createRow(rownum++);
				row.createCell(0).setCellValue(i.getTid());
				row.createCell(1).setCellValue(i.getTname());
				row.createCell(2).setCellValue(i.getTpno());
				row.createCell(3).setCellValue(i.getCid());
				row.createCell(4).setCellValue(i.getCname());
				row.createCell(5).setCellValue(i.getCpno());
				row.createCell(6).setCellValue(i.getJobname());
				row.createCell(7).setCellValue(i.getJobprice());
				row.createCell(8).setCellValue(i.getDiscount());
				row.createCell(9).setCellValue(i.getGst());
				row.createCell(10).setCellValue(i.getAmount());
				row.createCell(11).setCellValue(i.getLname());
				row.createCell(12).setCellValue(i.getLpno());
				
				String[] temp=i.getDate().toString().split("-");
				String final1=temp[2]+"-"+temp[1]+"-"+temp[0];
				row.createCell(13).setCellValue(final1);
				
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
		
		
		return "AdminTellerReport.jsp";
	}
	
}
