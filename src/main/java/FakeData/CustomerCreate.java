package FakeData;

import java.util.ArrayList;

import com.trisModels.Bill;
import com.trisModels.Customer;

public class CustomerCreate {
	Customer getCustomer() {
		Customer customer = new Customer(1, "John Doe", 0, new ArrayList<Bill>());
		return customer;
	}
}
