package com.linda.contactsapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.linda.contactsapp.adapter.ContactAdapter;
import com.linda.contactsapp.db.model.Contact;
import com.linda.contactsapp.db.ContactAppDatabase;
import com.linda.contactsapp.service.ContactService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.linda.contactsapp.util.ImageUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_Activity";
    private MainActivity mContext = MainActivity.this;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContactAdapter adapter;
    private FloatingActionButton fab;

    private ContactAppDatabase contactAppDatabase;
    private ContactService contactService;
    private List<Contact> contacts;

    // 사진 업로드
    private CircleImageView ivProfile;
   // private File tempFile;
    private String imageRealPath;
   // private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB관련
        contactAppDatabase = Room.databaseBuilder(getApplicationContext(), ContactAppDatabase.class, "ContactDB")
                .allowMainThreadQueries()//실제로 서비스할 때 전부 쓰레드 사용해야 함.
                .fallbackToDestructiveMigration()//DB스키마가 변경되면 이거 해야한다.
                .build();
        contactService = new ContactService(contactAppDatabase.contactRepository());



        initData();
        initObject(); //new 하거나 findBy하는거 다
        initDesign();
        initListener();
        tedPermission();
    }

    private void initData(){
        contacts = contactService.연락처전체보기();
    }

    private void initObject(){
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view_contacts);
        fab = findViewById(R.id.fab);
        ivProfile = findViewById(R.id.iv_profile);
        adapter = new ContactAdapter(this, contacts);
    }

    private void initDesign(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Contact App");

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactDialog();
            }
        });
    }



    public void addContactDialog(){
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_contact, null);
        final EditText etName = dialogView.findViewById(R.id.name);
        final EditText etEmail = dialogView.findViewById(R.id.email);

        // 갤러리 사진 가져오기
        ivProfile = dialogView.findViewById(R.id.iv_profile);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUpload.goToAlbum(mContext);
            }
        });

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 등록");
        dlg.setView(dialogView);
        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createContact(etName.getText().toString(), etEmail.getText().toString(), imageRealPath);
                notifyListener();
            }
        });
        dlg.setNegativeButton("닫기", null);
        dlg.show();
    }

    // 상세보기 (수정, 삭제)
    public void editContactDialog(final Contact contact){ //리사이클러뷰에서
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_add_contact, null);
        final EditText etName = dialogView.findViewById(R.id.name);
        final EditText etEmail = dialogView.findViewById(R.id.email);
        ivProfile = dialogView.findViewById(R.id.iv_profile);
        etName.setText(contact.getName());
        etEmail.setText(contact.getEmail());

        if(contact.getProfileURL() == null || contact.getProfileURL().equals("")){
            ivProfile.setImageResource(R.drawable.ic_person);
        }else{
            ImageUpload.setImage(contact.getProfileURL(), ivProfile);
        }

        // 갤러리 사진 수정하기
        ivProfile = dialogView.findViewById(R.id.iv_profile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUpload.goToAlbum(mContext);
            }
        });

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 수정");
        dlg.setView(dialogView);
        dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateContact(contact.getId(), etName.getText().toString(), etEmail.getText().toString(), imageRealPath);
            }
        });
        dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contactService.연락처삭제(contact.getId());
                notifyListener();
            }
        });
        dlg.show();
    }

    private void createContact(String name, String email, String profileUrl) {
        long contactId = contactService.연락처등록(new Contact(name, email, profileUrl));
        Contact contact = contactService.연락처상세보기(contactId);
        adapter.addItem(contact);
        notifyListener();
        imageRealPath = null; //왜 null하는거야??
    }

    private void updateContact(long contactId, String name, String email, String profileURL) {
        Contact updateContact = new Contact(contactId, name, email, profileURL);
        contactService.연락처수정(updateContact);
        notifyListener();
        imageRealPath = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        contactService.연락처전체삭제(); //DB만 삭제, adapter는 그대로...그럼 화면걍신을 한번 해주든 혹은 adapter내용을 비우면 된다
        notifyListener();
        Log.d(TAG, "onOptionsItemSelected: 삭제됨");
        return true;
    }

    public void notifyListener(){
        //DB내용 변경 -> adapter 데이터 변경 -> UI갱신
        // live data - MV
        //서버를 리액티브하게 짜면 RS Java
        //DB내용만 변경하면면        adapter.addItems(contactService.연락처전체보기()); //어댑터에 내용 변경
        adapter.addItems(contactService.연락처전체보기());
        adapter.notifyDataSetChanged();//UI갱신
    }

    // 이미지 선택 후 이미지 채우기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageUpload.PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            imageRealPath = ImageUpload.getRealPathFromURI(photoUri, MainActivity.this);
            ImageUpload.setImage(imageRealPath, ivProfile);
        }
    }

    // 권한 얻기
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }


    ///////////////////////



}