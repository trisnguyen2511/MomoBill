package Factories;

import java.util.ArrayList;

import com.tris.Models.Bill;
import com.tris.Models.Customer;

import FakeData.FakeDataCreate;
import Services.PaymentService;
import Services.PaymentServiceImpl;

public class ServiceFactory {
    public static PaymentService createPaymentService() {
        // Tạo đối tượng Customer ban đầu
        Customer customer = FakeDataCreate.getCustomer();
        // Tạo và trả về đối tượng PaymentService
        return new PaymentServiceImpl(customer);
    }
}
