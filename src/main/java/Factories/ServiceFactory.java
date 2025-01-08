package Factories;

import java.util.ArrayList;

import com.trisModels.Bill;
import com.trisModels.Customer;

import Services.PaymentService;
import Services.PaymentServiceImpl;

public class ServiceFactory {
    public static PaymentService createPaymentService() {
        // Tạo đối tượng Customer ban đầu
        Customer customer = new Customer(1, "John Doe", 0, new ArrayList<Bill>());
        // Tạo và trả về đối tượng PaymentService
        return new PaymentServiceImpl(customer);
    }
}
