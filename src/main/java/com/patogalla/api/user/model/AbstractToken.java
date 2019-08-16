package com.patogalla.api.user.model;

import com.patogalla.api.utils.model.Identity;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "token_type")
public abstract class AbstractToken {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id", nullable = false, updatable = false))
    Identity id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false, updatable = false)) Identity userId;
    @Column(name = "created_on", nullable = false) LocalDateTime createdOn;
    @Column(name = "expires_on", nullable = false) LocalDateTime expiresOn;

    /**
     * Default constructor is needed by JPA
     */
    AbstractToken() {}

    public AbstractToken(Identity id, Identity userId, LocalDateTime createdOn, LocalDateTime expiresOn) {
        this.id = id;
        this.userId = userId;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    public Identity getId() {
        return id;
    }

    public void setId(Identity id) {
        this.id = id;
    }

    public Identity getUserId() {
        return userId;
    }

    public void setUserId(Identity userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(LocalDateTime expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractToken that = (AbstractToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createdOn, that.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
