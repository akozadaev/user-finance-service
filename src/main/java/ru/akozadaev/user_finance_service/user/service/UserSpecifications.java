package ru.akozadaev.user_finance_service.user.service;

import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import ru.akozadaev.user_finance_service.user.model.UserEntity;

public final class UserSpecifications {
	private UserSpecifications() { }

	public static Specification<UserEntity> filtered(LocalDate dateOfBirth, String phone, String name, String email) {
		return (root, query, builder) -> {
			var predicate = builder.conjunction();
			if (dateOfBirth != null) predicate = builder.and(predicate, builder.greaterThan(root.get("dateOfBirth"), dateOfBirth));
			if (phone != null) predicate = builder.and(predicate, builder.equal(root.join("phones", JoinType.INNER).get("phone"), phone));
			if (name != null) predicate = builder.and(predicate, builder.like(builder.lower(root.get("name")), name.toLowerCase() + "%"));
			if (email != null) predicate = builder.and(predicate, builder.equal(builder.lower(root.join("emails", JoinType.INNER).get("email")), email.toLowerCase()));
			query.distinct(true);
			return predicate;
		};
	}
}
