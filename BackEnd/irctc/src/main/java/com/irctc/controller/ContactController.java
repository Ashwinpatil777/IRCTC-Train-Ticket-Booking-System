package com.irctc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.irctc.model.Contact;
import com.irctc.repository.ContactRepository;


@RestController
@RequestMapping("contact")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping
    public Contact saveContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }
}
