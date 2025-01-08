package Services;

public interface PaymentService {
    void cashIn(double amount);
    void listBills();
    void payBills(int[] billIds);
    void listDueBills();
    void schedulePayment(int billId, String scheduleDate);
    void listPayments();
    void searchBillByProvider(String provider);
    double getBalance();
}