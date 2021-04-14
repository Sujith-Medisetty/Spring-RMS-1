package com.Anurag.demo.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.Anurag.demo.dto.AdminAnalysisDetails;
import com.Anurag.demo.dto.AdminDetails;
import com.Anurag.demo.dto.AllResponse;
import com.Anurag.demo.dto.DropDownAdminList;
import com.Anurag.demo.dto.DropDownTellerList;
import com.Anurag.demo.dto.LocationResponse;
import com.Anurag.demo.dto.TellerAnalysis;
import com.Anurag.demo.dto.TellerPerformance;

@Repository
public class AdminMasterRepo {

    @PersistenceContext 
    EntityManager entityManager;
    
	/*------------------------------------getDropDownTellerList()--------------------------------------------------------------------------------------*/
    
    public ArrayList<DropDownTellerList>  getDropDownTellerList() {
    	List<Object[]> results= this.entityManager.createNativeQuery("select tid, tname, tpno from teller_master ")
    			                     .getResultList();
    	
    	ArrayList<DropDownTellerList> responses = new ArrayList<DropDownTellerList>();
    	
    	results.stream().forEach((record)->{
            String tid=(String)record[0];
            String tname=(String)record[1];
            String tpno=(String)record[2];
            responses.add(new DropDownTellerList(tid,tname,tpno));
    	});
    	
    	return responses;
     }
    
		/*------------------------------------getLocationList()--------------------------------------------------------------------------------------*/
    
    public ArrayList<LocationResponse>  getLocationList() {
    	List<Object[]> results= this.entityManager.createNativeQuery("select lid, lname, lpno from location_master")
    			                   .getResultList();
    	
    	ArrayList<LocationResponse> responses = new ArrayList<LocationResponse>();
    	
    	results.stream().forEach((record)->{
    		String lid=(String)record[0];
    		String lname=(String)record[1];
    		String lpno=(String)record[2];
    		responses.add(new LocationResponse(lid, lname, lpno));
    	});
    	
    	return responses;
     }
    
	/*--------------------------------------------------getAllDetails()-------------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getAllDetails(String lid) {
    	List<Object[]> results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? group by c.cid;")
    			                .setParameter(1, lid)     
    			                .getResultList();
    	
    	ArrayList<AllResponse> responses = new ArrayList<AllResponse>();
    	
    	results.stream().forEach((record)->{
    		String tellerid=(String)record[0];
    		String tname=(String)record[01];
    		String tpno=(String)record[2];
    		int cid=(int)record[3];
    		String cname=(String)record[4];
    		String cpno=(String)record[5];
    		String jobname=(String)record[6];
    		String jobprice=(String)record[7];
    		String discount=(String)record[8];
    		String gst=(String)record[9];
    		String amount=(String)record[10];
    		String lname=(String)record[11];
    		String lpno=(String)record[12];
    		Date date =(Date)record[13];
    		responses.add(new AllResponse(tellerid,tname, tpno, cid, cname, cpno, jobname,jobprice,discount,gst, amount,lname, lpno, date));
    	});
    	
    	
    	return responses;
     }
    
    
    /*------------------------------------getDropDownAdminList()---------------------------------------------------*/
    
    public ArrayList<DropDownAdminList> getDropDownAdminList(String adminid) {
    	
    	List<Object[]> results=this.entityManager.createNativeQuery("select aid, aname, apass, apno, location_lid from admin_master where aid=?")
    			                    .setParameter(1, adminid)
    			                    .getResultList();
    	
    	ArrayList<DropDownAdminList> responses=new ArrayList<DropDownAdminList>();
    	
    	results.stream().forEach((record)->{
    		String aid=(String)record[0];
    		String aname=(String)record[1];
    		String apass=(String)record[2];
    		String apno=(String)record[3];
    		String location_lid=(String)record[4];
    		responses.add(new DropDownAdminList(aid,aname,apass,apno,location_lid));
    	});
    	
    	return responses;
    	
    }
    
		/*-------------------------------------------tellerInsert--------------------------------------------------------------*/
    
    @Transactional
	  public void tellerInsert(String tid,String tname,String tpno, String tpass, String location_lid) {
		    
	      entityManager.createNativeQuery("insert into teller_master (tid,tname,tpass,tpno,location_lid) values (?, ?, ? ,?,?)")
	        .setParameter(1, tid)
	        .setParameter(2,tname)
	        .setParameter(3,tpass)
	        .setParameter(4, tpno)
	        .setParameter(5, location_lid)
	        .executeUpdate();
	  }
    
		/*------------------------------------getTellerList()--------------------------------------------------------------------------------------*/
    
