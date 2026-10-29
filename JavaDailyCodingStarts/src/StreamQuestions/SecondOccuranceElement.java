package StreamQuestions;
import java.util.*;
import java.util.stream.*;

public class SecondOccuranceElement {
    static void main() {
        int[] nums = {4, 7, 4, 9, 7, 4, 9, 10};
        getListOf2ndOcc(nums).forEach(pair->{
            System.out.println(Arrays.toString(pair));
        });
    }
    public static List<int[]> getListOf2ndOcc(int[] arr){
            Map<Integer,Integer> map=new HashMap<>();
            List<int[]> lst=new ArrayList<>();
            IntStream.range(0,arr.length).forEach(i->{
                int occ=map.getOrDefault(arr[i],0);
                if(occ==1){
                    lst.add(new int[]{i,arr[i]});
                }
                map.put(arr[i],occ+1);
            });
            return lst;
    }
}
