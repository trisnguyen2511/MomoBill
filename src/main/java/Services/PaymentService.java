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
    void addBill(String type, double amount, String dueDate, String provider);
    void deleteBill(int billId);
    void updateBill(int billId, String type, double amount, String dueDate, String provider);
}