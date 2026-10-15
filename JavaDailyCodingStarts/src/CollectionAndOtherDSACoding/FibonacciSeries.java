package CollectionAndOtherDSACoding;

import java.util.*;

public class FibonacciSeries {

    // Online Java Compiler (Editor)
// Write and run Java online using this editor.
        public static void main(String[] args) {
            System.out.println(getFibonacciSeries(100));
        }

        public static List<Integer> getFibonacciSeries(int n){
            int a=0,b=1;
            int next=0;
            List<Integer> lst=new ArrayList<>();

            lst.add(a);
            lst.add(b);

            for(int i=2;i<n;i++){
                next=a+b;
                a=b;
                b=next;
                lst.add(next);
            }
            return lst;
        }
}