    public ArrayList<DropDownTellerList>  getTellerList(String tellerid) {
    	List<Object[]> results= this.entityManager.createNativeQuery("select tid, tname, tpno from teller_master where tid=? ")
    			                     .setParameter(1, tellerid)
    			                     .getResultList();
    	
    	ArrayList<DropDownTellerList> responses = new ArrayList<DropDownTellerList>();
    	
    	results.stream().forEach((record)->{
            String tid=(String)record[0];
            String tname=(String)record[1];
            String tpno=(String)record[2];
            responses.add(new DropDownTellerList(tid,tname,tpno));
    	});
    	
    	return responses;
     }
    
	/*--------------------------------------------------getAllTellerDetails()-------------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getAllTellerDetails(String tid) {
    	List<Object[]> results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? group by c.cid;")
    			                .setParameter(1, tid)     
    			                .getResultList();
    	
    	ArrayList<AllResponse> responses = new ArrayList<AllResponse>();
    	
    	results.stream().forEach((record)->{
    		String tellerid=(String)record[0];
    		String tname=(String)record[01];
    		String tpno=(String)record[2];
    		int cid=(int)record[3];
    		String cname=(String)record[4];
    		String cpno=(String)record[5];
    		String jobname=(String)record[6];
    		String jobprice=(String)record[7];
    		String discount=(String)record[8];
    		String gst=(String)record[9];
    		String amount=(String)record[10];
    		String lname=(String)record[11];
    		String lpno=(String)record[12];
    		Date date =(Date)record[13];
    		responses.add(new AllResponse(tellerid,tname, tpno, cid, cname, cpno, jobname,jobprice,discount,gst, amount,lname, lpno, date));
    	});
    	
    	return responses;
     }
    
    
    
    public ArrayList<AllResponse>  getCompleteTellerDetails(String lid , String ...args) {

    	List<Object[]> results;
    	if(args[0]!=null && args[1]!=null && args[2]!=null) {
    		
        	results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and (c.date between ? and ?)  and t.tid=?  group by c.cid;")
	                .setParameter(1,lid)
	                .setParameter(2, args[1])
	                .setParameter(3, args[2])
	                .setParameter(4, args[0])
	                .getResultList();
    		
    	}else if(args[0]!=null && args[1]==null && args[2]==null) {
    		
        	results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and t.tid=? group by c.cid;")
	                .setParameter(1,lid)
	                .setParameter(2, args[0])
	                .getResultList();
    		
    	}else if(args[0]==null && args[1]!=null && args[2]!=null){
    		
        	results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and (c.date between ? and ?) group by c.cid;")
	                .setParameter(1,lid)
	                .setParameter(2, args[1])
	                .setParameter(3, args[2])
	                .getResultList();
    		
    	}else {
    		
        	results= this.entityManager.createNativeQuery("select array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname, array_to_string(array_agg(distinct t.tpno),',') tpno, c.cid, array_to_string(array_agg(distinct c.cname),',') cname, array_to_string(array_agg(distinct c.cpno),',') cpno ,array_to_string(array_agg(j.jobname),',') jobname,cast(sum(j.jobprice) as text) as jobprice ,cast(array_to_string(array_agg(distinct h.discount),',') as text) discount,cast(array_to_string(array_agg(distinct h.gst),',') as text) gst,cast(round((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as text) as amount, array_to_string(array_agg(distinct l.lname),',') lname, array_to_string(array_agg(distinct l.lpno),',') lpno, array_to_string(array_agg(distinct c.date),',') date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where  l.lid=? group by c.cid;")
	                .setParameter(1,lid)
	                .getResultList();
    		
    	}
    	
    	
    	ArrayList<AllResponse> responses = new ArrayList<AllResponse>();
    	
    	results.stream().forEach((record)->{
    		String tellerid=(String)record[0];
    		String tname=(String)record[01];
    		String tpno=(String)record[2];
    		int cid=(int)record[3];
    		String cname=(String)record[4];
    		String cpno=(String)record[5];
    		String jobname=(String)record[6];
    		String jobprice=(String)record[7];
    		String discount=(String)record[8];
    		String gst=(String)record[9];
    		String amount=(String)record[10];
    		String lname=(String)record[11];
    		String lpno=(String)record[12];
    		Date date =(Date)record[13];
    		responses.add(new AllResponse(tellerid,tname, tpno, cid, cname, cpno, jobname,jobprice,discount,gst, amount,lname, lpno, date));
    	});
    	
    	return responses;
     }
    
		/* --------------------------------- */
    
    public String getTotalCustomers(String location_lid) {
    	
		  
		  Query query= entityManager.createNativeQuery("select cast(count(distinct(c.cid)) as text) from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?")
				             .setParameter(1, location_lid);
				             
		 
		 String result=(String) query.getResultList().get(0);		  
		  return result;
  	
  }
  
