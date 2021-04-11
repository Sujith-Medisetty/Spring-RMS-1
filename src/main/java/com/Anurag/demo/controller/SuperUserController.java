package com.Anurag.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
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

import com.Anurag.demo.dao.SuperUserDao;
import com.Anurag.demo.dao.SuperUserRepo;
import com.Anurag.demo.dao.TellerMasterDao;
import com.Anurag.demo.dto.AdminAnalysisDetails;
import com.Anurag.demo.dto.AllResponse;
import com.Anurag.demo.dto.DropDownTellerList;
import com.Anurag.demo.dto.LocationResponse;


@Controller
public class SuperUserController {
	
	String sid;
	String spass;
	ArrayList<AllResponse> responses;
	ArrayList<DropDownTellerList> responses2;
	List<String> responses3;
	ArrayList<LocationResponse> responses4;

	@Autowired
	SuperUserDao dao;
	
	@Autowired
	TellerMasterDao dao2;
	
	@Autowired
	SuperUserRepo repo;
	
	@RequestMapping("/SuperUserLogOut")
	public String SuperUserLogOut() {
		
		return "redirect:Login.jsp";
	}
	
	@RequestMapping("/SuperUserLogin")
	public String SuperUserLogin(@RequestParam String sid,@RequestParam String spass,HttpSession session) {
		
		if(dao.findBySuperUser(sid, spass)>0) {
			
			this.sid=sid;
			this.spass=spass;
			
			responses=repo.getAllDetails();
			responses2=repo.getDropDownTellerList();
			responses3=repo.getDropDownLocationList();			
			responses4=repo.getLocationList();
		
			for(LocationResponse i:responses4) {
				System.out.println("location is "+i);
			}
			
			session.setAttribute("repo", repo);
			session.setAttribute("responses4",responses4);
			session.setAttribute("responses", responses);
			session.setAttribute("responses2", responses2);
			session.setAttribute("responses3", responses3);
			
			//---------------------------------------------------
			
			File home = FileSystemView.getFileSystemView().getHomeDirectory();
			String path=home.getAbsolutePath();
			String[] list1=path.replaceAll(Pattern.quote("\\"),"\\\\").split("\\\\");
			String stri="";
			for(String i :list1) {
				stri=stri+i+"/";
			}
			stri=stri+"invoice.xlsx";
			System.out.println(stri);
			
			
			try {
				
				//create an .xlsx format workbook
				Workbook workbook=new XSSFWorkbook();
				
				//create a new sheet in workbook
				Sheet sh=workbook.createSheet("Invoices");
				
				//create top row with column headings
				String[] columnHeaders= {"tname","tpno","cid","cname","cpno","jobname","lname","lpno","date"};
				
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
				ArrayList<AllResponse> a=repo.getAllDetails();
				System.out.println(a);
				int rownum=1;
				for(AllResponse i:a) {
					Row row=sh.createRow(rownum++);
					row.createCell(0).setCellValue(i.getTname());
					row.createCell(1).setCellValue(i.getTpno());
					row.createCell(2).setCellValue(i.getCid());
					row.createCell(3).setCellValue(i.getCname());
					row.createCell(4).setCellValue(i.getCpno());
					row.createCell(5).setCellValue(i.getJobname());
					row.createCell(6).setCellValue(i.getLname());
					row.createCell(7).setCellValue(i.getLpno());
					
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
			
			return "SuperUserLogin.jsp";
		}else {
			return "redirect:Login.jsp ";
		}
		
		
	}
	
	@RequestMapping("/SuperUserTellerReport")
	public String SuperUserTellerReport(@RequestParam(value = "tellerid", required = false) String tellerid,@RequestParam(value = "location", required = false) String location,@RequestParam(value = "date1", required = false) String date1,@RequestParam(value = "date2", required = false) String date2,HttpSession session) {
		
		ArrayList<AllResponse> completeData = repo.getIdAndLocation(tellerid,location,date1,date2);
		session.setAttribute("completeData", completeData);
		System.out.println(completeData);
		
		session.setAttribute("sid", sid);
		session.setAttribute("spass",spass);
		
		
		return "SuperUserTellerReport.jsp";
	}
	
	
	@RequestMapping("/SuperUserAddTeller")
	public String SuperUserAddTeller(@RequestParam String tid,@RequestParam String tname,@RequestParam String tpno,@RequestParam String tpass,@RequestParam String location_lid,HttpServletResponse res,HttpSession session) {
		

		
		repo.tellerInsert(tid, tname, tpno, tpass, location_lid);
		System.out.println("Insert Success");
		
		 return "redirect:SuperUserLogin.jsp";
			
	}
	
	@RequestMapping("/SuperUserLoginBack")
	public String SuperUserLoginBack(HttpSession session) {
		
		session.setAttribute("repo", repo);
		session.setAttribute("responses4",responses4);
		session.setAttribute("responses", responses);
		session.setAttribute("responses2", responses2);
		session.setAttribute("responses3", responses3);
		
		return "SuperUserLogin.jsp";
	}
	
	@RequestMapping("/SuperUserAddAdmin")
	public String SuperUserAddAdmin(@RequestParam String aid,@RequestParam String aname,@RequestParam String apno,@RequestParam String apass,@RequestParam String location_lid,HttpServletResponse res,HttpSession session) {
	
		repo.adminInsert(aid, aname, apno, apass, location_lid);
		System.out.println("Insert Success");
		
		 return "redirect:SuperUserLogin.jsp";
		 
	}
	
	@RequestMapping("/SuperUserAddLocation")
	public String SuperUserAddLocation(@RequestParam String lid,@RequestParam String lname,@RequestParam String lpno) {
		
		repo.locationInsert(lid, lname, lpno);
		System.out.println("Insert Success");
		
		return "redirect:SuperUserLogin.jsp";
	}
	
	@RequestMapping("/SuperUserTellerAnalysis")
	public String SuperUserTellerAnalysis(@RequestParam(value = "tellerid", required = false) String tellerid,@RequestParam(value = "location", required = false) String location,@RequestParam(value = "date1", required = false) String date1,@RequestParam(value = "date2", required = false) String date2,HttpSession session) {

		ArrayList<AdminAnalysisDetails> summary=repo.getSuperUserTellerAnalysis(tellerid, location,date1,date2);
		session.setAttribute("summary", summary);
		return "superUserAnalysis.jsp";
	}
	
	@RequestMapping("/update2")
	public String update(@RequestParam String new1,@RequestParam String confirm) {
		
		if(new1.equals(confirm)) {
			System.out.println("yes buddy they are same ... ");
            if(new1.equals(confirm)) {
            	repo.updatePassword(sid, new1);
            }
		}
		
	return "Login.jsp";	
	}
	
	
	@RequestMapping("/superuserTellerCompleteReport")
	public String superuserTellerCompleteReport(HttpSession session) {
		
		session.setAttribute("responses", responses);
		
		return "superuserTellerCompleteReport.jsp";
	}
	
	@RequestMapping("/superuserTellerCompleteAnalysis")
	public String superuserTellerCompleteAnalysis(HttpSession session) {
		
		ArrayList<AdminAnalysisDetails> analysisDetails=repo.getSuperUserTellerCompleteAnalysis();
		session.setAttribute("analysisDetails", analysisDetails);
		return "superuserTellerCompleteAnalysis.jsp";
	}
	
}
