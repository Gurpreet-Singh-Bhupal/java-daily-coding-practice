package StreamQuestions;

import java.util.*;
import java.util.stream.*;

public class FindEvenNumber {
    //Write a stream question to find even from a List of Integers.

    public static void main() {
        List<Integer> list=new ArrayList<>(List.of(2,5,12,3,8,9,15,6));
        System.out.println(getEvenNumbers(list));
    }

    static List<Integer> getEvenNumbers(List<Integer> lst){
        return lst.stream().filter(n->n%2==0).collect(Collectors.toList());
    }

}
