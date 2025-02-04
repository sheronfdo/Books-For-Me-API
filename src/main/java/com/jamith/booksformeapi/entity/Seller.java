package com.jamith.booksformeapi.entity;

import com.jamith.booksformeapi.util.SellerType;
import com.jamith.booksformeapi.util.UserRole;

import java.util.Date;
import java.util.Map;

public class Seller {
    private String sellerId;
    private SellerType sellerType;
    private String fullNameOrRepresentative;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Map<String, String> address;
    private Map<String, String> businessDetails;
    private boolean verified;
    private Date createdAt;
    private UserRole role;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public SellerType getSellerType() {
        return sellerType;
    }

    public void setSellerType(SellerType sellerType) {
        this.sellerType = sellerType;
    }

    public String getFullNameOrRepresentative() {
        return fullNameOrRepresentative;
    }

    public void setFullNameOrRepresentative(String fullNameOrRepresentative) {
        this.fullNameOrRepresentative = fullNameOrRepresentative;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public void setAddress(Map<String, String> address) {
        this.address = address;
    }

    public Map<String, String> getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(Map<String, String> businessDetails) {
        this.businessDetails = businessDetails;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
