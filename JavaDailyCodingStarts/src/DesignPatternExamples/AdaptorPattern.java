package DesignPatternExamples;
interface PaymentProcessor{
        void pay(double amount);
}

class LegacyPaymentClass{
    void paymentMethod(double amount){
        System.out.println("Rs"+amount+" payment is done !!");
    }
}

class Adaptor implements  PaymentProcessor{
    private final LegacyPaymentClass legacyPaymentClass;

    Adaptor(LegacyPaymentClass legacyPaymentClass){
        this.legacyPaymentClass=legacyPaymentClass;
    }

    @Override
    public void pay(double amount){
        legacyPaymentClass.paymentMethod(amount);
    }
}


public class AdaptorPattern {
    static void main() {
        PaymentProcessor payment=new Adaptor(new LegacyPaymentClass());
        payment.pay(5000);
    }

}
