package CollectionAndOtherDSACoding;

import java.util.*;

public class LongestSubstring {

    static void main() {
        System.out.println(getLongestSubstring("okakykik"));
    }

    public static String getLongestSubstring(String st){

        int i=0;
        Set<Character> set=new HashSet<>();
        int maxLength=0;
        String maxLengthWord="";

        for(int j=0;j<st.length();j++){
            while(set.contains(st.charAt(j))){
                set.remove(st.charAt(i));
                i++;
            }
            set.add(st.charAt(j));
            int currentLength=j-i+1;
            if(currentLength>maxLength){
                maxLength=currentLength;
                maxLengthWord=st.substring(i,j+1);
            }
        }

        return maxLengthWord;
    }
}
