import java.io.PrintStream;
import java.io.IOException;

public class calc_model_exp {
   
   public static Map<String, Map<String, Integer>> train_data = new TreeMap<String, Map<String, Integer>>();
   public static int all_documents;
   
   public static void main(String[] args) throws IOException {
      
      String train_path = args[0];
      build_model(train_path);
      
      PrintStream model_count = new PrintStream(args[1]);
      
      String model_path = "";
      if (args.length > 2) {
         model_path = args[2];
      }
      
      if (model_path.length() == 0) {
         calc_wi_model(model_count);
      } else {
         calc_wo_model(train_path, model_count);
      }
      
   }
   
   public static void build_model(String train_path) throws IOException {
      
      all_documents = 0;
      
      BufferedReader train = new BufferedReader(new FileReader(train_path));
      String line = "";
      String classLabel = "";
      
      while ((line = train.readLine()) != null) {
         all_documents ++;
         line = line.replaceAll("[\\s]+", " ");
         String[] tokens = line.split(" ");
         classLabel = tokens[0];
         
         if (!train_data.containsKey(classLabel)) {
            train_data.put(classLabel, new TreeMap<String, Integer>());
         }
         
         for (int i = 1; i < tokens.length; i ++) {
            String token = tokens[i];
            String word = token.replaceAll(":[\\d]+", "");
            
            if (train_data.get(classLabel).containsKey(word)) {
               train_data.get(classLabel).put(word, train_data.get(classLabel).get(word) + 1);
            } else {
               train_data.get(classLabel).put(word, 1);
            }
         }
      }
   }
   
   public static void calc_wi_model(PrintStream model_count) throws IOException {
      
      for (String classLabel : train_data.keySet()) {
         for (String feature : train_data.get(classLabel).keySet()) {
            int featCount = train_data.get(classLabel).get(feature);
            double exp = featCount * 1.0 / all_documents;
            model_count.println(classLabel + " " + feature + " " + exp + " " + featCount);
         }
      }
   }
   
   public static void calc_wo_model(String train_path, PrintStream model_count) throws IOException {
      
      
   }
   
}