package com.patogalla.api.user.repository;

import com.patogalla.api.user.model.User;
import com.patogalla.api.utils.model.Identity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Identity> {

    Optional<User> findByUsernameOrEmail(final String username, final String email);

    User findByEmail(String email);

    User findByPhoneCountryIgnoreCaseAndPhoneIgnoreCaseEndingWith(String phoneCountry, String phone);

    Optional<User> findByEmailAndActivationToken(String email, UUID activationToken);
}