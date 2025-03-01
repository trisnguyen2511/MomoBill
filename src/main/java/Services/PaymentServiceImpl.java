package Services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tris.Models.Bill;
import com.tris.Models.Customer;
import com.tris.Resources.ConstPayment;

public class PaymentServiceImpl implements PaymentService {
	private final Customer customer;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ConstPayment.DATE_FORMAT);

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
			System.out.println("Bill No.  Type       Amount     Due Date     State     Provider");
			bills.forEach(bill -> System.out.printf("%-8d  %-10s %-10.2f %-12s %-8s  %s%n", bill.getId(), bill.getType(),
					bill.getAmount(), bill.getDueDate().format(dateTimeFormatter), bill.getState(),
					bill.getProvider()));
		}
	}

	@Override
	public void payBills(int[] billIds) {
		List<Bill> billsToPay = new ArrayList<>();
		// kiểm tra hóa đơn có tồn tại hoặc đã trả rồi hay chưa
		for (int billId : billIds) {
			Bill bill = findBillById(billId);
			if (bill != null && ConstPayment.NOT_PAID.equals(bill.getState())) {
				billsToPay.add(bill);
			} else {
				System.out.printf("Bill with ID %d not found or already paid.%n", billId);
			}
		}

		// Sắp xếp theo ngày đến hạn
		billsToPay.sort(Comparator.comparing(Bill::getDueDate));

		for (Bill bill : billsToPay) {
			if (customer.deductFunds(bill.getAmount())) {
				bill.setState(ConstPayment.PAID);
				System.out.printf("Payment completed for Bill ID %d.%n", bill.getId());
			} else {
				System.out.printf("Not enough funds to pay Bill ID %d.%n", bill.getId());
			}
		}

	}

	@Override
	public void listDueBills() {
		// Lấy danh sách các Bills chưa trả
		List<Bill> dueBills = customer.getBills().stream().filter(
				bill -> ConstPayment.NOT_PAID.equals(bill.getState()) && bill.getDueDate().isBefore(LocalDate.now()))
				.sorted(Comparator.comparing(Bill::getDueDate)).collect(Collectors.toList());
		
		if (dueBills.isEmpty()) {
			System.out.println("No due bills available.");
		} else {
			System.out.println("Bill No.  Type       Amount     Due Date     State     Provider");
			dueBills.forEach(bill -> System.out.printf("%-8d  %-10s %-10.2f %-12s %-8s  %s%n", bill.getId(),
					bill.getType(), bill.getAmount(), bill.getDueDate(), bill.getState(), bill.getProvider()));
		}
	}

	@Override
	public void schedulePayment(int billId, LocalDate scheduleDate) {
		Bill bill = findBillById(billId);
		if (bill == null) {
			System.out.printf("Bill with ID %d not found.%n", billId);
			return;
		}
		
		// Kiểm tra Bill được lênh lịch đã được trả chưa
		if (ConstPayment.PAID.equals(bill.getState())) {
			System.out.printf("Bill ID %d is already paid.%n", bill.getId());
			return;
		}

		try {
			bill.setScheduleDate(scheduleDate); // Gán ngày lên lịch
			System.out.printf("Payment for Bill ID %d is scheduled on %s.%n", bill.getId(),
					scheduleDate.format(dateTimeFormatter));
		} catch (Exception e) {
			System.out.printf("Something went wrong. %s.%n", e.getMessage());
		}

	}

	@Override
	public void listPayments() {
		System.out.println("No.   Amount     Payment Date    State      Bill ID");
		List<Bill> listBillSorted = customer.getBills().stream().sorted(Comparator.comparing(Bill::getState).reversed())
				.collect(Collectors.toList());
		int index = 1; // xuât theo thứ tự của danh sách Bill
		for (Bill bill : listBillSorted) {
			// đổi PAID = PROCESSED ; NOT_PAID = PENDING
			String paymentState = ConstPayment.PAID.equals(bill.getState()) ? ConstPayment.PROCESSED
					: ConstPayment.PENDING;
			System.out.printf("%-5d %-10.2f %-15s %-10s %d%n", index++, bill.getAmount(), bill.getDueDate(),
					paymentState, bill.getId());
		}
	}

	@Override
	public void searchBillByProvider(String provider) {
		// Lấy các Bill theo nhà cung cấp
		List<Bill> result = customer.getBills().stream().filter(bill -> provider.equals(bill.getProvider()))
				.collect(Collectors.toList());
		
		if (result.isEmpty()) {
			System.out.printf("No bills found for provider: %s%n", provider);
		} else {
			System.out.println("Bill No.  Type       Amount     Due Date     State     Provider");
			result.forEach(bill -> System.out.printf("%-8d  %-10s %-10.2f %-12s %-8s  %s%n", bill.getId(),
					bill.getType(), bill.getAmount(), bill.getDueDate(), bill.getState(), bill.getProvider()));
		}

	}

	@Override
	public double getBalance() {
		// Lấy số tiền dư
		return customer.getBalance();
	}

	// Giúp để tìm hóa đơn theo ID
	private Bill findBillById(int billId) {
		return customer.getBills().stream().filter(bill -> bill.getId() == billId).findFirst().orElse(null);
	}

	@Override
	public void addBill(String type, double amount, LocalDate dueDate, String provider) {
		try {
			// Lấy lớn nhất +1
			int newId = customer.getBills().stream()
			        .mapToInt(Bill::getId)
			        .max()
			        .orElse(0) + 1;
			// Khi thêm hóa đơn thì mặc định sẽ là chưa được trả và không có ngày schedule
			Bill newBill = new Bill(newId, type, amount, dueDate, "NOT_PAID", provider);
			customer.getBills().add(newBill);
			System.out.printf("Bill added successfully! ID: %d%n", newId);
		} catch (Exception e) {
			System.out.printf("Something went wrong. %s.%n", e.getMessage());
		}
	}

	@Override
	public void deleteBill(int billId) {
		Bill billToDelete = findBillById(billId);
		if (billToDelete != null) {
			customer.getBills().remove(billToDelete);
			System.out.printf("Bill with ID %d has been deleted successfully.%n", billId);
		} else {
			System.out.printf("Bill with ID %d not found.%n", billId);
		}
	}

	@Override
	public void updateBill(int billId, String type, double amount, LocalDate dueDate, String provider) {
		Bill billToUpdate = findBillById(billId);
		if (billToUpdate != null) {
			try {
				billToUpdate.setType(type);
				billToUpdate.setAmount(amount);
				billToUpdate.setDueDate(dueDate);
				billToUpdate.setProvider(provider);
				System.out.printf("Bill with ID %d has been updated successfully.%n", billId);
			} catch (Exception e) {
				System.out.printf("Something went wrong. %s.%n", e.getMessage());
			}
		} else {
			System.out.printf("Bill with ID %d not found.%n", billId);
		}
	}
}
