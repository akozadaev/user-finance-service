package ru.akozadaev.user_finance_service.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserEntity user;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal balance;

	@Column(name = "initial_balance", nullable = false, precision = 19, scale = 2)
	private BigDecimal initialBalance;

	@Version
	private long version;

	protected AccountEntity() {
	}

	public AccountEntity(UserEntity user, BigDecimal initialBalance) {
		this.user = user;
		this.balance = initialBalance;
		this.initialBalance = initialBalance;
	}

	public Long getId() { return id; }
	public UserEntity getUser() { return user; }
	public BigDecimal getBalance() { return balance; }
	public BigDecimal getInitialBalance() { return initialBalance; }
	public void setBalance(BigDecimal balance) { this.balance = balance; }
}
