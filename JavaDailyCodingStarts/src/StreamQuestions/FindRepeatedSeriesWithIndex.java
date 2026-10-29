package StreamQuestions;

import java.util.*;

import java.util.stream.*;

public class FindRepeatedSeriesWithIndex {

    static void main() {
        int[] nums = {1, 2, 3, 1, 5, 2, 1, 5};
        getRepeatedAfterFirstOcc(nums).forEach(arr->{
            System.out.println(Arrays.toString(arr));
        });
    }

    public static List<int[]> getRepeatedAfterFirstOcc(int[] lst){
        List<int[]> newLst=new ArrayList<>();
        Set<Integer> set=new HashSet<>();

        IntStream.range(0,lst.length).forEach(i->{
                if(!set.add(lst[i])){
                    int[] pair=new int[]{i,lst[i]};
                    newLst.add(pair);
                }
        });
        return newLst;
    }
}
