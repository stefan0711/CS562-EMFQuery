package output;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.io.*;
import utils.JDBCUtil;
import org.apache.poi.ss.usermodel.Cell; 
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.xssf.usermodel.XSSFSheet; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

public class Query {
	public static class Grouping_variable_x{
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
	public static class Grouping_variable_y{
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
	public static class Grouping_variable_z{
		public List<Date> date = new ArrayList<>();
		public List<String> prod = new ArrayList<>();
		public List<Integer> month = new ArrayList<>();
		public List<Integer> year = new ArrayList<>();
		public List<String> state = new ArrayList<>();
		public List<Integer> quant = new ArrayList<>();
		public List<String> cust = new ArrayList<>();
		public List<Integer> day = new ArrayList<>();
		public List<Integer> count_quant = new ArrayList<>();
	}
	public static class Grouping_variable_O{
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
	Grouping_variable_O O = new Grouping_variable_O();
	Grouping_variable_x x = new Grouping_variable_x();
	Grouping_variable_y y = new Grouping_variable_y();
	Grouping_variable_z z = new Grouping_variable_z();
	ResultSet rs = null;

	//creating mfs-structure
	Map<String, List> mfs = new HashMap<>();
	mfs.put("prod", new ArrayList<String>());
	mfs.put("month", new ArrayList<Integer>());
	mfs.put("x_avg_quant", new ArrayList<Integer>());
	mfs.put("y_avg_quant", new ArrayList<Integer>());
	mfs.put("z_count_quant", new ArrayList<Integer>());
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
			//load data to attribute z
				z.date.add((Date)rs.getObject("date"));
				z.prod.add((String)rs.getObject("prod"));
				z.month.add((Integer)rs.getObject("month"));
				z.year.add((Integer)rs.getObject("year"));
				z.state.add((String)rs.getObject("state"));
				z.quant.add((Integer)rs.getObject("quant"));
				z.cust.add((String)rs.getObject("cust"));
				z.day.add((Integer)rs.getObject("day"));
			//load data to attribute O 
				O.date.add((Date)rs.getObject("date"));
				O.prod.add((String)rs.getObject("prod"));
				O.month.add((Integer)rs.getObject("month"));
				O.year.add((Integer)rs.getObject("year"));
				O.state.add((String)rs.getObject("state"));
				O.quant.add((Integer)rs.getObject("quant"));
				O.cust.add((String)rs.getObject("cust"));
				O.day.add((Integer)rs.getObject("day"));
			//load data to grouping_variable 
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
			Integer x_avg_quant = 0;
			if(!mfs.get("x_avg_quant").isEmpty()&&i<mfs.get("x_avg_quant").size()) { 
				x_avg_quant= (Integer)mfs.get("x_avg_quant").get(i); 
			 }
			Integer y_avg_quant = 0;
			if(!mfs.get("y_avg_quant").isEmpty()&&i<mfs.get("y_avg_quant").size()) { 
				y_avg_quant= (Integer)mfs.get("y_avg_quant").get(i); 
			 }
			Integer z_count_quant = 0;
			if(!mfs.get("z_count_quant").isEmpty()&&i<mfs.get("z_count_quant").size()) { 
				z_count_quant= (Integer)mfs.get("z_count_quant").get(i); 
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
			if(x_avg_quant!=null&&y_avg_quant!=null&&z_count_quant!=null&&true) {
				if(x_prod.equals(prod)&&x_month.equals(month-1)) { 
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
			String prod = (String)mfs.get("prod").get(i); 
			Integer month = (Integer)mfs.get("month").get(i); 
			Integer x_avg_quant = 0;
			if(!mfs.get("x_avg_quant").isEmpty()&&i<mfs.get("x_avg_quant").size()) { 
				x_avg_quant= (Integer)mfs.get("x_avg_quant").get(i); 
			 }
			Integer y_avg_quant = 0;
			if(!mfs.get("y_avg_quant").isEmpty()&&i<mfs.get("y_avg_quant").size()) { 
				y_avg_quant= (Integer)mfs.get("y_avg_quant").get(i); 
			 }
			Integer z_count_quant = 0;
			if(!mfs.get("z_count_quant").isEmpty()&&i<mfs.get("z_count_quant").size()) { 
				z_count_quant= (Integer)mfs.get("z_count_quant").get(i); 
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
			if(x_avg_quant!=null&&y_avg_quant!=null&&z_count_quant!=null&&true) {
				if(y_prod.equals(prod)&&y_month.equals(month+1)) { 
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


		for(int i = 0; i < O.prod.size(); i++) { 
			Integer sum = null; 
			Integer count = null; 
			Integer avg = null; 
			String prod = (String)mfs.get("prod").get(i); 
			Integer month = (Integer)mfs.get("month").get(i); 
			Integer x_avg_quant = 0;
			if(!mfs.get("x_avg_quant").isEmpty()&&i<mfs.get("x_avg_quant").size()) { 
				x_avg_quant= (Integer)mfs.get("x_avg_quant").get(i); 
			 }
			Integer y_avg_quant = 0;
			if(!mfs.get("y_avg_quant").isEmpty()&&i<mfs.get("y_avg_quant").size()) { 
				y_avg_quant= (Integer)mfs.get("y_avg_quant").get(i); 
			 }
			Integer z_count_quant = 0;
			if(!mfs.get("z_count_quant").isEmpty()&&i<mfs.get("z_count_quant").size()) { 
				z_count_quant= (Integer)mfs.get("z_count_quant").get(i); 
			 }
			for(int j = 0; j < O.prod.size(); j++) {
				Date z_date =  O.date.get(j); 
				String z_prod =  O.prod.get(j); 
				Integer z_month =  O.month.get(j); 
				Integer z_year =  O.year.get(j); 
				String z_state =  O.state.get(j); 
				Integer z_quant =  O.quant.get(j); 
				String z_cust =  O.cust.get(j); 
				Integer z_day =  O.day.get(j); 
			if(x_avg_quant!=null&&y_avg_quant!=null&&z_count_quant!=null&&true) {
				if(z_prod.equals(prod)&&z_month.equals(month)&&z_quant>x_avg_quant&&z_quant<y_avg_quant) { 
					 if(sum==null) 
						 sum=0; 
					 if(count==null) 
						 count=0; 
					 sum +=z_quant; 
					count++; 
 				} 
				} 
			} 
			if(count != null&&count != 0) {
				avg = sum/count;
			} 
				mfs.get("z_count_quant").add(count); 
 		}
	//creating result-structure
	Map<String, List> result = new HashMap<>();
	result.put("prod", new ArrayList<String>());
	result.put("month", new ArrayList<Integer>());
	result.put("x_avg_quant", new ArrayList<Integer>());
	result.put("y_avg_quant", new ArrayList<Integer>());
	result.put("z_count_quant", new ArrayList<Integer>());
	boolean flag = true; 
	for(int i = 0; i<O.prod.size();i++) { 
 		for(int j = 0; j<result.get("prod").size();j++) { 
 			 if(result.get("prod").get(j).equals(mfs.get("prod").get(i))&&result.get("month").get(j).equals(mfs.get("month").get(i))) {
				flag = false; 
				break; 
			}else { 
				 flag = true;			}; 
		} 
		Integer x_avg_quant=(		Integer)mfs.get("x_avg_quant").get(i);
		Integer y_avg_quant=(		Integer)mfs.get("y_avg_quant").get(i);
		Integer z_count_quant=(		Integer)mfs.get("z_count_quant").get(i);
		try{
		if(flag) { 
			result.get("prod").add(mfs.get("prod").get(i)); 
			result.get("month").add(mfs.get("month").get(i)); 
			result.get("x_avg_quant").add(mfs.get("x_avg_quant").get(i)); 
			result.get("y_avg_quant").add(mfs.get("y_avg_quant").get(i)); 
			result.get("z_count_quant").add(mfs.get("z_count_quant").get(i)); 
		} 
		}catch(NullPointerException e){
			continue;
		}
		 flag = true; 
	} 
		 try {
			XSSFWorkbook workbook = new XSSFWorkbook(); 
			XSSFSheet sheet = workbook.createSheet("table result"); 

			FileOutputStream out = new FileOutputStream(new File("result.xlsx")); 
			List<String> column_name = new ArrayList<>(); 
			List<List<Object>> data = new ArrayList<>(); 
 			column_name.add("prod"); 
			data.add(result.get("prod")); 
			column_name.add("month"); 
			data.add(result.get("month")); 
			column_name.add("z_count_quant"); 
			data.add(result.get("z_count_quant")); 
			int rownum = 0;
			int cellnum = 0;
			int flag_null_value = 0;
			Row row = sheet.createRow(rownum++);
			for(int i=0;i<column_name.size();i++) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue(column_name.get(i));
			}
			cellnum=0;
			for(int i=0;i<data.get(0).size();i++) {
				row = sheet.createRow(rownum++);
				for(int j=0;j<column_name.size();j++) {
					if(flag_null_value==1)
						continue;
					if(data.get(j).get(i)==null) {
						sheet.removeRow(row);
						rownum--;
						flag_null_value=1;
						continue;
					}
					Cell cell = row.createCell(cellnum++);
					if(data.get(j).get(i) instanceof String)
						cell.setCellValue((String)data.get(j).get(i));
					else if(data.get(j).get(i) instanceof Integer)
						cell.setCellValue((Integer)data.get(j).get(i));
				}
				cellnum = 0;
				flag_null_value = 0;
			}
			workbook.write(out);
			out.close();
			System.out.println("result.xlsx written successfully on disk.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
