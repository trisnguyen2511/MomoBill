package com.tris;

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
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Welcome to the Bill Payment System!");

			while (true) {
				System.out.print("> ");
				String input = scanner.nextLine();
				String[] parts = input.split(" ");

				String command = parts[0].toUpperCase();

				try {
					switch (command) {
					case ConstPayment.ADD_BILL:
						if (parts.length != 5) {
							System.out.println("Invalid command. Usage: ADD_BILL <type> <amount> <due_date> <provider>");
							break;
						}
						String type = parts[1];
						double amount = Double.parseDouble(parts[2]);
						String dueDate = parts[3];
						String provider = parts[4];
						paymentService.addBill(type, amount, dueDate, provider);
						break;
					case ConstPayment.DELETE_BILL:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: DELETE_BILL <bill_id>");
							break;
						}
						int billIdToDelete = Integer.parseInt(parts[1]);
						paymentService.deleteBill(billIdToDelete);
						break;
					case ConstPayment.UPDATE_BILL:
						if (parts.length != 6) {
							System.out.println(
									"Invalid command. Usage: UPDATE_BILL <bill_id> <type> <amount> <due_date> <provider>");
							break;
						}
						int billIdToUpdate = Integer.parseInt(parts[1]);
						String newType = parts[2];
						double newAmount = Double.parseDouble(parts[3]);
						String newDueDate = parts[4]; // Format: YYYY-MM-DD
						String newProvider = parts[5];
						paymentService.updateBill(billIdToUpdate, newType, newAmount, newDueDate, newProvider);
						break;
					case ConstPayment.BALANCE:
						System.out.println("Your available balance: " + paymentService.getBalance());
						break;
					case ConstPayment.CASH_IN:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: CASH_IN <amount>");
							break;
						}
						double amountCashIn = Double.parseDouble(parts[1]);
						paymentService.cashIn(amountCashIn);
						System.out.println("Your available balance: " + paymentService.getBalance());
						break;

					case ConstPayment.LIST_BILL:
						paymentService.listBills();
						break;

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

					case ConstPayment.DUE_DATE:
						paymentService.listDueBills();
						break;

					case ConstPayment.SCHEDULE:
						if (parts.length != 3) {
							System.out.printf("Invalid command. Usage: SCHEDULE <bill_id> <%s>%n",
									ConstPayment.DATE_FORMAT_OUTPUT);
							break;
						}
						int billId = Integer.parseInt(parts[1]);
						String scheduleDate = parts[2];
						paymentService.schedulePayment(billId, scheduleDate);
						break;

					case ConstPayment.LIST_PAYMENT:
						paymentService.listPayments();
						break;

					case ConstPayment.SEARCH_BILL_BY_PROVIDER:
						if (parts.length != 2) {
							System.out.println("Invalid command. Usage: SEARCH_BILL_BY_PROVIDER <provider>");
							break;
						}
						String providerSearch = parts[1];
						paymentService.searchBillByProvider(providerSearch);
						break;

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
				} catch (Exception e) {
					System.out.println("An error occurred: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}
