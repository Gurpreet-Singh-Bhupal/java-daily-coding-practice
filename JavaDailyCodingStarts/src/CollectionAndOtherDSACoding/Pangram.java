package CollectionAndOtherDSACoding;

import java.util.Locale;

public class Pangram {
    static void main() {
        System.out.println(isPangram("The quick brown fox jumps over the lazy dog."));
    }
    public static boolean isPangram(String st){
        boolean[] seen=new boolean[26];

        for(int i=0;i<st.length();i++){
            char ch=st.toLowerCase().charAt(i);
            if(ch>='a' && ch<='z'){
                seen[ch-'a']=true;
            }
        }

        for(boolean val:seen){
            if(!val){
                return false;
            }
        }
        return true;
    }
}
