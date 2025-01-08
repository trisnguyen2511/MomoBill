package Services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.tris.Resources.Constant.State;
import com.tris.Resources.StatePay;
import com.trisHelper.BillHelper;
import com.trisModels.Bill;
import com.trisModels.Customer;

public class PaymentServiceImpl implements PaymentService {
	private final Customer customer;

	public PaymentServiceImpl(Customer customer) {
		this.customer = customer;
	}

	@Override
	public void cashIn(double amount) {
        customer.addFunds(amount);
	}

	@Override
	public void listBills() {
        List<Bill> bills = customer.getBills();
        if (bills.isEmpty()) {
            System.out.println("No bills available.");
        } else {
            System.out.println("Bill No.  Type      Amount    Due Date     State     Provider");
            bills.forEach(bill -> System.out.printf("%-8d %-10s %-10.2f %-12s %-8s %s%n",
                    bill.getId(),
                    bill.getType(),
                    bill.getAmount(),
                    bill.getDueDate(),
                    bill.getState(),
                    bill.getProvider()));
        }

	}

	@Override
	public void payBills(int[] billIds) {
        List<Bill> billsToPay = new ArrayList<>();
        for (int billId : billIds) {
            Bill bill = findBillById(billId);
            if (bill != null && StatePay.NOT_PAID.getId() == bill.getState()) {
                billsToPay.add(bill);
            } else {
                System.out.printf("Bill with ID %d not found or already paid.%n", billId);
            }
        }

        // Sắp xếp theo ngày đến hạn
        billsToPay.sort(Comparator.comparing(Bill::getDueDate));

        for (Bill bill : billsToPay) {
            if (customer.deductFunds(bill.getAmount())) {
                bill.setState(StatePay.PAID.getId());
                System.out.printf("Payment completed for Bill ID %d.%n", bill.getId());
            } else {
                System.out.printf("Not enough funds to pay Bill ID %d.%n", bill.getId());
            }
        }

	}

	@Override
	public void listDueBills() {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulePayment(int billId, String scheduleDate) {
        Bill bill = findBillById(billId);
        if (bill == null) {
            System.out.printf("Bill with ID %d not found.%n", billId);
            return;
        }
        if (StatePay.PAID.getId() == bill.getState()) {
            System.out.printf("Bill ID %d is already paid.%n", bill.getId());
            return;
        }
        System.out.printf("Payment for Bill ID %d is scheduled on %s.%n", bill.getId(), scheduleDate);

	}

	@Override
	public void listPayments() {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchBillByProvider(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getBalance() {
		return customer.getBalance();
	}
	
    // Helper method to find a bill by its ID
    private Bill findBillById(int billId) {
        return customer.getBills().stream()
                .filter(bill -> bill.getId() == billId)
                .findFirst()
                .orElse(null);
    }
	
}
