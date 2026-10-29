package CollectionAndOtherDSACoding.StringQuestions;
import java.util.*;

public class MostFrequentWord {
    static void main() {
        String str = "java is good and java is powerful and java is popular";
        System.out.println(getMostFrequentWord(str));
    }

    public static String getMostFrequentWord(String st){
        Map<String,Integer> map=new HashMap<>();
        String[] strArr=st.split("\\s+");
        String mostFreq="";
        int mostFreqWordCount=0;

        for (String s : strArr) {
            map.put(s, map.getOrDefault(s, 0) + 1);
            if (map.get(s) > mostFreqWordCount) {
                mostFreqWordCount = map.get(s);
                mostFreq = s;
            }
        }
        return mostFreq;
    }
}
