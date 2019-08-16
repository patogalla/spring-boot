package com.patogalla.api.user.model;

import com.patogalla.api.utils.model.Identity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("token_login")
public class Token extends AbstractToken {

    Token(){}

    public Token(Identity id, Identity userId, LocalDateTime createdOn, LocalDateTime expiresOn) {
        super(id, userId, createdOn, expiresOn);
    }
}
