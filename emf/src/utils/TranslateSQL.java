package utils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.io.*;

public class TranslateSQL {
	/**
	 * 
	 * @param querry Lines of SQL sentence to be translated 
	 * @implNote it is designed to translate SQL into EMF form
	 */
	public static List<List<String>> translate(List<String> querry) {
		Matcher m1 = null;
		String key1 = "select";
		String key2 = "from";
		String key3 = "group by";
		String key4 = "such that";
		String key5 = "having";
		String KEY1 = "SELECT";
		String KEY2 = "FROM";
		String KEY3 = "GROUP BY";
		String KEY4 = "SUCH THAT";
		String KEY5 = "HAVING";
		String Select_Att = "";
		int Num_Group_Variables = 0;
		String Group_Att = "";
		String Aggregate_function = "";
		String Condition_Vect = "";
		String Having_Condition = "";
		String[] temp = {};
		List<String> Order = new ArrayList<>();
		List<String> S_Order = new ArrayList<>();
		List<List<String>> All_Order = new ArrayList<>();
		Order.add("O");
		Matcher m2 = null;
		for(String line:querry) {
			if(line.contains(key1)||line.contains(KEY1)) {
				Pattern p1 = Pattern.compile("(select\\s+)(.*)");
				m1 = p1.matcher(line);
				m1.find();
				Select_Att = m1.group(2);
				temp = m1.group(2).replaceAll(" ", "").split(",");
				for(String i:temp) {
					Pattern p2 = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.]*)([a-zA-Z0-9|*]*)([)]*)()");
					m2 = p2.matcher(i);
					if(m2.find()) {
						Pattern p3 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9|*]+)([.]*)([a-zA-Z0-9|*]*)([)])()");
						m2 = p3.matcher(i);
						if(m2.find()) {
							if(m2.group(5).equals(".")) {
								S_Order.add(m2.group(4) + "_" + m2.group(2) + "_" + m2.group(6));
							}
							else {
								S_Order.add("O_" + m2.group(2) + "_" + m2.group(4));
							}
						}
						else {
							Pattern p4 = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()");
							m2 = p4.matcher(i);
							if(m2.find()) {
								S_Order.add(m2.group(2)+"_"+m2.group(4));
							}
							else {
								S_Order.add(i);
							}
						}
					}
					

				}
			}
			else if(line.contains(key3)||line.contains(KEY3)) {
				Pattern p5 = Pattern.compile("(group by\\s+)(.*)([;])(.*)");
				m1 = p5.matcher(line);
				m1.find();
				Group_Att = m1.group(2);
				Num_Group_Variables = m1.group(4).split(",").length;
				temp = m1.group(4).replaceAll(" ", "").split(",");
				for(String i:temp) {
					Order.add(i);
				}
			}
			else if(line.contains(key4)||line.contains(KEY4)) {
				Pattern p6 = Pattern.compile("(such that\\s+)(.*)");
				m1 = p6.matcher(line);
				m1.find();
				Condition_Vect = m1.group(2);
			}
			else if(line.contains(key5)||line.contains(KEY5)) {
				Pattern p7 = Pattern.compile("(having\\s+)(.*)");
				m1 = p7.matcher(line);
				m1.find();
				Having_Condition = m1.group(2);
			}
		}
		//find all the grouping variable aggregate in such as
		Pattern p8 = Pattern.compile("([^A-Za-z0-9]*)([A-Za-z0-9]+[(][A-Za-z0-9]*[.]*[A-Za-z0-9]+[)])([^A-Za-z0-9]*)");
		m1 = p8.matcher(Condition_Vect);
		while(m1.find()) {
			if(m1.hitEnd()) {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2) + ",";
				}
			}
			else {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2) + ",";
				}
			}
		}
		//find all the grouping variable aggregate in having
		m1 = p8.matcher(Having_Condition);
		while(m1.find()) {
			if(m1.hitEnd()) {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2) + ",";
				}
			}
			else {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2) + ",";
				}
			}
		}
		//find all the grouping variable aggregate in select
		Pattern p9 = Pattern.compile("([^A-Za-z0-9]*)([A-Za-z0-9]+[(][A-Za-z0-9]*[.]*[A-Za-z0-9|*]+[)])([^A-Za-z0-9]*)");
		m1 = p9.matcher(Select_Att);
		while(m1.find()) {
			if(m1.hitEnd()) {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2);
				}
			}
			else {
				if(!Aggregate_function.contains(m1.group(2))) {
					Aggregate_function = Aggregate_function + m1.group(2) + ",";
				}
			}
		}
		Condition_Vect = Condition_Vect.replaceAll("'", "\"");
		Having_Condition = Having_Condition.replaceAll("'", "\"");
		File file = new File("emf/Example 1.txt");
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file); 
			writer.write("// SELECT ATTRIBUTE(S):\n");
			writer.write(Select_Att+"\n");
			writer.write("// NUMBER OF GROUPING VARIABLES(n):\n");
			writer.write(Num_Group_Variables+"\n");
			writer.write("// GROUPING ATTRIBUTES(V):\n");
			writer.write(Group_Att+"\n");
			writer.write("// F-VECT([F]):\n");
			writer.write(Aggregate_function+"\n");
			writer.write("// SELECT CONDITION-VECT([Sigma]):\n");
			writer.write(Condition_Vect+"\n");
			writer.write("// HAVING CONDITION(G):\n");
			writer.write(Having_Condition+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		All_Order.add(S_Order);
		All_Order.add(Order);
		return All_Order;
	}
}
