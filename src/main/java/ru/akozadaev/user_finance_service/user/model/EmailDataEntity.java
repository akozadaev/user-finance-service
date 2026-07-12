package ru.akozadaev.user_finance_service.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_data")
public class EmailDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false, unique = true, length = 200)
	private String email;

	protected EmailDataEntity() {
	}

	public EmailDataEntity(UserEntity user, String email) { this.user = user; this.email = email; }
	public Long getId() { return id; }
	public UserEntity getUser() { return user; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
}
