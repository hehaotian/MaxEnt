// Park, Jonggun
// Q3
import java.util.*;
import java.io.*;
public class EmpiricalExpectation {
   public static void main(String[] args) throws IOException {
      PrintStream output = new PrintStream(args[1]);
      File training_data = new File(args[0]);
      calculate(training_data, output);
   }
   
   public static void calculate(File training_data, PrintStream output) throws IOException {
      BufferedReader train = new BufferedReader(new FileReader(training_data));
      BufferedReader secondTrain = new BufferedReader(new FileReader(training_data));;
      String line = "";
      Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
      double N = 0;
      while((line = train.readLine()) != null) {
         N++;
         String[] instance = line.split(" "); 
         String classLabel = instance[0].trim();
         String feature = "";
         if (!map.containsKey(classLabel)) {
           Map<String, Integer> matrix = new HashMap<String, Integer>();
           map.put(classLabel, matrix);
         }
         for (int i = 1; i < instance.length; i++) { 
            feature = instance[i].replaceAll(":[\\d]+", ""); // saved all the features
            if (map.get(classLabel).containsKey(feature)) {
               int count = map.get(classLabel).get(feature);
               count = count + 1;
               map.get(classLabel).put(feature, count);
            } else if (!map.get(classLabel).containsKey(feature)) {
               map.get(classLabel).put(feature, 1);
            }
         }
      } 
      String temp = "";
      String x = ""; // instance.
      String y = ""; // y = class label.
      // Map<String
      while((temp = secondTrain.readLine()) != null) {    
         String[] lines = temp.split(" ");
         y = lines[0];     
         for (int i = 1; i < lines.length; i++) {
            x = lines[i].replaceAll(":[\\d]+", ""); // saved all the features  
            if (map.get(y).containsKey(x)) {
               int rawCount = map.get(y).get(x);
               double expectation = rawCount * (1 / N);
               output.println(y + " " + x + " " + expectation + " " + rawCount);
            }    
         }
      }
      
   } 
}