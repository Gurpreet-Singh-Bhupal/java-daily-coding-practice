package StreamQuestions;
import java.util.*;
import java.util.stream.*;

public class TopThreeElementsFromMap {
    static void main() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 45);
        map.put("B", 80);
        map.put("C", 65);
        map.put("D", 70);
        map.put("E", 75);
        getTopThreeValues(map).forEach((key,value)->{
            System.out.println(key+" = "+value);
        });
    }
    public static Map<String,Integer> getTopThreeValues(Map<String,Integer> map){
        return map.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(a,b)->a,LinkedHashMap::new));
    }
}
