package com.patogalla.api.user.repository;

import com.patogalla.api.user.model.Token;
import com.patogalla.api.utils.model.Identity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface TokenRepository extends CrudRepository<Token, Identity> {

    Stream<Token> findAllByUserId(Identity id);
}
