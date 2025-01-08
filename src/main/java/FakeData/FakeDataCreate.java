package FakeData;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.tris.Resources.StatePay;
import com.trisModels.Bill;
import com.trisModels.Customer;

public class FakeDataCreate {
	Customer getCustomer() {
		Customer customer = new Customer(1, "John Doe", 0, new ArrayList<Bill>());
		return customer;
	}
	
	List<Bill> getListBill(){
		List<Bill> listBills = new ArrayList<Bill>();
		listBills.add(new Bill(1,"ELECTRIC",10000.0,LocalDate.of(2020, 1, 8),StatePay.PAID.getId(),"EVN HCMC"));
		return listBills;
	}
}
