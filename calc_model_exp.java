import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;

public class calc_model_exp {
   
   public static Map<String, Map<String, Double>> model = new HashMap<String, Map<String, Double>>();
   public static Map<String, Map<String, Integer>> train_data = new TreeMap<String, Map<String, Integer>>();
   public static Map<String, Map<String, Double>> model_expect = new TreeMap<String, Map<String, Double>>();
   public static Set<String> classLabs = new TreeSet<String>();
   public static int all_documents;

   public static void main(String[] args) throws IOException {      
      
      String train_path = args[0];     
      PrintStream model_count = new PrintStream(args[1]);
      classLabs = getClassLabs(train_path);
      all_documents = getLineNum(train_path);
      boolean isModel = true;

      String model_path = "";
      if (args.length > 2) {
         model_path = args[2];
         model = get_model(model_path);
      }

      if (model_path.length() == 0) {
         isModel = false;
         build_wo_model(train_path);  
      } else {
         isModel = true;
         build_wi_model(train_path);
      }
      print(model_count, isModel);
   }
   
   public static void build_wo_model(String train_path) throws IOException {     
 
      BufferedReader train = new BufferedReader(new FileReader(train_path));
      String line = "";

      while ((line = train.readLine()) != null) {
         line = line.replaceAll("[\\s]+", " ");
         String[] tokens = line.split(" ");
         
         for (int i = 1; i < tokens.length; i ++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");

            Iterator itr1 = classLabs.iterator();
            while (itr1.hasNext()) {
               String label = "" + itr1.next();
               if (!train_data.containsKey(word)) {
                  train_data.put(word, new TreeMap<String, Integer>());
               }
               if (train_data.get(word).containsKey(label)) {
                  train_data.get(word).put(label, train_data.get(word).get(label) + 1);
               } else {
                  train_data.get(word).put(label, 1);
               }
            }
         }
      }
   }

   public static void build_wi_model(String train_path) throws IOException {     
 
      BufferedReader train = new BufferedReader(new FileReader(train_path));
      String line = "";

      while ((line = train.readLine()) != null) {
         line = line.replaceAll("[\\s]+", " ");
         String[] tokens = line.split(" ");

         Map<String, Double> classProbs = new HashMap<String, Double>();
         
         classProbs = getClassProbs(line);

         for (int i = 1; i < tokens.length; i ++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");

            Iterator itr1 = classLabs.iterator();
            while (itr1.hasNext()) {
               String label = "" + itr1.next();
               double classProb = classProbs.get(label);
               double weight = classProb * 1.0 / all_documents;

               if (!train_data.containsKey(word)) {
                  train_data.put(word, new TreeMap<String, Integer>());
               }
               if (train_data.get(word).containsKey(label)) {
                  train_data.get(word).put(label, train_data.get(word).get(label) + 1);
               } else {
                  train_data.get(word).put(label, 1);
               }

               if (!model_expect.containsKey(word)) {
                  model_expect.put(word, new TreeMap<String, Double>());
               }
               if (model_expect.get(word).containsKey(label)) {
                  model_expect.get(word).put(label, model_expect.get(word).get(label) + weight);
               } else {
                  model_expect.get(word).put(label, weight);
               }
            }
         }
      }
   }

   public static void print(PrintStream count, boolean isModel) throws IOException {      
      for (String feature : train_data.keySet()) {
         for (String classLabel : train_data.get(feature).keySet()) {   
            if (isModel) {
               double exp = model_expect.get(feature).get(classLabel);
               double featCount = train_data.get(feature).get(classLabel) * 1.0;
               double classProb = exp * all_documents / featCount;
               featCount = featCount * classProb;
               count.println(classLabel + " " + feature + " " + exp + " " + featCount);
            } else {
               double classProb = 0.0;
               classProb = 1.0 / classLabs.size();
               double featCount = train_data.get(feature).get(classLabel) * 1.0 * classProb;
               double exp = featCount / all_documents;
               count.println(classLabel + " " + feature + " " + exp + " " + featCount);              
            }
         }
      }
   }

   public static int getLineNum(String train_path) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(train_path));
      int lineNum = 0;
      String line = "";
      while ((line = br.readLine()) != null) {
         lineNum ++;
      }
      return lineNum;
   }

   public static Set<String> getClassLabs(String train_path) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(train_path));
      Set<String> classLabs = new TreeSet<String>();
      String line = "";
      String classLabel = "";
      while ((line = br.readLine()) != null) {
         line = line.replaceAll("[\\s]+", " ");
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         classLabs.add(classLabel);
      }
      return classLabs;
   }

   public static Map<String, Double> getClassProbs(String line) throws IOException {
      
      line = line.replaceAll("[\\s]+", " ");
      String[] tokens = line.split(" ");
         
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
      return final_pred_probs;
   }

   public static Map<String, Map<String, Double>> get_model(String model_path) throws IOException {
      BufferedReader model_file = new BufferedReader(new FileReader(model_path));
      String line = "";
      String classLabel = "";
      while ((line = model_file.readLine()) != null) {
         if (line.contains("FEATURES FOR CLASS")) {
            classLabel = line.trim().split(" ")[3];
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
      }
      return model;
   }

}