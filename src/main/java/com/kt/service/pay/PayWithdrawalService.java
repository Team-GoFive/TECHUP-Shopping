package com.kt.service.pay;

import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.BankAccountTransactionEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.PayTransactionEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.pay.transaction.PayTransactionRepository;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayWithdrawalService {
	private final UserRepository userRepository;
	private final BankAccountTransactionRepository bankAccountTransactionRepository;
	private final PayTransactionRepository payTransactionRepository;
	private final BankAccountRepository bankAccountRepository;

	@Transactional
	public void withdraw(long amount, UUID holderId) {
		UserEntity user = userRepository.findByIdOrThrow(holderId);
		PayEntity pay = user.getPay();
		BankAccountEntity bankAccount = bankAccountRepository.findByHolderIdOrThrow(holderId);
		pay.withdraw(amount);
		bankAccount.deposit(amount);

		createDepositBankAccountTransaction(bankAccount, pay, amount);
		createWithdrawPayTransaction(pay, bankAccount, amount);
	}

	private void createDepositBankAccountTransaction(BankAccountEntity bankAccount, PayEntity pay, long amount) {
		BankAccountTransactionEntity bankAccountTransaction =
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				BankAccountTransactionPurpose.PAY_WITHDRAW,
				amount,
				bankAccount.getBalance(),
				pay.getDisplayName(),
				bankAccount.getDisplayName()
			);
		bankAccountTransactionRepository.save(bankAccountTransaction);
	}

	private void createWithdrawPayTransaction(PayEntity pay, BankAccountEntity bankAccount, long amount) {
		PayTransactionEntity payTransaction = PayTransactionEntity.create(
			pay,
			PayTransactionType.DEBIT,
			PayTransactionPurpose.WITHDRAW,
			amount,
			pay.getBalance(),
			pay.getDisplayName(),
			bankAccount.getDisplayName()
		);
		payTransactionRepository.save(payTransaction);
	}
}
