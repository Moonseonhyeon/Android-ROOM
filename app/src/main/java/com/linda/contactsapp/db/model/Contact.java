package com.linda.contactsapp.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true) // 기본키 + 시퀀스
    @ColumnInfo(name="contact_id")
    private long id;

    @ColumnInfo(name="contact_name")
    private String name;

    @ColumnInfo(name="contact_email")
    private String email;

    @ColumnInfo(name="profileURL")
    private  String profileURL;

    @Ignore
    public Contact() { }

    @Ignore //jpa는 setter로 db에 넣는데 안드로이드는 생성자로 db에 넣음.
    public Contact(long id, String name, String email, String profileURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileURL = profileURL;
    }

    //이 생성자로만 db에 set할거다! ROOM이 알고 있는 생성자는 딱 하나만 있어야 한다.
    public Contact(String name, String email, String profileURL) {
        this.name = name;
        this.email = email;
        this.profileURL = profileURL;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }
}

/**
 *  @Ignore // 엔티티에 지속하고 싶지 않은 필드나 생성자가 있는 경우 @Ignore를 사용한다.
 */