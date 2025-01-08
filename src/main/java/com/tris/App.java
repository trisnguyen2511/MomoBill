package com.tris;

import java.util.Scanner;

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
		Scanner scanner = new Scanner(System.in);

		System.out.println("Welcome to the Bill Payment System!");

		while (true) {
			System.out.print("> ");
			String input = scanner.nextLine();
			String[] parts = input.split(" ");

			String command = parts[0].toUpperCase();

			try {
				switch (command) {
				case "CASH_IN":
					if (parts.length != 2) {
						System.out.println("Invalid command. Usage: CASH_IN <amount>");
						break;
					}
					double amount = Double.parseDouble(parts[1]);
					paymentService.cashIn(amount);
					System.out.println("Your available balance: " + paymentService.getBalance());
					break;
				case "LIST_BILL":
					paymentService.listBills();
					break;
				case "EXIT":
					System.out.println("Good bye!");
					return;
				default:
					System.out.println("Unknown command: " + command);
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid number format. Please check your input.");
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}
		}
	}
}
