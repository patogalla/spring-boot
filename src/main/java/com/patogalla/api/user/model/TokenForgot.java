package com.patogalla.api.user.model;

import com.patogalla.api.utils.model.Identity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("token_forgot")
public class TokenForgot extends AbstractToken {

    TokenForgot() {}

    public TokenForgot(Identity id, Identity userId, LocalDateTime createdOn, LocalDateTime expiresOn) {
        super(id, userId, createdOn, expiresOn);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenForgot{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", createdOn=").append(createdOn);
        sb.append(", expiresOn=").append(expiresOn);
        sb.append('}');
        return sb.toString();
    }
}
