package com.tris;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import com.tris.Resources.ConstPayment;

import Factories.ServiceFactory;
import Services.PaymentService;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		// Tạo đối tượng PaymentService thông qua Factory
		PaymentService paymentService = ServiceFactory.createPaymentService();
		final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ConstPayment.DATE_FORMAT);
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Welcome to the Bill Payment System!");

			while (true) {
				System.out.print("> ");
				String input = scanner.nextLine();
				String[] parts = input.split(" ");

				String command = parts[0].toUpperCase();

				try {
					switch (command) {
					// Thêm bill
					case ConstPayment.ADD_BILL:
						if (parts.length != 5) {
							System.out
									.println("Invalid command. Usage: ADD_BILL <type> <amount> <due_date> <provider>");
							break;
						}
						String type = parts[1];
						double amount = Double.parseDouble(parts[2]);
						LocalDate dueDate = LocalDate.parse(parts[3], dateTimeFormatter);
						String provider = parts[4];
						paymentService.addBill(type, amount, dueDate, provider);
						break;
					// Xóa bill
					case ConstPayment.DELETE_BILL:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: DELETE_BILL <bill_id>");
							break;
						}
						int billIdToDelete = Integer.parseInt(parts[1]);
						paymentService.deleteBill(billIdToDelete);
						break;
					// Sửa bill
					case ConstPayment.UPDATE_BILL:
						if (parts.length != 6) {
							System.out.println(
									"Invalid command. Usage: UPDATE_BILL <bill_id> <type> <amount> <due_date> <provider>");
							break;
						}
						int billIdToUpdate = Integer.parseInt(parts[1]);
						String newType = parts[2];
						double newAmount = Double.parseDouble(parts[3]);
						LocalDate newDueDate = LocalDate.parse(parts[4], dateTimeFormatter);
						String newProvider = parts[5];
						paymentService.updateBill(billIdToUpdate, newType, newAmount, newDueDate, newProvider);
						break;
					// Kiểm tra số tiền dư
					case ConstPayment.BALANCE:
						System.out.println("Your available balance: " + paymentService.getBalance());
						break;
					// Nạp tiền
					case ConstPayment.CASH_IN:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: CASH_IN <amount>");
							break;
						}
						double amountCashIn = Double.parseDouble(parts[1]);
						paymentService.cashIn(amountCashIn);
						System.out.println("Your available balance: " + paymentService.getBalance());
						break;
					// Hiển thị danh sách các BIll
					case ConstPayment.LIST_BILL:
						paymentService.listBills();
						break;
					// trả hóa đoă
					case ConstPayment.PAY:
						if (parts.length < 2) {
							System.out.println("Invalid command. Usage: PAY <bill_id> ...");
							break;
						}
						int[] billIds = new int[parts.length - 1];
						for (int i = 1; i < parts.length; i++) {
							billIds[i - 1] = Integer.parseInt(parts[i]);
						}
						paymentService.payBills(billIds);
						break;
					// hiển thị ngày đến hạn
					case ConstPayment.DUE_DATE:
						paymentService.listDueBills();
						break;
					// Lên lịch trả hóa đơn
					case ConstPayment.SCHEDULE:
						if (parts.length != 3) {
							System.out.printf("Invalid command. Usage: SCHEDULE <bill_id> <%s>%n",
									ConstPayment.DATE_FORMAT_OUTPUT);
							break;
						}
						int billId = Integer.parseInt(parts[1]);
						LocalDate scheduleDate = LocalDate.parse(parts[2], dateTimeFormatter);
						paymentService.schedulePayment(billId, scheduleDate);
						break;
					// Danh sách các hóa đơn đã trả
					case ConstPayment.LIST_PAYMENT:
						paymentService.listPayments();
						break;
					// Tìm hóa đơn theo nhà cung cấp
					case ConstPayment.SEARCH_BILL_BY_PROVIDER:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: SEARCH_BILL_BY_PROVIDER <provider>");
							break;
						}
						String providerSearch = parts[1];
						paymentService.searchBillByProvider(providerSearch);
						break;
					// Thoát chương trình
					case ConstPayment.EXIT:
						System.out.println("Goodbye! Have a great day!");
						return;
					default:
						System.out.println("Unknown command: " + command);
						System.out.println(
								"Available commands: BALANCE, CASH_IN, LIST_BILL, PAY, DUE_DATE, SCHEDULE, LIST_PAYMENT, SEARCH_BILL_BY_PROVIDER, ADD_BILL, DELETE_BILL, UPDATE_BILL, EXIT.");
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid number format. Please check your input.");
				} catch (DateTimeParseException e) {
					System.out.printf("Invalid date format. Please ensure the date is in %s format.%n",
							ConstPayment.DATE_FORMAT_OUTPUT);
				} catch (Exception e) {
					System.out.println("An error occurred: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}
