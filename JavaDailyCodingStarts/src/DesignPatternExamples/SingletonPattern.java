package DesignPatternExamples;
/*
* "Singleton ensures that a class has only one instance and provides a global access point to it.
* I use a private constructor, a static volatile instance,
* and double-checked locking with synchronization to achieve lazy initialization in a thread-safe manner."

One final tip: if the interviewer asks for the simplest thread-safe Singleton in Java,
* an enum Singleton or a static-holder implementation is often simpler.
* Double-checked locking is useful to understand because it demonstrates concurrency concepts.
* */
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