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
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class AbstractUser {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id", nullable = false, updatable = false))
    Identity id;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "salt", nullable = false)
    String salt;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "phone")
    String phone;

    @Column(name = "phone_country")
    String phoneCountry;

    @Column(name = "created_on", nullable = false, updatable = false)
    LocalDateTime createdOn;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    List<Token> tokens;

    @Column(name = "is_active")
    Boolean active = Boolean.FALSE;

    @Column(name = "activation_token")
    @Type(type = "pg-uuid")
    UUID activationToken;

    @Column(name = "activation_expires_on")
    LocalDateTime activationExpiresOn;

    AbstractUser() {
    }

    AbstractUser(Identity id, String username, String email, String password, String salt, String firstName, String lastName, String phone, String phoneCountry, LocalDateTime createdOn, Role role, List<Token> tokens) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.phoneCountry = phoneCountry;
        this.createdOn = createdOn;
        this.role = role;
        this.tokens = tokens;
    }

    public Identity getId() {
        return id;
    }

    public void setId(Identity id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCountry() {
        return phoneCountry;
    }

    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UUID getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(UUID activationToken) {
        this.activationToken = activationToken;
    }

    public LocalDateTime getActivationExpiresOn() {
        return activationExpiresOn;
    }

    public void setActivationExpiresOn(LocalDateTime activationExpiresOn) {
        this.activationExpiresOn = activationExpiresOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUser that = (AbstractUser) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(salt, that.salt) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(createdOn, that.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract Collection<? extends GrantedAuthority> getRoles();

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

}
