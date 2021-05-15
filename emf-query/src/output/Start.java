package output;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.io.*;
import utils.JDBCUtil;

public class Start {
	public static class groupingVariable_X{
		public List<Date> date = new ArrayList<>();
		public List<String> prod = new ArrayList<>();
		public List<Integer> month = new ArrayList<>();
		public List<Integer> year = new ArrayList<>();
		public List<String> state = new ArrayList<>();
		public List<Integer> quant = new ArrayList<>();
		public List<String> cust = new ArrayList<>();
		public List<Integer> day = new ArrayList<>();
		public List<Integer> avg_quant = new ArrayList<>();
	}
	public static class groupingVariable_Y{
		public List<Date> date = new ArrayList<>();
		public List<String> prod = new ArrayList<>();
		public List<Integer> month = new ArrayList<>();
		public List<Integer> year = new ArrayList<>();
		public List<String> state = new ArrayList<>();
		public List<Integer> quant = new ArrayList<>();
		public List<String> cust = new ArrayList<>();
		public List<Integer> day = new ArrayList<>();
		public List<Integer> avg_quant = new ArrayList<>();
	}
	public static class groupingVariable_O{
		public List<Date> date = new ArrayList<>();
		public List<String> prod = new ArrayList<>();
		public List<Integer> month = new ArrayList<>();
		public List<Integer> year = new ArrayList<>();
		public List<String> state = new ArrayList<>();
		public List<Integer> quant = new ArrayList<>();
		public List<String> cust = new ArrayList<>();
		public List<Integer> day = new ArrayList<>();
	}
	public static void main(String[] args) {
	groupingVariable_O O = new groupingVariable_O();
	groupingVariable_X X = new groupingVariable_X();
	groupingVariable_Y Y = new groupingVariable_Y();
	ResultSet rs = null;

	//creating mfs-structure
	Map<String, List> mfs = new HashMap<>();
	mfs.put("prod", new ArrayList<String>());
	mfs.put("month", new ArrayList<Integer>());
	mfs.put("X_avg_quant", new ArrayList<Integer>());
	mfs.put("Y_avg_quant", new ArrayList<Integer>());
		try {
			System.out.println("Database: Loading successful.");
			JDBCUtil db = new JDBCUtil();
			Connection conn = db.connectToDatabase();
			Statement stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM sales");
			while(rs.next()){
			//load data to attribute X
				X.date.add((Date)rs.getObject("date"));
				X.prod.add((String)rs.getObject("prod"));
				X.month.add((Integer)rs.getObject("month"));
				X.year.add((Integer)rs.getObject("year"));
				X.state.add((String)rs.getObject("state"));
				X.quant.add((Integer)rs.getObject("quant"));
				X.cust.add((String)rs.getObject("cust"));
				X.day.add((Integer)rs.getObject("day"));
			//load data to attribute Y
				Y.date.add((Date)rs.getObject("date"));
				Y.prod.add((String)rs.getObject("prod"));
				Y.month.add((Integer)rs.getObject("month"));
				Y.year.add((Integer)rs.getObject("year"));
				Y.state.add((String)rs.getObject("state"));
				Y.quant.add((Integer)rs.getObject("quant"));
				Y.cust.add((String)rs.getObject("cust"));
				Y.day.add((Integer)rs.getObject("day"));
			//load data to attribute O 
				O.date.add((Date)rs.getObject("date"));
				O.prod.add((String)rs.getObject("prod"));
				O.month.add((Integer)rs.getObject("month"));
				O.year.add((Integer)rs.getObject("year"));
				O.state.add((String)rs.getObject("state"));
				O.quant.add((Integer)rs.getObject("quant"));
				O.cust.add((String)rs.getObject("cust"));
				O.day.add((Integer)rs.getObject("day"));
			//load data to groupingVariable 
				mfs.get("prod").add((String)rs.getObject("prod")); 
				mfs.get("month").add((Integer)rs.getObject("month")); 
			}
		}
		catch(SQLException e) {
			System.out.println("Database: URL or username or password or table name error!");
			e.printStackTrace();
		}




		for(int i = 0; i < O.prod.size(); i++) { 
			Integer sum = null; 
			Integer count = null; 
			Integer avg = null; 
			String prod = (String)mfs.get("prod").get(i); 
			Integer month = (Integer)mfs.get("month").get(i); 
			Integer X_avg_quant = 0;
			if(!mfs.get("X_avg_quant").isEmpty()&&i<mfs.get("X_avg_quant").size()) { 
				X_avg_quant= (Integer)mfs.get("X_avg_quant").get(i); 
			 }
			Integer Y_avg_quant = 0;
			if(!mfs.get("Y_avg_quant").isEmpty()&&i<mfs.get("Y_avg_quant").size()) { 
				Y_avg_quant= (Integer)mfs.get("Y_avg_quant").get(i); 
			 }
			for(int j = 0; j < O.prod.size(); j++) {
				Date X_date =  O.date.get(j); 
				String X_prod =  O.prod.get(j); 
				Integer X_month =  O.month.get(j); 
				Integer X_year =  O.year.get(j); 
				String X_state =  O.state.get(j); 
				Integer X_quant =  O.quant.get(j); 
				String X_cust =  O.cust.get(j); 
				Integer X_day =  O.day.get(j); 
			if(X_avg_quant!=null&&Y_avg_quant!=null&&true) {
				if(X_prod.equals(prod)&&X_month<month) { 
					 if(sum==null) 
						 sum=0; 
					 if(count==null) 
						 count=0; 
					 sum +=X_quant; 
					count++; 
 				} 
				} 
			} 
			if(count != null&&count != 0) {
				avg = sum/count;
			} 
				mfs.get("X_avg_quant").add(avg); 
 		}


		for(int i = 0; i < O.prod.size(); i++) { 
			Integer sum = null; 
			Integer count = null; 
			Integer avg = null; 
			String prod = (String)mfs.get("prod").get(i); 
			Integer month = (Integer)mfs.get("month").get(i); 
			Integer X_avg_quant = 0;
			if(!mfs.get("X_avg_quant").isEmpty()&&i<mfs.get("X_avg_quant").size()) { 
				X_avg_quant= (Integer)mfs.get("X_avg_quant").get(i); 
			 }
			Integer Y_avg_quant = 0;
			if(!mfs.get("Y_avg_quant").isEmpty()&&i<mfs.get("Y_avg_quant").size()) { 
				Y_avg_quant= (Integer)mfs.get("Y_avg_quant").get(i); 
			 }
			for(int j = 0; j < O.prod.size(); j++) {
				Date Y_date =  O.date.get(j); 
				String Y_prod =  O.prod.get(j); 
				Integer Y_month =  O.month.get(j); 
				Integer Y_year =  O.year.get(j); 
				String Y_state =  O.state.get(j); 
				Integer Y_quant =  O.quant.get(j); 
				String Y_cust =  O.cust.get(j); 
				Integer Y_day =  O.day.get(j); 
			if(X_avg_quant!=null&&Y_avg_quant!=null&&true) {
				if(Y_prod.equals(prod)&&Y_month>month) { 
					 if(sum==null) 
						 sum=0; 
					 if(count==null) 
						 count=0; 
					 sum +=Y_quant; 
					count++; 
 				} 
				} 
			} 
			if(count != null&&count != 0) {
				avg = sum/count;
			} 
				mfs.get("Y_avg_quant").add(avg); 
 		}
	//creating result-structure
	Map<String, List> result = new HashMap<>();
	result.put("prod", new ArrayList<String>());
	result.put("month", new ArrayList<Integer>());
	result.put("X_avg_quant", new ArrayList<Integer>());
	result.put("Y_avg_quant", new ArrayList<Integer>());
	boolean flag = true; 
	for(int i = 0; i<O.prod.size();i++) { 
 		for(int j = 0; j<result.get("prod").size();j++) { 
 			 if(result.get("prod").get(j).equals(mfs.get("prod").get(i))&&result.get("month").get(j).equals(mfs.get("month").get(i))) {
				flag = false; 
				break; 
			}else { 
				 flag = true;			}; 
		} 
		Integer X_avg_quant=(		Integer)mfs.get("X_avg_quant").get(i);
		Integer Y_avg_quant=(		Integer)mfs.get("Y_avg_quant").get(i);
		try{
		if(flag) { 
			result.get("prod").add(mfs.get("prod").get(i)); 
			result.get("month").add(mfs.get("month").get(i)); 
			result.get("X_avg_quant").add(mfs.get("X_avg_quant").get(i)); 
			result.get("Y_avg_quant").add(mfs.get("Y_avg_quant").get(i)); 
		} 
		}catch(NullPointerException e){
			continue;
		}
		 flag = true; 
	} 
		//Print the data on the console 
		List<String> column_name = new ArrayList<>(); 
		column_name.add("prod"); 
		column_name.add("month"); 
		column_name.add("X_avg_quant"); 
		column_name.add("Y_avg_quant"); 
		StringBuffer stringBuffer = new StringBuffer(); 
		StringBuffer line = new StringBuffer(); 
		//Print the header
		for(int i=0;i<column_name.size();i++) {
			stringBuffer.append("----").append(column_name.get(i));
			line.append("=============");
		}
		System.out.println(stringBuffer); 
		System.out.println(line); 
		//Iterate through the data and print the data as a line
		for(int i=0;i<result.get("month").size();i++) {
			if(result.get("month").get(i)!=null&&result.get("X_avg_quant").get(i)!=null&&result.get("Y_avg_quant").get(i)!=null) {
				System.out.println("----"+result.get("prod").get(i)+"----"+result.get("month").get(i)+"----"+result.get("X_avg_quant").get(i)+"----"+result.get("Y_avg_quant").get(i)+"----");
			}
		}
	}
}
