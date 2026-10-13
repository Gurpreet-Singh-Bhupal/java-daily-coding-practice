package DesignPatternExamples;

/*
* Factory is a creational design pattern that encapsulates object creation
* and returns the appropriate object based on the requirement.

For example, in a notification system, a Factory can create Email, SMS, or WhatsApp notification objects.
* This centralizes object-creation logic and reduces coupling between the client and concrete classes.
*
* */



interface Notification{
    void sendNotification();
}
class EmailNotification implements Notification{

    @Override
    public void sendNotification() {
        System.out.println("Email notification sent !!");
    }
}

class SMSNotification implements Notification{
    @Override
    public void sendNotification(){
        System.out.println("SMS Notification sent !!");
    }
}

public class FactoryPattern {

    public static Notification create(String type){
            if(type.equalsIgnoreCase("email")){
                return new EmailNotification();
            }
            else if(type.equalsIgnoreCase("sms")){
                return new SMSNotification();
            }

            throw new IllegalArgumentException("Invalid return type");
    }

}
class Main{
    static void main() {
        Notification email=FactoryPattern.create("email");
        email.sendNotification();
    }
}