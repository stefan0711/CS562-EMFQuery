package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class TranslateUtil {
	/**
	 *
	 * @param querry Lines of SQL sentence to be translated
	 * @implNote it is designed to translate SQL into EMF form
	 */
	public static List<List<String>> transSQLFile(List<String> querry) {
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
			e.printStackTrace();
		}
		All_Order.add(S_Order);
		All_Order.add(Order);
		return All_Order;
	}

	/**
	 * 
	 * @param attributes
	 * @return a HashMap with GROUPING VARIABLES as key, and output are like avg_quant or just quant if there are no function
	 */
	public static HashMap<String,String> transAttrs(String[] attributes){
		HashMap<String,String> t_attributes = new HashMap<>();
		String temp = "";
		String variableName = "";
		String groupByAttri = "";
		Matcher m = null;
		for(int i=0; i<attributes.length; i++) {
			attributes[i] = attributes[i].replaceAll(" ", "");
			//if the attribute is a aggregate function
			if(attributes[i].contains("(")) {
				//matching pattern like avg(x.quant) or avg(quant)
				Pattern p1 = Pattern.compile("([a-zA-Z]+)([(])([a-zA-Z0-9]+)([.]*)([a-zA-Z0-9|*]*)([)])");
				m = p1.matcher(attributes[i]);
				m.find();
				temp = m.group(1) + "_";
				if(attributes[i].contains(".")) {
					variableName = m.group(3);
					temp = temp + m.group(5);
					//if there are multiple attribute link to same GROUPING VARIABLES
					if(t_attributes.containsKey(variableName)) {
						temp = t_attributes.get(variableName) + "," + temp;
					}
					t_attributes.put(variableName, temp);
				}
				else {
					temp = temp + m.group(3);
					//if there are multiple attribute link to same GROUPING VARIABLES
					if(t_attributes.containsKey("O")) {
						temp = t_attributes.get("O") + "," + temp;
					}
					t_attributes.put("O", temp);
					
				}
			}
			else {
				//if the attribute is a data of grouping variable like x.quant
				if(attributes[i].contains(".")) {
					//matching x.quant
					Pattern p2 = Pattern.compile("([a-zA-Z0-9]+)([.])([a-zA-Z0-9|*]+)");
					m = p2.matcher(attributes[i]);
					m.find();
					variableName = m.group(1);
					temp = m.group(3);
					//if there are multiple attribute link to same GROUPING VARIABLES
					if(t_attributes.containsKey(variableName)) {
						temp = t_attributes.get(variableName) + "," + temp;
					}
					
					t_attributes.put(variableName, temp);
				}
				else {
					groupByAttri = groupByAttri+attributes[i];
					//if there are multiple attribute link to same GROUPING VARIABLES
					if(t_attributes.containsKey("O")) {
						groupByAttri = t_attributes.get("O") + "," + groupByAttri;
					}
					t_attributes.put("O", groupByAttri);
					groupByAttri = "";
				}
			}
		}
		System.out.println("------------99999"+t_attributes);
		return t_attributes;
	}
	
	/**
	 * 
	 * @param attributes
	 * @return a HashMap with GROUPING VARIABLES as key, and function are 2*i and column are 2*i+1, and the function like avg(quant) are stored in key 0
	 */
	public static HashMap<String,String> transFVECT(String[] attributes){
		HashMap<String,String> t_attributes = new HashMap<>();
		t_attributes = transAttrs(attributes);
		return t_attributes;
	}
	
	/**
	 * 
	 * @param Conditions
	 * @return a string that can just be used in if logic and you can use groupingVariable or O to retrive it
	 */
	public static HashMap<String,String> transCon(String[] Conditions){
		HashMap<String,String> t_Conditions = new HashMap<>();
		if(Conditions[0].isEmpty())
			return t_Conditions;
		String groupingVariable = "";
		String column = "";
		String[] sub = {};
		List<String> logic = new ArrayList<>();
		String[] subJudge = {};
		String left = "";
		String right = "";
		String oper = "";
		String left_t = "";
		String right_t = "";
		String temp = "";
		Queue<String> left_logic = new LinkedList<>();
		Queue<String> right_logic = new LinkedList<>();
		//match pattern like x.qunant >= .....
		Pattern gv = Pattern.compile("([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([=><!][=]*|[<>]*)(.*)");
		Matcher m = null;
		Matcher n = null;
		//replace the logic function into the logic that java can understand 
		for(int i=0; i<Conditions.length; i++) {
			Conditions[i] = Conditions[i].replaceAll("=", "==").replaceAll(">==", ">=").replaceAll("<==", "<=").replaceAll("<>", "!=").replaceAll(" and | AND ", "&&").replaceAll(" or | OR ", "\\|\\|").replaceAll(" ", "");
			//find the logic and save it
			for(int j=0; j<Conditions[i].length()-2;j++) {
				if(Conditions[i].substring(j, j+2).matches("&&")||Conditions[i].substring(j, j+2).matches("\\|\\|")) {
					logic.add(Conditions[i].substring(j, j+2));
				}
			}
			//split the condition by && or ||
			subJudge = Conditions[i].split("&&|\\|\\|");
			for(int j = 0; j<subJudge.length; j++) {
				//seperate the condition by operation
				sub = subJudge[j].split("==|>=|<=|!=|>|<");
				Pattern p3 = Pattern.compile(".*([=><!][=]|[<>]).*");
				m = p3.matcher(subJudge[j]);
				m.find();
				oper = m.group(1);
				left = sub[0];
				right = sub[1];
				
				left_t = "";
				right_t = "";
				//further separate left and right part
				//find all the operation in left part
				Pattern p4 = Pattern.compile("([-+*/])");
				m = p4.matcher(left);
				while(m.find())
					left_logic.add(m.group(1));
				//find all the operation in right part
				m = p4.matcher(right);
				while(m.find())
					right_logic.add(m.group(1));
				//find the number or things like x.quant or avg(quant) or avg(x.quant) in the left part, change avg(x.quant) into x_avg_quant
				Pattern p5 = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)");
				m = p5.matcher(left);
				while(m.find()) {
					temp = m.group(2);
					//if pattern match x.quant or avg(x.quant)
					Pattern p6 = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()");
					n = p6.matcher(temp);
					if(n.find()) {
						//if pattern match avg(x.quant)
						Pattern p7 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()");
						n = p7.matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
							groupingVariable = n.group(4);
						}
						else {
							Pattern p8 = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()");
							n = p8.matcher(temp);
							if(n.find()) {
								temp = n.group(2) + "_" + n.group(4);
								groupingVariable = n.group(2);
							}
								
						}
					}
					//if pattern match avg(quant)
					Pattern p9 = Pattern.compile("()([A-Za-z0-9]+)([(])([a-zA-Z0-9|*]+)([)])()");
					n = p9.matcher(temp);
					if(n.find()) {
						temp = "O_" + n.group(2) + "_" + n.group(4);
						groupingVariable = "O";
					}
					
					left_t = left_t+temp;
					if(!left_logic.isEmpty())
						left_t = left_t+left_logic.poll();
				}
				//do the same for the right part
				left = left_t;
				m = p5.matcher(right);
				while(m.find()) {
					temp = m.group(2);
					Pattern p6 = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()");
					n = p6.matcher(temp);
					if(n.find()) {
						Pattern p7 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()");
						n = p7.matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							Pattern p8 = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()");
							n = p8.matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					Pattern p9 = Pattern.compile("()([A-Za-z0-9]+)([(])([a-zA-Z0-9|*]+)([)])()");
					n = p9.matcher(temp);
					if(n.find()) {
						temp = "O_" + n.group(2) + "_" + n.group(4);
					}
					right_t = right_t+temp;
					if(!right_logic.isEmpty())
						right_t = right_t+right_logic.poll();
				}
				right = right_t;
				//change == or != into .equals() or !.equals() so that String can use it
				if(oper.contains("=="))
					column = column + left + ".equals(" + right + ")";
				else if(oper.contains("!="))
					column = "!" + column + left + ".equals(" + right + ")";
				else
					column = column + left + oper + right;
				if(j < logic.size())
					column = column + logic.get(j);
			}
			t_Conditions.put(groupingVariable,column);
			column ="";
			logic.clear();
		}
		return t_Conditions;
	}
	
	/**
	 * 
	 * @param Conditions
	 * @return stringArray contain logic can be used in if, find by loop.
	 */
	public static List<String> transHavingCon(String[] Conditions){
		
		String t_Conditions = "";
		List<String> Array_Conditions = new ArrayList<String>();
		if(Conditions[0].isEmpty())
			return Array_Conditions;
		String[] subJudge = {};
		String[] sub = {};
		List<String> logic = new ArrayList<>();
		String left = "";
		String right = "";
		String oper = "";
		String left_t = "";
		String right_t = "";
		String temp = "";
		Queue<String> left_logic = new LinkedList<>();
		Queue<String> right_logic = new LinkedList<>();
		Pattern p3 = Pattern.compile(".*([=><!][=]|[<>]).*");
		Matcher m = null;
		Matcher n = null;
		for(int i=0; i<Conditions.length; i++) {
			Conditions[i] = Conditions[i].replaceAll("=", "==").replaceAll(">==", ">=").replaceAll("<==", "<=").replaceAll("<>", "!=").replaceAll(" and | AND ", "&&").replaceAll(" or | OR ", "\\|\\|").replaceAll(" ", "");
			//find the logic and save it
			for(int j=0; j<Conditions[i].length()-2;j++) {
				if(Conditions[i].substring(j, j+2).matches("&&")||Conditions[i].substring(j, j+2).matches("\\|\\|")) {
					logic.add(Conditions[i].substring(j, j+2));
				}
			}
			//split the condition by && or ||
			subJudge = Conditions[i].split("&&|\\|\\|");
			for(int j=0; j<subJudge.length; j++) {
				//seperate the condition by operation
				sub = subJudge[j].split("==|>=|<=|!=|>|<");
				m = p3.matcher(subJudge[j]);
				m.find();
				oper = m.group(1);
				left = sub[0];
				right = sub[1];
				left_t = "";
				right_t = "";
				
				//further separate left and right part
				//find all the operation in left part
				Pattern p4 = Pattern.compile("([-+*/])");
				m = p4.matcher(left);
				while(m.find())
					left_logic.add(m.group(1));
				m = p4.matcher(right);
				while(m.find())
					right_logic.add(m.group(1));
				Pattern p5 = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)");
				m = p5.matcher(left);
				while(m.find()) {
					temp = m.group(2);
					Pattern p6 = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()");
					n = p6.matcher(temp);
					if(n.find()) {
						Pattern p7 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()");
						n = p7.matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							Pattern p8 = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()");
							n = p8.matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					Pattern p10 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([)])()");
					n = p10.matcher(temp);
					if(n.find())
						temp = "O_" + n.group(2) + "_" + n.group(4);
					left_t = left_t+temp;
					if(!left_logic.isEmpty())
						left_t = left_t+left_logic.poll();
				}
				left = left_t;
				m = p5.matcher(right);
				//find the number or things like x.quant or avg(quant) or avg(x.quant) in the left part, change avg(x.quant) into x_avg_quant
				while(m.find()) {
					temp = m.group(2);
					Pattern p6 = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()");
					n = p6.matcher(temp);
					if(n.find()) {
						Pattern p7 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()");
						n = p7.matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							Pattern p8 = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()");
							n = p8.matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					Pattern p10 = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([)])()");
					n = p10.matcher(temp);
					if(n.find())
						temp = "O_" + n.group(2) + "_" + n.group(4);
					right_t = right_t+temp;
					if(!right_logic.isEmpty())
						right_t = right_t+right_logic.poll();
				}
				right = right_t;
				
				if(j < logic.size())
					t_Conditions = t_Conditions + left + oper + right + logic.get(j);
				else
					t_Conditions = t_Conditions + left + oper + right;
			}
			
			Array_Conditions.add(t_Conditions);
			t_Conditions = "";
			logic.clear();
		}
		return Array_Conditions;
	}
}
