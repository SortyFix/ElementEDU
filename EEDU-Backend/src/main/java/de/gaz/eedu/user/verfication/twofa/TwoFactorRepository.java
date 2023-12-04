package de.gaz.eedu.user.verfication.twofa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorRepository extends JpaRepository<TwoFactorEntity, Long> {}
