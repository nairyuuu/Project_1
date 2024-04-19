import java.util.*;

public class ParameterCount {
    public static void main(String[] args) {
        String inputString = "";
        boolean first = true;

        for (int i = 0; i < args.length; i++) {
            inputString += args[i] + " ";
        }
        // Gather the input string
        Map<String, Integer> frequencyMap = countFrequency(inputString);
        // Use hash-map to countfrequency

        List<String> sortedKeys = new ArrayList<>(frequencyMap.keySet());
        Collections.sort(sortedKeys);
        LinkedHashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();
        for (String key : sortedKeys) {
            sortedHashMap.put(key, frequencyMap.get(key));
        }
        // Sorting

        System.out.print("{");
        for (Map.Entry<String, Integer> entry : sortedHashMap.entrySet()) {
        if (first) {
            first = false;
        } else {
            System.out.print("; ");
        }
        System.out.print(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("}");
    }
    // Weird way to print output

    private static Map<String, Integer> countFrequency(String inputString) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        String[] tokens = inputString.split(" ");
        for (String token : tokens) {
            token = token.toLowerCase();
            int count = frequencyMap.getOrDefault(token, 0);
            frequencyMap.put(token, count + 1);
        }
        return frequencyMap;
    }
}