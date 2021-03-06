package com.linda.contactsapp.db;

import android.content.Context;

import androidx.room.Room;

import com.linda.contactsapp.db.ContactAppDatabase;

public class DBConn {

    private static ContactAppDatabase contactAppDatabase;

    public static ContactAppDatabase getConnetion(Context context) {
        if (contactAppDatabase == null) {
            contactAppDatabase =
                    Room.databaseBuilder(context, ContactAppDatabase.class, "ContactDB")
                            .allowMainThreadQueries()
                            .build();
        }

        return contactAppDatabase;
    }
}
