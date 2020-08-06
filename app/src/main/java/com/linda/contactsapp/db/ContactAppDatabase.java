package com.linda.contactsapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.linda.contactsapp.db.model.Contact;
import com.linda.contactsapp.repository.ContactRepository;


@Database(entities = {Contact.class}, version = 2)
public abstract class ContactAppDatabase extends RoomDatabase {
    public abstract ContactRepository contactRepository();
}
