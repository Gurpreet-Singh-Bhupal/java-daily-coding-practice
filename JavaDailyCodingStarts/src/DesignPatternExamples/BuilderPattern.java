package DesignPatternExamples;
/*
*
Builder is a creational design pattern used to construct complex objects step by step,
especially when they have multiple optional parameters.

For example, when creating a User from form data, name and email may be mandatory,
while phone and address are optional. Builder allows us to set required fields first,
configure optional fields, and finally create the User object using build().
* */
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
