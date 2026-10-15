package CollectionAndOtherDSACoding;
import java.util.*;

public class Anagram {

    static void main() {
        System.out.println(isAnagram("loop","poool"));
    }

    public static boolean isAnagram(String st1,String st2){
        if(st1.length()!=st2.length()) return false;

        char[] ch1=st1.toCharArray();
        char[] ch2=st2.toCharArray();

        Arrays.sort(ch1);
        Arrays.sort(ch2);

        return Arrays.equals(ch1,ch2);

    }
}
