package code;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;

import utils.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProjMain {
    public static void main(String[] args) throws IOException {

        JDBCUtil loader = new JDBCUtil();
        Connection db = loader.connectToDatabase();

        HashMap<String, String> columnNameAndType = JDBCUtil.columnNameAndType(db, "sales");
        System.out.println("Please select the .txt file: ");

        //Select the EMFSQL file using JFilEchooser 默认是本项目
        File current = new File("");
        // To get the path of the current project so that the code can be used on other platforms
        String currentPath = current.getCanonicalPath();
        System.out.println("Current project path: ======> " + currentPath);
        JFileChooser chooser = new JFileChooser(currentPath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
        chooser.setFileFilter(filter);
        // This method displays the box that pops up when we select the file
        int returnVal = chooser.showOpenDialog(null);
        // Get to select the path to the file
        String path = chooser.getSelectedFile().getPath();
        // Triggers if the user clicks OK
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: ======> " + path);
        }

        System.out.print("columnNameAndType: ======> " + columnNameAndType);
        //
        List<String> SQLquery = FileUtil.readFileByLine(path);
        List<List<String>> all_order = TranslateUtil.transSQLFile(SQLquery);
        List<String> order = all_order.get(1);
        List<String> select_order = all_order.get(0);
        System.out.println(order);

        List<String> EMFquery = FileUtil.readFileByLine("emf-query/src/output/TranslatedSQL.txt");
        String selectAttribute = "";
        int numberGrouping = 0;
        String groupingVariable = "";
        String FVECT = "";
        String conditionVect = "";
        String havingCondition = "";


        for (int i = 0; i < EMFquery.size(); i++) {
            if (EMFquery.get(i).contains("SELECT ATTRIBUTE")) {
                selectAttribute = EMFquery.get(i + 1);
            }
            if (EMFquery.get(i).contains("NUMBER OF GROUPING VARIABLES")) {
                numberGrouping = Integer.parseInt(EMFquery.get(i + 1));
            }
            if (EMFquery.get(i).contains("GROUPING ATTRIBUTES")) {
                groupingVariable = EMFquery.get(i + 1);
            }
            if (EMFquery.get(i).contains("F-VECT")) {
                FVECT = EMFquery.get(i + 1);
            }
            if (EMFquery.get(i).contains("SELECT CONDITION-VECT")) {
                conditionVect = EMFquery.get(i + 1);
            }
            if (EMFquery.get(i).contains("HAVING CONDITION")) {
                havingCondition = EMFquery.get(i + 1);
            }
        }

        HashMap<String, String> select_attribute = TranslateUtil.transAttrs(selectAttribute.split(","));
        HashMap<String, String> F_VECT = TranslateUtil.transFVECT(FVECT.split(","));
        HashMap<String, String> condition_vect = TranslateUtil.transCon(conditionVect.split(","));
        List<String> having_condition = TranslateUtil.transHavingCon(havingCondition.split(","));
        String[] grouping_variable = groupingVariable.replaceAll(" ", "").split(",");

        System.out.println("SELECT ATTRIBUTE(S): ======> " + select_attribute);
        System.out.println("GROUPING ATTRIBUTES: ======> " + Arrays.toString(grouping_variable));
        System.out.println("F-VECT: ======> " + F_VECT);
        System.out.println("SELECT CONDITION-VECT: ======> " + condition_vect);
        System.out.println("HAVING CONDITION: ======> " + having_condition);

        //Use the file input stream to generate file write data
        FileWriter writer = null;
        try {
            String className = "Start";
            String pathName = "emf-query/src/output/" + className + ".java";
            File file = new File(pathName);
            if (file.createNewFile()) {
                System.out.println("File: ======> \"" + className + ".java\" created successfully.");
                System.out.println("Path: ======> " + pathName);
            } else {
                System.out.println("File: ======> The file already exists, recreate it");
                System.out.println("Path: ======> " + pathName);
            }

            //write data for file
            writer = new FileWriter(pathName, false);

            writer.write("package output;" + "\n");
            writer.write("import java.sql.*;" + "\n");
            writer.write("import java.sql.Date;" + "\n");
            writer.write("import java.util.*;" + "\n");
            writer.write("import java.io.*;" + "\n");
            writer.write("import utils.JDBCUtil;" + "\n");

            writer.write("\n");
            writer.write("public class " + className + " {" + "\n");
            Matcher m = null;

            //write the storage structure for the grouping variables
            for (Map.Entry<String, String> entry : F_VECT.entrySet()) {
                if (entry.getKey() != "O") {
                    writer.write("\tpublic static class groupingVariable_" + entry.getKey() + "{" + "\n");
                    for (Map.Entry<String, String> basic_column : columnNameAndType.entrySet()) {
                        writer.write("\t\tpublic List<" + basic_column.getValue() + ">" + " " + basic_column.getKey() + " = new ArrayList<>();\n");
                    }
                    for (String aggregate_result : entry.getValue().split(",")) {
                        m = Pattern.compile("()(.+)(_)(.+)").matcher(aggregate_result);
                        m.find();
                        if (!m.group(4).equals("*"))
                            writer.write("\t\tpublic List<" + columnNameAndType.get(m.group(4)) + "> " + aggregate_result + " = new ArrayList<>();\n");
                        else
                            writer.write("\t\tpublic List<Integer> count_x = new ArrayList<>();\n");
                    }
                    writer.write("\t" + "}" + "\n");
                }
            }
            writer.write("\tpublic static class groupingVariable_O" + "{" + "\n");
            for (Map.Entry<String, String> basic_column : columnNameAndType.entrySet()) {
                writer.write("\t\tpublic List<" + basic_column.getValue() + ">" + " " + basic_column.getKey() + " = new ArrayList<>();\n");
            }
            if (F_VECT.containsKey("O")) {
                for (String aggregate_result : F_VECT.get("O").split(",")) {
                    m = Pattern.compile("(.)(_)(.+)").matcher(aggregate_result);
                    m.find();
                    writer.write("\t\tpublic List<" + columnNameAndType.get(m.group(3)) + "> " + aggregate_result + " = new ArrayList<>();\n");
                }
            }
            writer.write("\t" + "}" + "\n");

            //start of main
            writer.write("\tpublic static void main(String[] args) {" + "\n");

            //initialize
            writer.write("\tgroupingVariable_O O = new groupingVariable_O();" + "\n");
            for (Map.Entry<String, String> entry : F_VECT.entrySet()) {
                if (entry.getKey() != "O") {
                    writer.write("\tgroupingVariable_" + entry.getKey() + " " + entry.getKey() + " = new " + "groupingVariable_" + entry.getKey() + "();" + "\n");
                }
            }

            writer.write("\tResultSet rs = null;" + "\n\n");

            //creating the structure containing all vect
            Map<String, List> vect = new HashMap<>();
            for (Map.Entry<String, String> entry : select_attribute.entrySet()) {
                if (!entry.getKey().contentEquals("O")) {
                    vect.put(entry.getKey(), new ArrayList<String>());
                    if (entry.getValue().contains(",")) {
                        String[] str = entry.getValue().split(",");
                        for (String value : str) {
                            vect.get(entry.getKey()).add(value);
                        }
                    } else {
                        vect.get(entry.getKey()).add(entry.getValue());
                    }
                }
            }

            for (Map.Entry<String, String> entry : F_VECT.entrySet()) {
                if (!vect.containsKey(entry.getKey())) {
                    vect.put(entry.getKey(), new ArrayList<String>());
                    if (entry.getValue().contains(",")) {
                        String[] str = entry.getValue().split(",");
                        for (String value : str) {
                            vect.get(entry.getKey()).add(value);
                        }
                    } else {
                        vect.get(entry.getKey()).add(entry.getValue());
                    }
                } else {
                    if (entry.getValue().contains(",")) {
                        String[] str = entry.getValue().split(",");
                        for (String value : str) {
                            if (!vect.get(entry.getKey()).contains(value)) {
                                vect.get(entry.getKey()).add(value);
                            }
                        }
                    } else {
                        if (!vect.get(entry.getKey()).contains(entry.getValue())) {
                            vect.get(entry.getKey()).add(entry.getValue());
                        }
                    }
                }
            }


            //creating mfs-structure
            writer.write("\t//creating mfs-structure\n");
            writer.write("\tMap<String, List> mfs = new HashMap<>();" + "\n");
            for (String group_var : grouping_variable) {
                writer.write("\tmfs.put(\"" + group_var + "\", new ArrayList<" + columnNameAndType.get(group_var) + ">());\n");
            }
            for (Map.Entry<String, List> entry : vect.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    m = Pattern.compile("()(.+)(_)(.+)").matcher((String) entry.getValue().get(i));
                    if (m.find()) {
                        if (!m.group(4).equals("*")) {
                            writer.write("\tmfs.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<" + columnNameAndType.get(m.group(4)) + ">());\n");
                        } else {
                            writer.write("\tmfs.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<Integer>());\n");
                        }
                    } else {
                        writer.write("\tmfs.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<" + columnNameAndType.get(entry.getValue().get(i)) + ">());\n");
                    }
                }
            }


            //beginning of processing
            writer.write("\t\ttry {" + "\n");
            writer.write("\t\t\tSystem.out.println(\"Database: Loading successful.\");" + "\n");
            writer.write("\t\t\tJDBCUtil db = new JDBCUtil();" + "\n");
            writer.write("\t\t\tConnection conn = db.connectToDatabase();" + "\n");
            writer.write("\t\t\tStatement stat = conn.createStatement();" + "\n");
            writer.write("\t\t\trs = stat.executeQuery(\"SELECT * FROM sales\");" + "\n");
            writer.write("\t\t\twhile(rs.next()){" + "\n");
            for (Map.Entry<String, String> entry : F_VECT.entrySet()) {
                if (entry.getKey() != "O") {
                    writer.write("\t\t\t//load data to attribute " + entry.getKey() + "\n");
                    for (Map.Entry<String, String> basic_column : columnNameAndType.entrySet()) {
                        writer.write("\t\t\t\t" + entry.getKey() + "." + basic_column.getKey() + ".add(" + "(" + basic_column.getValue() + ")rs.getObject(\"" + basic_column.getKey() + "\")" + ")" + ";" + "\n");
                    }
                }
            }
            writer.write("\t\t\t//load data to attribute O " + "\n");
            for (Map.Entry<String, String> basic_column : columnNameAndType.entrySet()) {
                writer.write("\t\t\t\t" + "O." + basic_column.getKey() + ".add(" + "(" + basic_column.getValue() + ")rs.getObject(\"" + basic_column.getKey() + "\")" + ")" + ";" + "\n");
            }

            writer.write("\t\t\t//load data to groupingVariable " + "\n");

            for (String group_var : grouping_variable) {
                writer.write("\t\t\t\tmfs.get(\"" + group_var + "\").add(" + "(" + columnNameAndType.get(group_var) + ")rs.getObject(\"" + group_var + "\")); \n");
            }

            writer.write("\t\t\t}" + "\n");

            //end of processing
            writer.write("\t\t}" + "\n");
            writer.write("\t\tcatch(SQLException e) {" + "\n");
            writer.write("\t\t\tSystem.out.println(\"Database: URL or username or password or table name error!\");" + "\n");
            writer.write("\t\t\te.printStackTrace();" + "\n");
            writer.write("\t\t}" + "\n");
            System.out.print(vect);

            //Scan the table to generate aggregate value 
            for (String key : order) { //vect.get(key)
                writer.write("\n\n");
                if (vect.containsKey(key)) {
                    for (int j = 0; j < vect.get(key).size(); j++) {
                        m = Pattern.compile("()(.+)(_)(.+)").matcher((String) vect.get(key).get(j));
                        if (m.find()) {
                            writer.write("\t\tfor(int i = 0; i < O.prod.size(); i++) { \n");
                            writer.write("\t\t\tInteger sum = null; \n");
                            writer.write("\t\t\tInteger count = null; \n");
                            writer.write("\t\t\tInteger avg = null; \n");
                            if (((String) vect.get(key).get(j)).contains("max")) {
                                writer.write("\t\t\tInteger max = null; \n");
                            }
                            if (((String) vect.get(key).get(j)).contains("min")) {
                                writer.write("\t\t\tInteger min = (Integer)x." + m.group(4) + ".get(i); \n");
                            }
                            for (String group_var : grouping_variable) {
                                writer.write("\t\t\t" + columnNameAndType.get(group_var) + " " + group_var + " = " + "(" + columnNameAndType.get(group_var) + ")mfs.get(\"" + group_var + "\").get(i); \n");
                            }
                            for (Map.Entry<String, List> entry : vect.entrySet()) {
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    writer.write(("\t\t\tInteger ") + entry.getKey() + "_" + entry.getValue().get(i) + " = 0;\n");
                                    writer.write("\t\t\tif(!mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").isEmpty()&&i<mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").size()) { \n");
                                    writer.write("\t\t\t\t" + entry.getKey() + "_" + entry.getValue().get(i) + "= (Integer)mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").get(i); \n");
                                    writer.write("\t\t\t }\n");
                                }
                            }
                            writer.write("\t\t\tfor(int j = 0; j < O.prod.size(); j++) {\n");
                            for (Map.Entry<String, String> attribute : columnNameAndType.entrySet()) {
                                writer.write("\t\t\t\t" + columnNameAndType.get(attribute.getKey()) + " " + key + "_" + attribute.getKey() + " =  O." + attribute.getKey() + ".get(j); \n");
                            }

                            writer.write("\t\t\tif(");
                            for (Map.Entry<String, List> entry : vect.entrySet()) {
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    writer.write(entry.getKey() + "_" + entry.getValue().get(i) + "!=null&&");
                                }
                            }
                            writer.write("true) {\n");
                            if (key.equals("O")) {
                                for (int i = 0; i < grouping_variable.length; i++) {
                                    if (i == 0) {
                                        writer.write("\t\t\t\t\tif(O_" + grouping_variable[i] + ".equals(" + grouping_variable[i] + ")");
                                    } else {
                                        writer.write("&&O_" + grouping_variable[i] + ".equals(" + grouping_variable[i] + ")) { \n");
                                    }
                                }
                            } else {
                                writer.write("\t\t\t\tif(" + condition_vect.get(key) + ") { \n");
                            }

                            writer.write("\t\t\t\t\t if(sum==null) \n");
                            writer.write("\t\t\t\t\t\t sum=0; \n");
                            writer.write("\t\t\t\t\t if(count==null) \n");
                            writer.write("\t\t\t\t\t\t count=0; \n");
                            if (((String) vect.get(key).get(j)).contains("max")) {
                                writer.write("\t\t\t\t\t if(max ==null) \n");
                                writer.write("\t\t\t\t\t\t max =0; \n");
                            }
                            writer.write("\t\t\t\t\t sum +=" + key + "_quant; \n");
                            writer.write("\t\t\t\t\tcount++; \n ");
                            if (((String) vect.get(key).get(j)).contains("max")) {
                                writer.write("\t\t\t\t\tif(max<" + key + "_" + m.group(4) + ") {\n");
                                writer.write("\t\t\t\t\t\t max = " + key + "_" + m.group(4) + "; \n");
                                writer.write("\t\t\t\t} \n");
                            }
                            if (((String) vect.get(key).get(j)).contains("min")) {
                                writer.write("\t\t\t\t\tif(min>" + key + "_" + m.group(4) + ") {\n");
                                writer.write("\t\t\t\t\t\t min = " + key + "_" + m.group(4) + "; \n");
                                writer.write("\t\t\t\t} \n");
                            }
                            writer.write("\t\t\t\t} \n");
                            writer.write("\t\t\t\t} \n");
                            writer.write("\t\t\t} \n");
                            writer.write("\t\t\tif(count != null&&count != 0) {\n");
                            writer.write("\t\t\t\tavg = sum/count;\n");
                            writer.write("\t\t\t} \n");
                            if (((String) vect.get(key).get(j)).contains("avg")) {
                                writer.write("\t\t\t\tmfs.get(\"" + key + "_" + vect.get(key).get(j) + "\").add(avg); \n ");
                            } else if (((String) vect.get(key).get(j)).contains("sum")) {
                                writer.write("\t\t\t\tmfs.get(\"" + key + "_" + vect.get(key).get(j) + "\").add(sum); \n ");
                            } else if (((String) vect.get(key).get(j)).contains("count")) {
                                writer.write("\t\t\t\tmfs.get(\"" + key + "_" + vect.get(key).get(j) + "\").add(count); \n ");
                            } else if (((String) vect.get(key).get(j)).contains("max")) {
                                writer.write("\t\t\t\tmfs.get(\"" + key + "_" + vect.get(key).get(j) + "\").add(max); \n ");
                            } else if (((String) vect.get(key).get(j)).contains("min")) {
                                writer.write("\t\t\t\tmfs.get(\"" + key + "_" + vect.get(key).get(j) + "\").add(min); \n ");
                            }
                            writer.write("\t\t}\n");
                        }
                    }
                }

            }


            writer.write("\t//creating result-structure\n");
            writer.write("\tMap<String, List> result = new HashMap<>();" + "\n");
            for (String group_var : grouping_variable) {
                writer.write("\tresult.put(\"" + group_var + "\", new ArrayList<" + columnNameAndType.get(group_var) + ">());\n");
            }
            for (Map.Entry<String, List> entry : vect.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    m = Pattern.compile("()(.+)(_)(.+)").matcher((String) entry.getValue().get(i));
                    if (m.find()) {
                        if (!m.group(4).equals("*")) {
                            writer.write("\tresult.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<" + columnNameAndType.get(m.group(4)) + ">());\n");
                        } else {
                            writer.write("\tresult.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<Integer>());\n");
                        }
                    } else {
                        writer.write("\tresult.put(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\", new ArrayList<" + columnNameAndType.get(entry.getValue().get(i)) + ">());\n");
                    }
                }
            }

            writer.write("\tboolean flag = true; \n");
            writer.write("\tfor(int i = 0; i<O.prod.size();i++) { \n ");

            writer.write("\t\tfor(int j = 0; j<result.get(\"" + grouping_variable[0] + "\").size();j++) { \n ");
            for (int i = 0; i < grouping_variable.length; i++) {
                if (i == 0) {
                    writer.write("\t\t\t if(result.get(\"" + grouping_variable[i] + "\").get(j).equals(mfs.get(\"" + grouping_variable[i] + "\").get(i))");
                } else {
                    writer.write("&&result.get(\"" + grouping_variable[i] + "\").get(j).equals(mfs.get(\"" + grouping_variable[i] + "\").get(i))");
                }
            }
            writer.write(") {\n");
            writer.write("\t\t\t\tflag = false; \n");
            writer.write("\t\t\t\tbreak; \n");
            writer.write("\t\t\t}else { \n");
            writer.write("\t\t\t\t flag = true;");
            writer.write("\t\t\t}; \n");
            writer.write("\t\t} \n");

            for (Map.Entry<String, List> entry : vect.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    m = Pattern.compile("()(.+)(_)(.+)").matcher((String) entry.getValue().get(i));
                    if (m.find()) {
                        if (!m.group(4).equals("*")) {
                            writer.write("\t\tInteger" + " " + entry.getKey() + "_" + entry.getValue().get(i) + "=(" + "\t\tInteger" + ")" + "mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").get(i)" + ";\n");
                        } else {
                            writer.write("\t\tInteger" + " " + entry.getKey() + "_" + entry.getValue().get(i) + "=(" + "Integer" + ")" + "mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").get(i)" + " ;\n");
                        }
                    } else {
                        writer.write("\t\tInteger" + " " + entry.getKey() + "_" + entry.getValue().get(i) + "=(" + "\t\tInteger" + ")" + "mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").get(i)" + ";\n");
                    }
                }
            }
            writer.write("\t\ttry{\n");
            writer.write("\t\tif(flag");
            for (String condition : having_condition) {
                writer.write("&&(" + condition + ")");
            }
            writer.write(") { \n");
            for (String group_var : grouping_variable) {
                writer.write("\t\t\tresult.get(\"" + group_var + "\").add(mfs.get(\"" + group_var + "\").get(i)); \n");
            }
            for (Map.Entry<String, List> entry : vect.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    writer.write("\t\t\tresult.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").add(mfs.get(\"" + entry.getKey() + "_" + entry.getValue().get(i) + "\").get(i)); \n");
                }
            }
            writer.write("\t\t} \n");
            writer.write("\t\t}catch(NullPointerException e){\n");
            writer.write("\t\t\tcontinue;\n");
            writer.write("\t\t}\n");
            writer.write("\t\t flag = true; \n");
            writer.write("\t} \n");

            //Print the data on the console
            writer.write("\t\t//Print the data on the console \n");
            writer.write("\t\tList<String> column_name = new ArrayList<>(); \n");
            List<String> conAttr = new ArrayList();
            for (String var : select_order) {
                conAttr.add("result.get(\"" + var + "\")");
                writer.write("\t\tcolumn_name.add(\"" + var + "\"); \n");
            }
            writer.write("\t\tStringBuffer stringBuffer = new StringBuffer(); \n");
            writer.write("\t\tStringBuffer line = new StringBuffer(); \n");
            writer.write("\t\t//Print the header\n");
            writer.write("\t\tfor(int i=0;i<column_name.size();i++) {\n");
            writer.write("\t\t\tstringBuffer.append(\"----\").append(column_name.get(i));\n");
            writer.write("\t\t\tline.append(\"=============\");\n");
            writer.write("\t\t}\n");
            writer.write("\t\tSystem.out.println(stringBuffer); \n");
            writer.write("\t\tSystem.out.println(line); \n");

            String con = "";
            String sys = "";
            for (int i = 0; i <= conAttr.size() - 1; i++) {
                if (i == 0) {
                    sys = sys + "\"----\"+" + conAttr.get(i) + ".get(i)";
                } else if (i != conAttr.size() - 1 && i != 0) {
                    con = con + conAttr.get(i) + ".get(i)!=null&&";
                    sys = sys + "+\"----\"+" + conAttr.get(i) + ".get(i)";
                } else {
                    con = con + conAttr.get(i) + ".get(i)!=null";
                    sys = sys + "+\"----\"+" + conAttr.get(i) + ".get(i)+\"----\"";
                }
            }
            writer.write("\t\t//Iterate through the data and print the data as a line\n");
            writer.write("\t\tfor(int i=0;i<" + conAttr.get(1) + ".size();i++) {\n");
            writer.write("\t\t\tif(" + con + ") {\n");
            writer.write("\t\t\t\tSystem.out.println(" + sys + ");\n");


            writer.write("\t\t\t}" + "\n");

            writer.write("\t\t}" + "\n");

            writer.write("\t}" + "\n");

            writer.write("}" + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
