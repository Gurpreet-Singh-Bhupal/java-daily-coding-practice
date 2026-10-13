package DesignPatternExamples;

class User{
    private final String name;
    private final String email;
    private final int phone;

    User(Builder builder){
        this.name=builder.name;
        this.email=builder.email;
        this.phone=builder.phone;
    }

    static class Builder{
        private final String name;
        private final String email;
        private int phone;

        Builder(String name,String email){
            this.name=name;
            this.email=email;
        }

        Builder phone(int phone){
            this.phone=phone;
            return this;
        }

        User build(){
            return new User(this);
        }

    }

    public String toString(){
        return this.name+" "+this.email+" "+this.phone;
    }
}

public class BuilderPattern {
    static void main() {
        User user=new User.Builder("Guru","gs@gmail.com").phone(98388393).build();
        System.out.println(user);
    }
}
