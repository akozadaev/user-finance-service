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
@Table(name = "phone_data")
public class PhoneDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false, unique = true, length = 13)
	private String phone;

	protected PhoneDataEntity() {
	}

	public PhoneDataEntity(UserEntity user, String phone) { this.user = user; this.phone = phone; }
	public Long getId() { return id; }
	public UserEntity getUser() { return user; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
}
