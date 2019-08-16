package com.patogalla.api.user.model;

import com.patogalla.api.utils.model.Identity;
import com.google.common.collect.ImmutableList;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue(UserType.user)
public class User extends AbstractUser {

    public User(Identity id, String userName, String email, String password, String salt, String firstName, String lastName, String phone, String phoneCountry, LocalDateTime createdOn, Role role, List<Token> tokens) {
        super(id, userName, email, password, salt, firstName, lastName, phone, phoneCountry, createdOn, role, tokens);
    }

    /**
     * Default constructor is needed by JPA
     */
    public User() {}

    @Override
    public Collection<? extends GrantedAuthority> getRoles() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", phone='").append(phone).append('\'');
        sb.append(", phoneCountry='").append(phoneCountry).append('\'');
        sb.append(", createdOn=").append(createdOn);
        sb.append(", role=").append(role);
        sb.append(", active=").append(active);
        sb.append(", activationToken=").append(activationToken);
        sb.append(", activationExpiresOn=").append(activationExpiresOn);
        sb.append('}');
        return sb.toString();
    }
}
