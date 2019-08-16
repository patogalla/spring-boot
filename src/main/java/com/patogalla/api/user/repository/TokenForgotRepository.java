package com.patogalla.api.user.repository;

import com.patogalla.api.user.model.TokenForgot;
import com.patogalla.api.utils.model.Identity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.stream.Stream;

@Repository
public interface TokenForgotRepository extends CrudRepository<TokenForgot, Identity> {

    Stream<TokenForgot> findAllByUserId(Identity id);
}
