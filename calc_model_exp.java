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
   
   public static Map<String, Map<String, Integer>> train_count = new TreeMap<String, Map<String, Integer>>();
   public static Map<String, Map<String, Integer>> train_weight = new TreeMap<String, Map<String, Integer>>();
   public static Set<String> classLabs = new TreeSet<String>();
   public static int all_documents;

   public static void main(String[] args) throws IOException {      
      
      String train_path = args[0];     
      PrintStream model_count = new PrintStream(args[1]);
      classLabs = getClassLabs(train_path);
      all_documents = getLineNum(train_path);
      boolean model = true;

      String model_path = "";
      if (args.length > 2) {
         model_path = args[2];
      }

      if (model_path.length() == 0) {
         build_wo_model(train_path);
         model = false;
      } else {
         build_wi_model(train_path, model_path);
      }

      print(model_count, model);
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
               if (!train_data.containsKey(label)) {
                  train_data.put(label, new TreeMap<String, Integer>());
               }
               if (train_data.get(label).containsKey(word)) {
                  train_data.get(label).put(word, train_data.get(label).get(word) + 1);
               } else {
                  train_data.get(label).put(word, 1);
               }
            }
         }
      }
   }

   public static void print(PrintStream count, boolean model) throws IOException {      
      for (String classLabel : train_data.keySet()) {
         for (String feature : train_data.get(classLabel).keySet()) {
            double classProb = 0.0;
            if (model) {
               classProb = 1.0; // getClassProb();
            } else {
               classProb = 1.0 / classLabs.size();
            }
            double featCount = train_data.get(classLabel).get(feature) * 1.0 * classProb;
            double exp = featCount / all_documents;
            count.println(classLabel + " " + feature + " " + exp + " " + featCount);
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

}