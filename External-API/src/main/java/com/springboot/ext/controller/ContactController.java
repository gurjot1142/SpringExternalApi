package com.springboot.ext.controller;

import com.springboot.ext.model.Contact;
import com.springboot.ext.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;


    @RequestMapping("/user/{userId}")
    public List<Contact> getContacts(@PathVariable("userId") Long userId) {
        return contactService.getContactsOfUser(userId);
    }

    @RequestMapping("/all")
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

}
