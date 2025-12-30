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
import com.kt.repository.BankAccountTransactionRepository;
import com.kt.repository.PayTransactionRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayChargeService {

	private final UserRepository userRepository;
	private final BankAccountTransactionRepository bankAccountTransactionRepository;
	private final PayTransactionRepository payTransactionRepository;

	@Transactional
	public void charge(long amount, UUID userId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		BankAccountEntity bankAccount = user.getBankAccount();
		PayEntity pay = user.getPay();
		bankAccount.withdraw(amount);
		pay.charge(amount);

		createWithdrawBankAccountTransaction(bankAccount, pay, amount);
		createChargePayTransaction(pay, bankAccount, amount);
	}

	private void createWithdrawBankAccountTransaction(BankAccountEntity bankAccount, PayEntity pay, long amount) {
		BankAccountTransactionEntity bankAccountTransaction =
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.WITHDRAW,
				BankAccountTransactionPurpose.PAY_CHARGE,
				amount,
				bankAccount.getBalance(),
				pay.getId()
			);
		bankAccountTransactionRepository.save(bankAccountTransaction);
	}

	private void createChargePayTransaction(PayEntity pay, BankAccountEntity bankAccount, long amount) {
		PayTransactionEntity payTransaction =
			PayTransactionEntity.create(
				pay,
				PayTransactionType.CREDIT,
				PayTransactionPurpose.CHARGE,
				amount,
				pay.getBalance(),
				bankAccount.getId()
			);
		payTransactionRepository.save(payTransaction);
	}

}
