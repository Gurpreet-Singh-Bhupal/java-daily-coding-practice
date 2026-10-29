package StreamQuestions;
import java.util.*;
import java.util.stream.*;
public class ValueGreaterThanPrevious {

    static void main() {
        List<Integer> nums = List.of(10, 20, 15, 30, 25, 40);
        System.out.println(getGreaterNumbList(nums));
    }

    public static List<Integer> getGreaterNumbList(List<Integer> lst){
        List<Integer> maxNums=new ArrayList<>();
        IntStream.range(1,lst.size()).forEach((i)->{
            if(lst.get(i)>lst.get(i-1)){
                maxNums.add(i);
            }
        });
        return maxNums;
    }
}
