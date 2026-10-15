package CollectionAndOtherDSACoding;

import java.util.HashMap;
import java.util.*;

public class TwoSumUsingHashmap {

    static void main() {
        int[] arr={5,6,2,1,4,8,0};
        int target=5;
        int[] pair= getTwoSumPair(arr,target);
        System.out.println(Arrays.toString(pair));
    }

    public static int[] getTwoSumPair(int[] arr,int target){
        Map<Integer,Integer> mp=new HashMap<>();
        for(int i=0;i<arr.length;i++){
            int current=target-arr[i];
            if(mp.containsKey(current)){
                return new int[]{mp.get(current),i};
            }
            mp.put(arr[i],i);
        }
        return  new int[]{};
    }
}
