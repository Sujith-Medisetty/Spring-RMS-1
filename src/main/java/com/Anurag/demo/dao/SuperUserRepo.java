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
import com.Anurag.demo.dto.AllResponse;
import com.Anurag.demo.dto.DropDownAdminList;
import com.Anurag.demo.dto.DropDownTellerList;
import com.Anurag.demo.dto.LocationResponse;
import com.Anurag.demo.dto.SuperUserDetails;
import com.Anurag.demo.dto.TellerAnalysis;
import com.Anurag.demo.dto.TellerAndLocationResponse;

@Repository
public class SuperUserRepo {

    @PersistenceContext 
    EntityManager entityManager;
    
	/*--------------------------------------------------getAllDetails()-------------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getAllDetails() {
    	List<Object[]> results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid  group by c.cid;")
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
    
    /*------------------------------------getDropDownAdminList()---------------------------------------------------*/
    
    public ArrayList<DropDownAdminList> getDropDownAdminList() {
    	
    	List<Object[]> results=this.entityManager.createNativeQuery("select aid, aname, apass, apno, location_lid from admin_master")
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
    
	/*-----------------------------------------getTellerAndLocation--------------------------------------------------*/
    
    public ArrayList<TellerAndLocationResponse> getTellerAndLocation() {
    	
    	List<Object[]> results=this.entityManager.createNativeQuery("select t.tid,t.tname,t.tpno,l.lid,l.lname,l.lpno from location_master l join teller_master t where t.location_lid=l.lid")
                .getResultList();
 
      ArrayList<TellerAndLocationResponse> responses=new ArrayList<TellerAndLocationResponse>();
     
      results.stream().forEach((record)->{
    	  String tid=(String)record[0];
    	  String tname=(String)record[1];
    	  String tpno=(String)record[2];
    	  String lid=(String)record[3];
    	  String lname=(String)record[4];
    	  String lpno=(String)record[5];
    	  responses.add(new TellerAndLocationResponse(tid,tname,tpno,lid,lname,lpno));
      });
      
      return responses;
    
    }
    
    
    
   /*-----------------------------------getDropDownLocationList()--------------------------------------------------------------------------------------*/
    
    public List<String>  getDropDownLocationList() {
    	List<String> results= this.entityManager.createNativeQuery("select lname from location_master ")
    			                     .getResultList();
    	
    	return results;
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
    
		/*---------------------------getIdAndLocation---------------------------------------------------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getIdAndLocation(String id, String... args) {
    	
    	List<Object[]> results;
    	
    	if(id!=null && args[0]!=null && args[1]!=null && args[2]!=null ) {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? and l.lid=? and (c.date between ? and ?)  group by c.cid;")
	                   .setParameter(1, id)
	                   .setParameter(2, args[0])
	                   .setParameter(3, args[1])
	                   .setParameter(4, args[2])
	                   .getResultList();
    		
    	}else if(id!=null && args[0]==null && args[1]==null && args[2]==null ) {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? group by c.cid;")
	                   .setParameter(1, id)
	                   .getResultList();
    		
    	}else if(id==null && args[0]!=null && args[1]==null && args[2]==null ) {
    		
       		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? group by c.cid;")
	                   .setParameter(1, args[0])
	                   .getResultList();
    		
    	}else if(id==null && args[0]==null && args[1]!=null && args[2]!=null ) {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where (c.date between ? and ?)  group by c.cid;")
	                   .setParameter(1, args[1])
	                   .setParameter(2, args[2])
	                   .getResultList();
    		
    	}else if(id!=null && args[0]!=null && args[1]==null && args[2]==null) {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? and l.lid=? group by c.cid;")
	                   .setParameter(1, id)
	                   .setParameter(2, args[0])
	                   .getResultList();
    		
    	}else if(id!=null && args[0]==null && args[1]!=null && args[2]!=null) {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? and (c.date between ? and ?)  group by c.cid;")
	                   .setParameter(1, id)
	                   .setParameter(2, args[1])
	                   .setParameter(3, args[2])
	                   .getResultList();
    		
    	}else if(id==null && args[0]!=null && args[1]!=null && args[2]!=null){
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? and (c.date between ? and ?)  group by c.cid;")
	                   .setParameter(1, args[0])
	                   .setParameter(2, args[1])
	                   .setParameter(3, args[2])
	                   .getResultList();
    		
    	}else {
    		
    		results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid  group by c.cid;")
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
    
    
    
    public ArrayList<AdminAnalysisDetails>  getSuperUserTellerAnalysis(String id, String... args) {
    	
    	List<Object[]> results;
    	
    	if(id!=null && args[0]!=null && args[1]!=null && args[2]!=null ) {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?  and (c.date between ? and ?) and tid=? group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[0])
	                   .setParameter(2, args[1])
	                   .setParameter(3, args[2])
	                   .setParameter(4, id)
	                   .getResultList();
    		
    	}else if(id!=null && args[0]==null && args[1]==null && args[2]==null ) {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where  tid=? group by c.cid) as derived group by tid;")
	                   .setParameter(1, id)
	                   .getResultList();
    		
    	}else if(id==null && args[0]!=null && args[1]==null && args[2]==null ) {
    		
       		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[0])
	                   .getResultList();
    		
    	}else if(id==null && args[0]==null && args[1]!=null && args[2]!=null ) {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where (c.date between ? and ?) group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[1])
	                   .setParameter(2, args[2])
	                   .getResultList();
    		
    	}else if(id!=null && args[0]!=null && args[1]==null && args[2]==null) {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?  and  tid=? group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[0])
	                   .setParameter(2, id)
	                   .getResultList();
    		
    	}else if(id!=null && args[0]==null && args[1]!=null && args[2]!=null) {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where (c.date between ? and ?) and tid=? group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[1])
	                   .setParameter(2, args[2])
	                   .setParameter(3, id)
	                   .getResultList();
    		
    	}else if(id==null && args[0]!=null && args[1]!=null && args[2]!=null){
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=?  and (c.date between ? and ?)  group by c.cid) as derived group by tid;")
	                   .setParameter(1, args[0])
	                   .setParameter(2, args[1])
	                   .setParameter(3, args[2])
	                   .getResultList();
    		
    	}else {
    		
    		results= this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid  group by c.cid) as derived group by tid;")
	                   .getResultList();
    		
    	}
    	
	ArrayList<AdminAnalysisDetails> responses = new ArrayList<AdminAnalysisDetails>();
    	
    	results.stream().forEach((record)->{
    		String tid=(String)record[0];
    		String tname=(String)record[1];
    		String tpno=(String)record[2];
    		String customers=(String)record[3];
    		String amount=(String)record[4];
    		responses.add(new AdminAnalysisDetails(tid,tname, tpno, customers, amount));
    		});
    	
    	return responses;
    }

    
    public ArrayList<AdminAnalysisDetails>  getSuperUserTellerCompleteAnalysis() {
    	
    	List<Object[]> results=this.entityManager.createNativeQuery("select tid, tname, tpno, convert(count(customers),char) as customers, convert(round(sum(totalamount)),char) as amount from  (select  t.tid, count(c.cid) as customers, t.tname, t.tpno,((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)) as totalamount, l.lname, l.lpno, c.date from  customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid  group by c.cid) as derived group by tid;")
                .getResultList();
	ArrayList<AdminAnalysisDetails> responses = new ArrayList<AdminAnalysisDetails>();
    	
    	results.stream().forEach((record)->{
    		String tid=(String)record[0];
    		String tname=(String)record[1];
    		String tpno=(String)record[2];
    		String customers=(String)record[3];
    		String amount=(String)record[4];
    		responses.add(new AdminAnalysisDetails(tid,tname, tpno, customers, amount));
    		});
    	
    	return responses;
    }
    
    
		/*----------------------------------------getId()--------------------------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getId(String id) {
    	List<Object[]> results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where t.tid=? group by c.cid;")
    			                   .setParameter(1, id)
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
    
		/*----------------------------------------getLocation()-----------------------------------------------------------------*/
    
    public ArrayList<AllResponse>  getLocation(String lid) {
    	List<Object[]> results= this.entityManager.createNativeQuery("select t.tid, t.tname, t.tpno, c.cid, c.cname, c.cpno ,  GROUP_CONCAT(j.jobname) jobname,convert(sum(j.jobprice),char) as jobprice ,convert(h.discount,char) discount,convert(h.gst,char) gst, convert(round((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))+(((sum(j.jobprice)-((sum(j.jobprice)*discount)/100))*gst)/100)),char) as amount, l.lname, l.lpno, c.date from customer_master c inner join history_master h on c.cid = h.customer_cid inner join job_price j on h.job_jobid = j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid where l.lid=? group by c.cid;")
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
    
		/*----------------------------------------------adminInsert----------------------------------------------------------------*/
    
    @Transactional
    public void adminInsert(String aid,String aname,String apno, String apass, String location_lid) {
    	
	      entityManager.createNativeQuery("insert into admin_master (aid,aname,apass,apno,location_lid) values (?, ?, ? ,?,?)")
	        .setParameter(1, aid)
	        .setParameter(2,aname)
	        .setParameter(3,apass)
	        .setParameter(4, apno)
	        .setParameter(5, location_lid)
	        .executeUpdate();
    	
    }
    
    @Transactional
    public void locationInsert(String lid,String lname,String lpno) {
    	entityManager.createNativeQuery("insert into location_master (lid,lname,lpno) values (?,?,?)")
    	   .setParameter(1, lid)
    		.setParameter(2, lname)
    		.setParameter(3, lpno)
    		.executeUpdate();
    }
    
    public ArrayList<TellerAnalysis> getSuperUserTellerAnalysis() {
      
    	List<Object[]> results=entityManager.createNativeQuery("select c.date, cid, c.cname,  h.job_jobid, j.jobname, convert(round(j.jobprice-(h.discount*j.jobprice)/100),char) as amount, l.lname from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid inner join teller_master t on t.tid=c.teller_tid inner join location_master l on l.lid=t.location_lid order by c.date")
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
    
    public String getTotalCustomers() {
    	
		  
		  Query query= entityManager.createNativeQuery("select CONVERT(count(distinct(c.cid)), CHAR) from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid");
		 
		 String result=(String) query.getResultList().get(0);		  
		  return result;
    	
    }
    
    public String getTotalMoneyEarned() {
    	
		  Query query= entityManager.createNativeQuery("select convert(round(sum(j.jobprice-(h.discount*j.jobprice)/100)),char) as amount from customer_master c inner join history_master h on c.cid=h.customer_cid  inner join job_price j on h.job_jobid=j.jobid");
			 
		 String result=(String) query.getResultList().get(0);		  
		  return result;
    }

    
    public ArrayList<SuperUserDetails> getSuperUserDetails() {
        
    	List<Object[]> results=entityManager.createNativeQuery("select sid,sname,spno,spass from super_user")
    	                                .getResultList();
    	
          ArrayList<SuperUserDetails> responses = new ArrayList<SuperUserDetails>();
    	
    	results.stream().forEach((record)->{
    		String sid=(String)record[0];
    		String sname=(String)record[1];
    		String spno=(String)record[2];
    		String spass=(String)record[3];
    		responses.add(new SuperUserDetails(sid,sname,spno,spass));
    	});
    	
    	return responses;
              	
    }
    
    @Transactional
    public void updatePassword(String sid,String spass) {
   	 
   	 entityManager.createNativeQuery("update super_user set spass=? where sid=?")
  	        .setParameter(1, spass)
  	        .setParameter(2,sid)
  	        .executeUpdate();
    }
  	
	
}
