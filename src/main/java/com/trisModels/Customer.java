package com.trisModels;

import java.util.List;

public class Customer {
	private int id;
	private String name;
	private double balance;
	private List<Bill> bills;

	public Customer() {

	}

	public Customer(int id, String name, double balance, List<Bill> bills) {
		super();
		this.id = id;
		this.name = name;
		this.balance = balance;
		this.bills = bills;
	}
	
    public void addFunds(double amount) {
        this.balance += amount;
    }

    public boolean deductFunds(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * @return the bills
	 */
	public List<Bill> getBills() {
		return bills;
	}

	/**
	 * @param bills the bills to set
	 */
	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

}
