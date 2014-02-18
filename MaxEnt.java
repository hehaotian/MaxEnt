import java.io.*;
import java.util.*;
import java.lang.*;
import java.math.*;

public class MaxEnt {
   
   private Map<String, Map<String, Double>> model = new HashMap<String, Map<String, Double>>();
   private Map<String, Map<String, Integer>> test_matrix = new HashMap<String, Map<String, Integer>>();
   private Set<String> classLabs = new TreeSet<String>();
   private Set<String> feature_counts = new TreeSet<String>();
   
   public MaxEnt(String model_path) throws IOException {
      this.model = get_model(model_path);
   }
   
   private Map<String, Map<String, Double>> get_model(String model_path) throws IOException {
      BufferedReader model_file = new BufferedReader(new FileReader(model_path));
      String line = "";
      String classLabel = "";
      while ((line = model_file.readLine()) != null) {
         if (line.contains("FEATURES FOR CLASS")) {
            classLabel = line.trim().split(" ")[3];
            classLabs.add(classLabel);
            continue;
         }
         String[] word_weight = line.split(" ");
         String word = word_weight[1];
         String weight_string = word_weight[2];
         double weight = Double.parseDouble(weight_string);
         if (!model.containsKey(classLabel)) {
            model.put(classLabel, new HashMap<String, Double>());
         }
         model.get(classLabel).put(word, weight);
         feature_counts.add(word);
      }
      
      /* DEBUG: CLASS LABEL COLLECTION
      for (String class_4 : classLabs) {
      System.out.print(class_4 + " ");
      }
      */
      
      return model;
   }
   
   public void predict(String file_path, PrintStream ps) throws IOException {
      
      BufferedReader br = new BufferedReader(new FileReader(file_path));
      ps.println("%%%%% test data:");
      
      String line = "";
      String correct_classLabel = "";
      int instanceName = -1;
      while ((line = br.readLine()) != null) {
         
         instanceName ++;
         line = line.replaceAll("[\\s]+", " ");
         String[] tokens = line.split(" ");
         correct_classLabel = tokens[0];
         
         Map<String, Double> temp_pred_probs = new HashMap<String, Double>();
         
         double normal_z = 0.0;
         Iterator itr1 = classLabs.iterator();
         while (itr1.hasNext()) {
            String label = "" + itr1.next();
            double sum = 0.0;
            
            for (int i = 1; i < tokens.length; i++) {
               String word = tokens[i].replaceAll(":[\\d]+", "");
               sum += model.get(label).get(word);
            }
            double result = Math.exp(sum);
            temp_pred_probs.put(label, result);
            normal_z += result;
         }
         
         Map<String, Double> final_pred_probs = new HashMap<String, Double>();
         
         Iterator itr2 = classLabs.iterator();
         while (itr2.hasNext()) {
            String label = "" + itr2.next();
            final_pred_probs.put(label, temp_pred_probs.get(label) / normal_z);
         }
         
         List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>();
         entryList.addAll(final_pred_probs.entrySet());
         
         Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
               return b.getValue().compareTo(a.getValue());
            }
         }
         );
         ps.print("array:" + instanceName + " " + correct_classLabel);
         
         int counter = 0;
         for (Map.Entry<String, Double> a: entryList) {
            String key = "" + a.getKey().replaceAll("[0-9]+", "");
            double prob = a.getValue();
            if (counter == 0) {
               if (!test_matrix.containsKey(correct_classLabel)) {
                  test_matrix.put(correct_classLabel, new HashMap<String, Integer>());
               }
               if (test_matrix.get(correct_classLabel).containsKey(key)) {
                  test_matrix.get(correct_classLabel).put(key, test_matrix.get(correct_classLabel).get(key) + 1);
               } else {
                  test_matrix.get(correct_classLabel).put(key, 1);
               }
            }
            ps.print(" " + key + " " + prob);
            counter ++;
         }
         ps.println("");
      }
   }
   
   public void report() {
      System.out.println("class_num=" + classLabs.size() + " feat_num=" + feature_counts.size());
      System.out.println();
      System.out.println("Confusion matrix for the test data:");
         System.out.println("row is the truth, column is the system output\n");
      System.out.print("\t");
      for (String class_3 : classLabs) {
         System.out.print(class_3 + " ");
      }
      System.out.println();
      for (String class_4 : classLabs) {
         System.out.print(class_4 + " ");
         for (String class_5 : classLabs) {
            if (test_matrix.get(class_4).get(class_5) != null) {
               System.out.print(test_matrix.get(class_4).get(class_5) + " ");
            } else {
               System.out.print("0 ");
            }
         }
         System.out.println();
      }
      System.out.println();
      System.out.println(" Test accuracy=" + getAccuracy(test_matrix));
   }
   
   private double getAccuracy(Map<String, Map<String, Integer>> matrix) {
      int correct_count = 0;
      int sum = 0;
      for (String str_1 : matrix.keySet()) {
         for (String str_2 : matrix.get(str_1).keySet()) {
            int count = matrix.get(str_1).get(str_2);
            if (str_1.equals(str_2)) {
               correct_count += count;
            }
            sum += count;
         }
      }
      return correct_count * 1.0 / sum;
   }
   
}