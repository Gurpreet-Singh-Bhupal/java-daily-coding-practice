package CollectionAndOtherDSACoding.StringQuestions;
import java.util.*;
public class PalindromeString {
    static void main() {
        System.out.println(isPalindrome("pool"));
        System.out.println(isPalindrome("Lol"));
    }

//    public static boolean isPalindrome(String str){
//        StringBuilder st=new StringBuilder(str);
//
//        return str.equalsIgnoreCase(st.reverse().toString());
//    }

    public static boolean isPalindrome(String str){
       int left=0;
       int right=str.length()-1;

       while(left<right){
           if(Character.toLowerCase(str.charAt(left)) != Character.toLowerCase(str.charAt(right))){
               return false;
           }
           left++;
           right--;
       }

        return true;
    }
}
