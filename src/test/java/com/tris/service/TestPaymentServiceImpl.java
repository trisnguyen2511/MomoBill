package com.tris.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tris.Models.Bill;
import com.tris.Models.Customer;

import Services.PaymentServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPaymentServiceImpl {
    private PaymentServiceImpl paymentService;
    private Customer customer;

    @BeforeEach
    public void setup() {
        // Tạo dữ liệu mẫu
        customer = new Customer(1, "John Doe", 0, List.of(
                new Bill(1, "ELECTRIC", 200000, LocalDate.of(2025, 1, 15), "NOT_PAID", "EVN_HCMC"),
                new Bill(2, "WATER", 150000, LocalDate.of(2025, 1, 20), "NOT_PAID", "SAVACO_HCMC")
        ));
        paymentService = new PaymentServiceImpl(customer);
    }

    @Test
    public void testAddBill() {
        // Thêm hóa đơn mới
        paymentService.addBill("INTERNET", 300000, "2025/02/10", "VNPT");

        // Kiểm tra hóa đơn mới có tồn tại trong danh sách không
        List<Bill> bills = customer.getBills();
        assertEquals(3, bills.size()); // Ban đầu có 2 hóa đơn, thêm 1 hóa đơn mới
        Bill addedBill = bills.get(2); // Hóa đơn mới sẽ nằm cuối danh sách

        assertEquals("INTERNET", addedBill.getType());
        assertEquals(300000, addedBill.getAmount());
        assertEquals(LocalDate.of(2025, 2, 10), addedBill.getDueDate());
        assertEquals("NOT_PAID", addedBill.getState());
        assertEquals("VNPT", addedBill.getProvider());
    }

    @Test
    public void testDeleteBill() {
        // Xóa hóa đơn với ID = 1
        paymentService.deleteBill(1);

        // Kiểm tra danh sách hóa đơn
        List<Bill> bills = customer.getBills();
        assertEquals(1, bills.size()); // Ban đầu có 2 hóa đơn, xóa 1
        assertEquals(2, bills.get(0).getId()); // Chỉ còn lại hóa đơn với ID = 2
    }

    @Test
    public void testDeleteBillNotFound() {
        // Xóa hóa đơn không tồn tại
        paymentService.deleteBill(99);

        // Không có thay đổi trong danh sách hóa đơn
        List<Bill> bills = customer.getBills();
        assertEquals(2, bills.size());
    }

    @Test
    public void testUpdateBill() {
        // Cập nhật thông tin hóa đơn với ID = 2
        paymentService.updateBill(2, "INTERNET", 500000, "2025-03-15", "VNPT");

        // Kiểm tra thông tin hóa đơn sau khi cập nhật
        Bill updatedBill = customer.getBills().stream()
                .filter(bill -> bill.getId() == 2)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedBill);
        assertEquals("INTERNET", updatedBill.getType());
        assertEquals(500000, updatedBill.getAmount());
        assertEquals(LocalDate.of(2025, 3, 15), updatedBill.getDueDate());
        assertEquals("VNPT", updatedBill.getProvider());
    }

    @Test
    public void testUpdateBillNotFound() {
        // Cố gắng cập nhật hóa đơn không tồn tại
        paymentService.updateBill(99, "INTERNET", 500000, "2025-03-15", "VNPT");

        // Danh sách hóa đơn không thay đổi
        List<Bill> bills = customer.getBills();
        assertEquals(2, bills.size());
    }
    
    @Test
    public void testCashIn() {
        // Nạp tiền vào tài khoản
        paymentService.cashIn(1000000);

        // Kiểm tra số dư sau khi nạp
        assertEquals(1000000, customer.getBalance());
    }

    @Test
    public void testListBills() {
        // In danh sách hóa đơn ra console
        paymentService.listBills();

        // Kiểm tra số lượng hóa đơn
        assertEquals(2, customer.getBills().size());
    }

    @Test
    public void testPayBillsSingle() {
        // Thanh toán hóa đơn với ID = 1
        customer.addFunds(200000); // Nạp tiền đủ để thanh toán
        paymentService.payBills(new int[]{1});

        // Kiểm tra trạng thái hóa đơn
        Bill paidBill = customer.getBills().get(0);
        assertEquals("PAID", paidBill.getState());

        // Kiểm tra số dư sau khi thanh toán
        assertEquals(0, customer.getBalance());
    }

    @Test
    public void testPayBillsMultiple() {
        // Thanh toán nhiều hóa đơn
        customer.addFunds(400000); // Nạp tiền đủ để thanh toán cả hai hóa đơn
        paymentService.payBills(new int[]{1, 2});

        // Kiểm tra trạng thái hóa đơn
        assertEquals("PAID", customer.getBills().get(0).getState());
        assertEquals("PAID", customer.getBills().get(1).getState());

        // Kiểm tra số dư sau khi thanh toán
        assertEquals(50000, customer.getBalance());
    }

    @Test
    public void testPayBillsInsufficientFunds() {
        // Không đủ tiền để thanh toán
        customer.addFunds(100000);
        paymentService.payBills(new int[]{1});

        // Hóa đơn không được thanh toán
        assertEquals("NOT_PAID", customer.getBills().get(0).getState());

        // Số dư không thay đổi
        assertEquals(100000, customer.getBalance());
    }

    @Test
    public void testListDueBills() {
        // Thay đổi ngày đến hạn để kiểm tra hóa đơn quá hạn
        customer.getBills().get(0).setDueDate(LocalDate.now().minusDays(1)); // Bill ID = 1 đã quá hạn

        // In danh sách hóa đơn quá hạn ra console
        paymentService.listDueBills();

        // Kiểm tra hóa đơn quá hạn
        assertEquals(1, customer.getBills().stream()
                .filter(bill -> bill.getDueDate().isBefore(LocalDate.now()))
                .count());
    }

    @Test
    public void testSchedulePayment() {
        // Lên lịch thanh toán cho hóa đơn
        paymentService.schedulePayment(1, "2025/01/14");

        // Kiểm tra ngày lên lịch
        Bill scheduledBill = customer.getBills().get(0);
        assertEquals(LocalDate.of(2025, 1, 14), scheduledBill.getScheduleDate());
    }

    @Test
    public void testSchedulePaymentInvalidDate() {
        // Lên lịch thanh toán với ngày không hợp lệ
        paymentService.schedulePayment(1, "INVALID_DATE");

        // Hóa đơn không có ngày được lên lịch
        Bill bill = customer.getBills().get(0);
        assertNull(bill.getScheduleDate());
    }

    @Test
    public void testSearchBillByProvider() {
        // Tìm hóa đơn của nhà cung cấp "SAVACO_HCMC"
        paymentService.searchBillByProvider("SAVACO_HCMC");

        // Kiểm tra kết quả
        List<Bill> filteredBills = customer.getBills().stream()
                .filter(bill -> "SAVACO_HCMC".equalsIgnoreCase(bill.getProvider()))
                .toList();
        assertEquals(1, filteredBills.size());
        assertEquals("SAVACO_HCMC", filteredBills.get(0).getProvider());
    }

    @Test
    public void testSearchBillByProviderNotFound() {
        // Tìm hóa đơn với nhà cung cấp không tồn tại
        paymentService.searchBillByProvider("UNKNOWN_PROVIDER");

        // Không có kết quả
        List<Bill> filteredBills = customer.getBills().stream()
                .filter(bill -> "UNKNOWN_PROVIDER".equalsIgnoreCase(bill.getProvider()))
                .toList();
        assertEquals(0, filteredBills.size());
    }
}