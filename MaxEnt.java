import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class MaxEnt {

	private Map<String, Map<String, Double>> model = new HashMap<String, Map<String, Double>>();
	private Map<String, Map<String, Integer>> test_data = new HashMap<String, Map<String, Integer>>();
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
        classLabel = line.split(" ")[3];
        continue;
      }
      String[] word_weight = line.split(" ");

      
      
    }
		return model;
	}

	public void predict(String test_path, PrintStream res) throws IOException {
		BufferedReader test_file = new BufferedReader(new FileReader(test_path));
		res.println("res file test");
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