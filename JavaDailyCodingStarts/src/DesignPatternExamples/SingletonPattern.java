package DesignPatternExamples;

class Singleton{
    private static volatile Singleton INSTANCE;

    private Singleton(){

    }

    public static Singleton getInstance(){
        if(INSTANCE==null){
            synchronized(Singleton.class){
                if(INSTANCE==null){
                    INSTANCE=new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}

public class SingletonPattern{
    static void main() {
        Singleton s1=Singleton.getInstance();
        Singleton s2=Singleton.getInstance();

        System.out.println(s1+"  "+s2);
        System.out.println(s1==s2);
    }
}