  public String getTotalMoneyEarned(String location_lid) {
  	
		  Query query= entityManager.createNativeQuery("select cast(round(sum(j.jobprice-(h.discount*j.jobprice)/100)) as text) as amount from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?")
		                        .setParameter(1, location_lid); 
		                        
		 String result=(String) query.getResultList().get(0);		  
		  return result;
  }

  
  public ArrayList<AdminDetails> getAdminDetails(String aid) {
      
  	List<Object[]> results=entityManager.createNativeQuery("select aid,aname,apno,apass,location_lid from admin_master where aid=?")
  			                        .setParameter(1, aid)
  	                                .getResultList();
  	
        ArrayList<AdminDetails> responses = new ArrayList<AdminDetails>();
  	
  	results.stream().forEach((record)->{
  		String adminid=(String)record[0];
  		String aname=(String)record[1];
  		String apno=(String)record[2];
  		String apass=(String)record[3];
  		String location_lid=(String)record[4];
  		responses.add(new AdminDetails(adminid,aname,apno,apass,location_lid));
  	});
  	
  	return responses;
            	
  }
  
  public ArrayList<TellerAnalysis> getAdminTellerAnalysis(String location_lid) {
      
  	List<Object[]> results=entityManager.createNativeQuery("select c.date, c.cid, c.cname,  h.job_jobid, j.jobname, cast(round(j.jobprice-(h.discount*j.jobprice)/100) as text) as amount, l.lname from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?")
  	                      .setParameter(1, location_lid)          
  			              .getResultList();
  	
        ArrayList<TellerAnalysis> responses = new ArrayList<TellerAnalysis>();
  	
  	results.stream().forEach((record)->{
  		Date date=(Date)record[0];
  		int cid=(int)record[1];
  		String cname=(String)record[2];
  		String jobid=(String)record[3];
  		String jobname=(String)record[4];
  		String amount=(String)record[5];
  		String lname=(String)record[6];
  		responses.add(new TellerAnalysis(cid, cname, jobid, jobname, amount,lname,date));
  	});
  	
  	return responses;
            	
  }

  public ArrayList<LocationResponse>  getMapIdToLocation(String location_lid) {
  	List<Object[]> results= this.entityManager.createNativeQuery("select lid, lname, lpno from location_master where lid=?")
  			                   .setParameter(1, location_lid)
  			                   .getResultList();
  	
  	ArrayList<LocationResponse> responses = new ArrayList<LocationResponse>();
  	
  	results.stream().forEach((record)->{
  		String lid=(String)record[0];
  		String lname=(String)record[1];
  		String lpno=(String)record[2];
  		responses.add(new LocationResponse(lid, lname, lpno));
  	});
  	
  	return responses;
   }
    
  
  public ArrayList<TellerPerformance> getTellersPerformance(String lid) {
	  
	  List<Object[]> results=this.entityManager.createNativeQuery("select tid , array_to_string(array_agg(distinct tname),',') tname,cast(count(customers) as text) as customers, cast(round(sum(totalamount)) as text) as amount from  (select  array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname,array_to_string(array_agg(distinct c.cname),',') cname, cast(count(c.cid) as text) as customers,  array_to_string(array_agg(distinct t.tpno),',') tpno,((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as totalamount, cast(array_to_string(array_agg(distinct l.lname),',') as text) lname, cast(array_to_string(array_agg(distinct l.lpno),',') as text) lpno, cast(array_to_string(array_agg(distinct c.date),',') as date) date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? group by c.cid) as derived group by tid;")
			                .setParameter(1, lid) 
			  				.getResultList();
	  
	  ArrayList<TellerPerformance> responses = new ArrayList<TellerPerformance>();
	  
	  results.stream().forEach((record)->{
		  String tid=(String)record[0];
		  String tname=(String)record[1];
		  String customers=(String)record[2];
		  String amount=(String)record[3];
		  responses.add(new TellerPerformance(tid, tname, customers, amount));
	  });
	  
	  return responses;
  }
  
  
  public ArrayList<AdminAnalysisDetails> getAdminAnalysisDetails1(String lid, String date1,String date2) {
	  
	  List<Object[]> results=this.entityManager.createNativeQuery("select tid , array_to_string(array_agg(distinct tname),',') tname,array_to_string(array_agg(distinct tpno),',') tpno,cast(count(customers) as text) as customers, cast(round(sum(totalamount)) as text) as amount from  (select  array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname,array_to_string(array_agg(distinct c.cname),',') cname, cast(count(c.cid) as text) as customers,  array_to_string(array_agg(distinct t.tpno),',') tpno,((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as totalamount, cast(array_to_string(array_agg(distinct l.lname),',') as text) lname, cast(array_to_string(array_agg(distinct l.lpno),',') as text) lpno, cast(array_to_string(array_agg(distinct c.date),',') as date) date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and(c.date between ? and ?)  group by c.cid) as derived group by tid;")
			                   .setParameter(1, lid)
			                   .setParameter(2, date1)
			                   .setParameter(3, date2)
			                   .getResultList();
	  
	  ArrayList<AdminAnalysisDetails> responses=new ArrayList<AdminAnalysisDetails>();
	  
	  results.stream().forEach((record)->{
		  String tid=(String)record[0];
		  String tname=(String)record[1];
		  String tpno=(String)record[2];
		  String customers=(String)record[3];
		  String amount=(String)record[4];
		  responses.add(new AdminAnalysisDetails(tid, tname, tpno, customers, amount));
	  });
	  
	  
	  return responses;
  }
  
