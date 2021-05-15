package output;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.io.*;
import utils.JDBCUtil;

public class Start {
	public static class groupingVariable_x{
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
	public static class groupingVariable_y{
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
	groupingVariable_x x = new groupingVariable_x();
	groupingVariable_y y = new groupingVariable_y();
	ResultSet rs = null;

	//creating mfs-structure
	Map<String, List> mfs = new HashMap<>();
	mfs.put("cust", new ArrayList<String>());
	mfs.put("prod", new ArrayList<String>());
	mfs.put("x_avg_quant", new ArrayList<Integer>());
	mfs.put("y_avg_quant", new ArrayList<Integer>());
		try {
			System.out.println("Database: Loading successful.");
			JDBCUtil db = new JDBCUtil();
			Connection conn = db.connectToDatabase();
			Statement stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM sales");
			while(rs.next()){
			//load data to attribute x
				x.date.add((Date)rs.getObject("date"));
				x.prod.add((String)rs.getObject("prod"));
				x.month.add((Integer)rs.getObject("month"));
				x.year.add((Integer)rs.getObject("year"));
				x.state.add((String)rs.getObject("state"));
				x.quant.add((Integer)rs.getObject("quant"));
				x.cust.add((String)rs.getObject("cust"));
				x.day.add((Integer)rs.getObject("day"));
			//load data to attribute y
				y.date.add((Date)rs.getObject("date"));
				y.prod.add((String)rs.getObject("prod"));
				y.month.add((Integer)rs.getObject("month"));
				y.year.add((Integer)rs.getObject("year"));
				y.state.add((String)rs.getObject("state"));
				y.quant.add((Integer)rs.getObject("quant"));
				y.cust.add((String)rs.getObject("cust"));
				y.day.add((Integer)rs.getObject("day"));
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
				mfs.get("cust").add((String)rs.getObject("cust")); 
				mfs.get("prod").add((String)rs.getObject("prod")); 
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
			String cust = (String)mfs.get("cust").get(i); 
			String prod = (String)mfs.get("prod").get(i); 
			Integer x_avg_quant = 0;
			if(!mfs.get("x_avg_quant").isEmpty()&&i<mfs.get("x_avg_quant").size()) { 
				x_avg_quant= (Integer)mfs.get("x_avg_quant").get(i); 
			 }
			Integer y_avg_quant = 0;
			if(!mfs.get("y_avg_quant").isEmpty()&&i<mfs.get("y_avg_quant").size()) { 
				y_avg_quant= (Integer)mfs.get("y_avg_quant").get(i); 
			 }
			for(int j = 0; j < O.prod.size(); j++) {
				Date x_date =  O.date.get(j); 
				String x_prod =  O.prod.get(j); 
				Integer x_month =  O.month.get(j); 
				Integer x_year =  O.year.get(j); 
				String x_state =  O.state.get(j); 
				Integer x_quant =  O.quant.get(j); 
				String x_cust =  O.cust.get(j); 
				Integer x_day =  O.day.get(j); 
			if(x_avg_quant!=null&&y_avg_quant!=null&&true) {
				if(x_cust.equals(cust)&&x_prod.equals(prod)) { 
					 if(sum==null) 
						 sum=0; 
					 if(count==null) 
						 count=0; 
					 sum +=x_quant; 
					count++; 
 				} 
				} 
			} 
			if(count != null&&count != 0) {
				avg = sum/count;
			} 
				mfs.get("x_avg_quant").add(avg); 
 		}


		for(int i = 0; i < O.prod.size(); i++) { 
			Integer sum = null; 
			Integer count = null; 
			Integer avg = null; 
			String cust = (String)mfs.get("cust").get(i); 
			String prod = (String)mfs.get("prod").get(i); 
			Integer x_avg_quant = 0;
			if(!mfs.get("x_avg_quant").isEmpty()&&i<mfs.get("x_avg_quant").size()) { 
				x_avg_quant= (Integer)mfs.get("x_avg_quant").get(i); 
			 }
			Integer y_avg_quant = 0;
			if(!mfs.get("y_avg_quant").isEmpty()&&i<mfs.get("y_avg_quant").size()) { 
				y_avg_quant= (Integer)mfs.get("y_avg_quant").get(i); 
			 }
			for(int j = 0; j < O.prod.size(); j++) {
				Date y_date =  O.date.get(j); 
				String y_prod =  O.prod.get(j); 
				Integer y_month =  O.month.get(j); 
				Integer y_year =  O.year.get(j); 
				String y_state =  O.state.get(j); 
				Integer y_quant =  O.quant.get(j); 
				String y_cust =  O.cust.get(j); 
				Integer y_day =  O.day.get(j); 
			if(x_avg_quant!=null&&y_avg_quant!=null&&true) {
				if(!y_cust.equals(cust)&&y_prod.equals(prod)) { 
					 if(sum==null) 
						 sum=0; 
					 if(count==null) 
						 count=0; 
					 sum +=y_quant; 
					count++; 
 				} 
				} 
			} 
			if(count != null&&count != 0) {
				avg = sum/count;
			} 
				mfs.get("y_avg_quant").add(avg); 
 		}
	//creating result-structure
	Map<String, List> result = new HashMap<>();
	result.put("cust", new ArrayList<String>());
	result.put("prod", new ArrayList<String>());
	result.put("x_avg_quant", new ArrayList<Integer>());
	result.put("y_avg_quant", new ArrayList<Integer>());
	boolean flag = true; 
	for(int i = 0; i<O.prod.size();i++) { 
 		for(int j = 0; j<result.get("cust").size();j++) { 
 			 if(result.get("cust").get(j).equals(mfs.get("cust").get(i))&&result.get("prod").get(j).equals(mfs.get("prod").get(i))) {
				flag = false; 
				break; 
			}else { 
				 flag = true;			}; 
		} 
		Integer x_avg_quant=(		Integer)mfs.get("x_avg_quant").get(i);
		Integer y_avg_quant=(		Integer)mfs.get("y_avg_quant").get(i);
		try{
		if(flag) { 
			result.get("cust").add(mfs.get("cust").get(i)); 
			result.get("prod").add(mfs.get("prod").get(i)); 
			result.get("x_avg_quant").add(mfs.get("x_avg_quant").get(i)); 
			result.get("y_avg_quant").add(mfs.get("y_avg_quant").get(i)); 
		} 
		}catch(NullPointerException e){
			continue;
		}
		 flag = true; 
	} 
		//Print the data on the console 
		List<String> column_name = new ArrayList<>(); 
		column_name.add("cust"); 
		column_name.add("prod"); 
		column_name.add("x_avg_quant"); 
		column_name.add("y_avg_quant"); 
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
		for(int i=0;i<result.get("prod").size();i++) {
			if(result.get("prod").get(i)!=null&&result.get("x_avg_quant").get(i)!=null&&result.get("y_avg_quant").get(i)!=null) {
				System.out.println("----"+result.get("cust").get(i)+"----"+result.get("prod").get(i)+"----"+result.get("x_avg_quant").get(i)+"----"+result.get("y_avg_quant").get(i)+"----");
			}
		}
	}
}
