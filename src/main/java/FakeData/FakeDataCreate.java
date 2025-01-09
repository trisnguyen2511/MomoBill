package FakeData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tris.Models.Bill;
import com.tris.Models.Customer;
import com.tris.Resources.ConstPayment;

public class FakeDataCreate {
	public static Customer getCustomer() {
		Customer customer = new Customer(1, "Tris", 200000.0, getListBill());
		return customer;
	}
	
	public static List<Bill> getListBill(){
		List<Bill> listBills = new ArrayList<Bill>();
		listBills.add(new Bill(1,"ELECTRIC",200000.0,LocalDate.of(2020, 10, 25),ConstPayment.NOT_PAID,"EVN HCMC"));
		listBills.add(new Bill(2,"WATER",10000.0,LocalDate.of(2020, 10, 30),ConstPayment.NOT_PAID,"SAVACO HCMC"));
		listBills.add(new Bill(3,"INTERNET",800000.0,LocalDate.of(2020, 11, 30),ConstPayment.NOT_PAID,"VNPT"));
		return listBills;
	}
}
