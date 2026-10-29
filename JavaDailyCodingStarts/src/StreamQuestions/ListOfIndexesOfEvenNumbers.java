package StreamQuestions;

import java.util.*;

import java.util.stream.*;

public class ListOfIndexesOfEvenNumbers {

    static void main() {
        List<Integer> nums = List.of(10, 20, 15, 30, 25, 40);

        System.out.println(checkIfIndexValueEven(nums));
    }
    public static List<Integer> checkIfIndexValueEven(List<Integer> lst){
        List<Integer> list=new ArrayList<>();

        IntStream.range(0,lst.size()).forEach((i)->{
            if(i%2==0){
                list.add(lst.get(i));
            }
        });

        return list;
    }
}
