package com.spring.hotel.model;

import lombok.Data;

@Data
public class Contact {
    private Long cId;
    private String email;
    private String contactName;
    private Long userId;

    public Contact(Long cId, String email, String contactName, Long userId) {
        this.cId = cId;
        this.email = email;
        this.contactName = contactName;
        this.userId = userId;
    }
}
