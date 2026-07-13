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
@Table(name = "oauth_identity")
public class OAuthIdentityEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private UserEntity user;
	@Column(nullable = false, unique = true, length = 36) private String subject;
	protected OAuthIdentityEntity() { }
	public OAuthIdentityEntity(UserEntity user, String subject) { this.user = user; this.subject = subject; }
	public UserEntity getUser() { return user; }
	public String getSubject() { return subject; }
}