  public ArrayList<AdminAnalysisDetails> getAdminAnalysisDetails2(String lid, String tid) {
	  
	  List<Object[]> results=this.entityManager.createNativeQuery("select tid , array_to_string(array_agg(distinct tname),',') tname,array_to_string(array_agg(distinct tpno),',') tpno,cast(count(customers) as text) as customers, cast(round(sum(totalamount)) as text) as amount from  (select  array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname,array_to_string(array_agg(distinct c.cname),',') cname, cast(count(c.cid) as text) as customers,  array_to_string(array_agg(distinct t.tpno),',') tpno,((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as totalamount, cast(array_to_string(array_agg(distinct l.lname),',') as text) lname, cast(array_to_string(array_agg(distinct l.lpno),',') as text) lpno, cast(array_to_string(array_agg(distinct c.date),',') as date) date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and tid=?   group by c.cid) as derived group by tid;")
			                   .setParameter(1, lid)
			                   .setParameter(2, tid)
			                   .getResultList();
	  
	  ArrayList<AdminAnalysisDetails> responses=new ArrayList<AdminAnalysisDetails>();
	  
	  results.stream().forEach((record)->{
		  String tellerid=(String)record[0];
		  String tname=(String)record[1];
		  String tpno=(String)record[2];
		  String customers=(String)record[3];
		  String amount=(String)record[4];
		  responses.add(new AdminAnalysisDetails(tellerid, tname, tpno, customers, amount));
	  });
	  
	  
	  return responses;
  }
  
  public ArrayList<AdminAnalysisDetails> getAdminAnalysisDetails3(String lid, String tid,String date1, String date2) {
	  
	  List<Object[]> results=this.entityManager.createNativeQuery("select tid , array_to_string(array_agg(distinct tname),',') tname,array_to_string(array_agg(distinct tpno),',') tpno,cast(count(customers) as text) as customers, cast(round(sum(totalamount)) as text) as amount from  (select  array_to_string(array_agg(distinct t.tid),',') tid, array_to_string(array_agg(distinct t.tname),',') tname,array_to_string(array_agg(distinct c.cname),',') cname, cast(count(c.cid) as text) as customers,  array_to_string(array_agg(distinct t.tpno),',') tpno,((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))+(((sum(j.jobprice)-((sum(j.jobprice)*cast(array_to_string(array_agg(distinct h.discount),',') as integer))/100))*cast(array_to_string(array_agg(distinct h.gst),',') as integer))/100)) as totalamount, cast(array_to_string(array_agg(distinct l.lname),',') as text) lname, cast(array_to_string(array_agg(distinct l.lpno),',') as text) lpno, cast(array_to_string(array_agg(distinct c.date),',') as date) date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?  and (c.date between ? and ?) and tid=?   group by c.cid) as derived group by tid;")
			                   .setParameter(1, lid)
			                   .setParameter(2, date1)
			                   .setParameter(3, date2)
			                   .setParameter(4, tid)
			                   .getResultList();
	  
	  ArrayList<AdminAnalysisDetails> responses=new ArrayList<AdminAnalysisDetails>();
	  
	  results.stream().forEach((record)->{
		  String tellerid=(String)record[0];
		  String tname=(String)record[1];
		  String tpno=(String)record[2];
		  String customers=(String)record[3];
		  String amount=(String)record[4];
		  responses.add(new AdminAnalysisDetails(tellerid, tname, tpno, customers, amount));
	  });
	  
	  
	  return responses;
  }
  
  @Transactional
  public void updatePassword(String aid,String apass) {
 	 
 	 entityManager.createNativeQuery("update admin_master set apass=? where aid=?")
	        .setParameter(1, apass)
	        .setParameter(2,aid)
	        .executeUpdate();
  }
	
}
