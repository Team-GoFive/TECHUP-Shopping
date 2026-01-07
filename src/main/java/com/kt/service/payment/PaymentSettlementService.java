package com.kt.service.payment;

import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.BankAccountTransactionEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.PayTransactionEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;
import com.kt.repository.pay.transaction.PayTransactionRepository;
import com.kt.repository.bankaccount.BankAccountRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentSettlementService {

	private final BankAccountRepository bankAccountRepository;
	private final PayTransactionRepository payTransactionRepository;
	private final BankAccountTransactionRepository bankAccountTransactionRepository;
	private final PaymentService paymentService;

	@Transactional
	public void settleOrderProduct(UserEntity buyer, OrderProductEntity orderProduct) {
		long totalPrice = orderProduct.calculateAmount();
		ProductEntity product = orderProduct.getProduct();
		PayEntity pay = buyer.getPay();

		SellerEntity seller = product.getSeller();

		pay.withdraw(totalPrice);
		BankAccountEntity sellerBankAccount = bankAccountRepository.findByHolderIdWithOrThrow(seller.getId());
		sellerBankAccount.deposit(totalPrice);
		createWithdrawPayTransaction(pay, sellerBankAccount, totalPrice);
		createDepositBankAccountTransaction(sellerBankAccount, pay, totalPrice);

		paymentService.create(
			totalPrice,
			0L,
			orderProduct
		);
	}

	private void createWithdrawPayTransaction(PayEntity pay, BankAccountEntity bankAccount, long totalPrice) {
		PayTransactionEntity payTransaction =
			PayTransactionEntity.create(
				pay,
				PayTransactionType.DEBIT,
				PayTransactionPurpose.ORDER_PAYMENT,
				totalPrice,
				pay.getBalance(),
				pay.getDisplayName(),
				bankAccount.getDisplayName()
			);
		payTransactionRepository.save(payTransaction);
	}

	private void createDepositBankAccountTransaction(BankAccountEntity bankAccount, PayEntity pay, long totalPrice) {
		BankAccountTransactionEntity bankAccountTransaction =
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				BankAccountTransactionPurpose.ORDER_SETTLEMENT,
				totalPrice,
				bankAccount.getBalance(),
				pay.getDisplayName(),
				bankAccount.getDisplayName()
			);
		bankAccountTransactionRepository.save(bankAccountTransaction);
	}
}
