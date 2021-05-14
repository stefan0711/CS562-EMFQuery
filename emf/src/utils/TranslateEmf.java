package utils;

import java.util.*;
import java.util.regex.*;

public class TranslateEmf {
	/**
	 * 
	 * @param attributes
	 * @return a HashMap with GROUPING VARIABLES as key, and output are like avg_quant or just quant if there are no function
	 */
	public static HashMap<String,String> translateAttributes(String[] attributes){
		HashMap<String,String> t_attributes = new HashMap<>();
		String temp = "";
		String variableName = "";
		String groupByAttri = "";
		Matcher m = null;
		for(int i=0; i<attributes.length;i++) {
			attributes[i] = attributes[i].replaceAll(" ", "");
			//if the attribute is a aggregate function
			if(attributes[i].contains("(")) {
				//matching pattern like avg(x.quant) or avg(quant)
				m = Pattern.compile("([a-zA-Z]+)([(])([a-zA-Z0-9]+)([.]*)([a-zA-Z0-9|*]*)([)])").matcher(attributes[i]);
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
					m = Pattern.compile("([a-zA-Z0-9]+)([.])([a-zA-Z0-9|*]+)").matcher(attributes[i]);
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
		return t_attributes;
	}
	
	/**
	 * 
	 * @param attributes
	 * @return a HashMap with GROUPING VARIABLES as key, and function are 2*i and column are 2*i+1, and the function like avg(quant) are stored in key 0
	 */
	public static HashMap<String,String> translateFVECT(String[] attributes){
		HashMap<String,String> t_attributes = new HashMap<>();
		t_attributes = translateAttributes(attributes);
		return t_attributes;
	}
	
	/**
	 * 
	 * @param Conditions
	 * @return a string that can just be used in if logic and you can use groupingVariable or O to retrive it
	 */
	public static HashMap<String,String> translateCondition(String[] Conditions){
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
				m = Pattern.compile(".*([=><!][=]|[<>]).*").matcher(subJudge[j]);
				m.find();
				oper = m.group(1);
				left = sub[0];
				right = sub[1];
				
				left_t = "";
				right_t = "";
				//further separate left and right part
				//find all the operation in left part
				m = Pattern.compile("([-+*/])").matcher(left);
				while(m.find())
					left_logic.add(m.group(1));
				//find all the operation in right part
				m = Pattern.compile("([-+*/])").matcher(right);
				while(m.find())
					right_logic.add(m.group(1));
				//find the number or things like x.quant or avg(quant) or avg(x.quant) in the left part, change avg(x.quant) into x_avg_quant
				m = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)").matcher(left);
				while(m.find()) {
					temp = m.group(2);
					//if pattern match x.quant or avg(x.quant)
					n = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()").matcher(temp);
					if(n.find()) {
						//if pattern match avg(x.quant)
						n = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
							groupingVariable = n.group(4);
						}
						else {
							n = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()").matcher(temp);
							if(n.find()) {
								temp = n.group(2) + "_" + n.group(4);
								groupingVariable = n.group(2);
							}
								
						}
					}
					//if pattern match avg(quant)
					n = Pattern.compile("()([A-Za-z0-9]+)([(])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
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
				m = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)").matcher(right);
				while(m.find()) {
					temp = m.group(2);
					n = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()").matcher(temp);
					if(n.find()) {
						n = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							n = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()").matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					n = Pattern.compile("()([A-Za-z0-9]+)([(])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
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
	public static List<String> translateHavingCondition(String[] Conditions){
		
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
		Pattern pattern = Pattern.compile(".*([=><!][=]|[<>]).*");
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
				m = pattern.matcher(subJudge[j]);
				m.find();
				oper = m.group(1);
				left = sub[0];
				right = sub[1];
				left_t = "";
				right_t = "";
				
				//further separate left and right part
				//find all the operation in left part
				m = Pattern.compile("([-+*/])").matcher(left);
				while(m.find())
					left_logic.add(m.group(1));
				m = Pattern.compile("([-+*/])").matcher(right);
				while(m.find())
					right_logic.add(m.group(1));
				m = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)").matcher(left);
				while(m.find()) {
					temp = m.group(2);
					n = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()").matcher(temp);
					if(n.find()) {
						n = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							n = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()").matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					n = Pattern.compile("()([A-Za-z0-9]+)([(])([a-zA-Z0-9]+)([)])()").matcher(temp);
					if(n.find())
						temp = "O_" + n.group(2) + "_" + n.group(4);
					left_t = left_t+temp;
					if(!left_logic.isEmpty())
						left_t = left_t+left_logic.poll();
				}
				left = left_t;
				m = Pattern.compile("([-+*/]*)([^-+*/]+)([-+*/]*)").matcher(right);
				//find the number or things like x.quant or avg(quant) or avg(x.quant) in the left part, change avg(x.quant) into x_avg_quant
				while(m.find()) {
					temp = m.group(2);
					n = Pattern.compile("()([A-Za-z0-9]+)([(]*)([A-Za-z0-9]*)([.])([a-zA-Z0-9|*]*)([)]*)()").matcher(temp);
					if(n.find()) {
						n = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)([)])()").matcher(temp);
						if(n.find()) {
							temp = n.group(4) + "_" + n.group(2) + "_" + n.group(6);
						}
						else {
							n = Pattern.compile("()([A-Za-z0-9]+)([.])([a-zA-Z0-9|*]+)()").matcher(temp);
							if(n.find())
								temp = n.group(2) + "_" + n.group(4);
						}
					}
					n = Pattern.compile("()([A-Za-z0-9]+)([(])([A-Za-z0-9]+)([)])()").matcher(temp);
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
