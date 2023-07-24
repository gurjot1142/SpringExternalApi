package com.springboot.ext.service;

import com.springboot.ext.model.Contact;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {
    List<Contact> list = List.of(
            new Contact(1L, "amit@gmail.com", "Amit", 1311L),
            new Contact(2L, "anil@gmail.com", "Anil", 1311L),
            new Contact(3L, "rohan@gmail.com", "Rohan", 1312L),
            new Contact(4L, "sameer@gmail.com", "Sameer", 1314L)
    );

    public List<Contact> getContactsOfUser(Long userId) {
        return list.stream().filter(contact -> contact.getUserId().equals(userId)).collect(Collectors.toList());
    }
    public List<Contact> getAllContacts() {
        return list;
    }
}
