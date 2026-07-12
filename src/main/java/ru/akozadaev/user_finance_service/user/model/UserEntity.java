package ru.akozadaev.user_finance_service.user.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 500)
	private String name;

	@Column(name = "date_of_birth", nullable = false)
	private LocalDate dateOfBirth;

	@Column(nullable = false, length = 500)
	private String password;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private AccountEntity account;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EmailDataEntity> emails = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhoneDataEntity> phones = new ArrayList<>();

	protected UserEntity() {
	}

	public UserEntity(String name, LocalDate dateOfBirth, String password) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.password = password;
	}

	public Long getId() { return id; }
	public String getName() { return name; }
	public LocalDate getDateOfBirth() { return dateOfBirth; }
	public String getPassword() { return password; }
	public AccountEntity getAccount() { return account; }
	public List<EmailDataEntity> getEmails() { return emails; }
	public List<PhoneDataEntity> getPhones() { return phones; }
	public void setAccount(AccountEntity account) { this.account = account; }
}
