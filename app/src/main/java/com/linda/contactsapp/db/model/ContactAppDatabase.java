package com.linda.contactsapp.db.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.linda.contactsapp.repository.ContactRepository;


@Database(entities = {Contact.class}, version = 1)
public abstract class ContactAppDatabase extends RoomDatabase {
    public abstract ContactRepository contactRepository();




}
