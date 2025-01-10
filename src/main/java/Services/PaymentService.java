package Services;

import java.time.LocalDate;

public interface PaymentService {
    void cashIn(double amount);
    void listBills();
    void payBills(int[] billIds);
    void listDueBills();
    void schedulePayment(int billId, LocalDate scheduleDate);
    void listPayments();
    void searchBillByProvider(String provider);
    double getBalance();
    void addBill(String type, double amount, LocalDate dueDate, String provider);
    void deleteBill(int billId);
    void updateBill(int billId, String type, double amount, LocalDate dueDate, String provider);
}