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
			System.out.println("Bill No. Type       Amount     Due Date     State     Provider");
			bills.forEach(bill -> System.out.printf("%-8d %-10s %-10.2f %-12s %-8s  %s%n", bill.getId(), bill.getType(),
					bill.getAmount(), bill.getDueDate().format(dateTimeFormatter), bill.getState(),
					bill.getProvider()));
		}

	}

	@Override
	public void payBills(int[] billIds) {
		List<Bill> billsToPay = new ArrayList<>();
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
		List<Bill> dueBills = customer.getBills().stream().filter(
				bill -> "NOT_PAID".equalsIgnoreCase(bill.getState()) && bill.getDueDate().isBefore(LocalDate.now()))
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
	public void schedulePayment(int billId, String scheduleDateStr) {
		Bill bill = findBillById(billId);
		if (bill == null) {
			System.out.printf("Bill with ID %d not found.%n", billId);
			return;
		}
		if (ConstPayment.PAID.equals(bill.getState())) {
			System.out.printf("Bill ID %d is already paid.%n", bill.getId());
			return;
		}

		try {
			LocalDate scheduleDate = LocalDate.parse(scheduleDateStr, dateTimeFormatter);
			bill.setScheduleDate(scheduleDate); // Gán ngày lên lịch
			System.out.printf("Payment for Bill ID %d is scheduled on %s.%n", bill.getId(),
					scheduleDate.format(dateTimeFormatter));
		} catch (Exception e) {
			System.out.printf("Invalid date format. Please use %s.%n", ConstPayment.DATE_FORMAT_OUTPUT);
		}

	}

	@Override
	public void listPayments() {
		System.out.println("No.   Amount     Payment Date    State      Bill ID");
		List<Bill> listBillSorted = customer.getBills().stream().sorted(Comparator.comparing(Bill::getState).reversed())
				.collect(Collectors.toList());
		int index = 1;
		for (Bill bill : listBillSorted) {
			String paymentState = ConstPayment.PAID.equalsIgnoreCase(bill.getState()) ? ConstPayment.PROCESSED
					: ConstPayment.PENDING;
			System.out.printf("%-5d %-10.2f %-15s %-10s %d%n", index++, bill.getAmount(), bill.getDueDate(),
					paymentState, bill.getId());
		}
	}

	@Override
	public void searchBillByProvider(String provider) {
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
		return customer.getBalance();
	}

	// Helper method to find a bill by its ID
	private Bill findBillById(int billId) {
		return customer.getBills().stream().filter(bill -> bill.getId() == billId).findFirst().orElse(null);
	}

	@Override
	public void addBill(String type, double amount, String dueDate, String provider) {
		try {
			int newId = customer.getBills().stream()
			        .mapToInt(Bill::getId)
			        .max()
			        .orElse(0) + 1;
			LocalDate parsedDueDate = LocalDate.parse(dueDate, dateTimeFormatter); // Parse chuỗi thành LocalDate
			Bill newBill = new Bill(newId, type, amount, parsedDueDate, "NOT_PAID", provider);
			customer.getBills().add(newBill);
			System.out.printf("Bill added successfully! ID: %d%n", newId);
		} catch (Exception e) {
			System.out.printf("Invalid input. Please ensure the due date is in %s format.%n",
					ConstPayment.DATE_FORMAT_OUTPUT);
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
	public void updateBill(int billId, String type, double amount, String dueDate, String provider) {
		Bill billToUpdate = findBillById(billId);
		if (billToUpdate != null) {
			try {
				LocalDate parsedDueDate = LocalDate.parse(dueDate, dateTimeFormatter);
				billToUpdate.setType(type);
				billToUpdate.setAmount(amount);
				billToUpdate.setDueDate(parsedDueDate);
				billToUpdate.setProvider(provider);
				System.out.printf("Bill with ID %d has been updated successfully.%n", billId);
			} catch (Exception e) {
				System.out.printf("Invalid input. Please ensure the due date is in %s format.%n",
						ConstPayment.DATE_FORMAT_OUTPUT);
			}
		} else {
			System.out.printf("Bill with ID %d not found.%n", billId);
		}
	}
}
