package com.hskl.imst.meineprodukte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Edit extends AppCompatActivity implements Dialog.DialogListener {
    private static final int REQUEST_CODE_GALLERY = 999;
    private Long id;
    private Toolbar toolbar;
    private EditText noteTitle,noteDetails, noteVerfallsdatum;
    private Produktdatenbank db;
    private Produkt produkt;
    private ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) setTheme(R.style.DayThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) setTheme(R.style.NightThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_UNDEFINED) setTheme(R.style.DayThemeToolbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#60000000"));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        CollapsingToolbarLayout coll_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent i = getIntent();
        id = i.getLongExtra("ID",0);

        db = new Produktdatenbank(this);
        produkt = db.getNote(id);

        noteTitle = findViewById(R.id.noteTitle);
        noteDetails = findViewById(R.id.noteDetails);
        noteVerfallsdatum = findViewById(R.id.noteVerfallsdatum);
        mImage = findViewById(R.id.mImage);

        coll_toolbar.setTitle(produkt.getTitle());
        coll_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        noteTitle.setText(produkt.getTitle());
        noteDetails.setText(produkt.getContent());
        noteVerfallsdatum.setText(produkt.getVerfallsdatum());

        noteTitle.addTextChangedListener(new TextWatcher() {
            CollapsingToolbarLayout coll_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                coll_toolbar.setTitle("Neues Produkt");
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0) {
                    coll_toolbar.setTitle(s);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        byte[] noteImage = produkt.getImage();
        if (noteImage == null) {
            mImage.setImageResource(R.drawable.default_topbar);
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(produkt.getImage(), 0, produkt.getImage().length);
            mImage.setImageBitmap(bitmap);
        }

        FloatingActionButton addImageButton = (FloatingActionButton) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        Edit.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        Button delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }


        });


        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }


    public void goToMain() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            produkt.setTitle(noteTitle.getText().toString());
            produkt.setContent(noteDetails.getText().toString());
            produkt.setVerfallsdatum(noteVerfallsdatum.getText().toString());
            produkt.setImage(imageViewToByte(mImage));
            if (noteTitle.length() == 0) {
                noteTitle.setError("Produktname eingeben");
                Toast.makeText(this,  "Produktname erforderlich", Toast.LENGTH_SHORT).show();
            }
            else {
                int id = db.editNote(produkt);
                Toast.makeText(this,  produkt.getTitle() + " " + getString(R.string.edit), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Details.class).putExtra("ID", produkt.getID()));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.rights, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView mImage = findViewById(R.id.mImage);
                mImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
       }
        else {
            ImageView noteImage = findViewById(R.id.noteImage);
            noteImage.setImageResource(R.drawable.default_topbar);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static byte[] imageViewToByte(ImageView image) {
        ImageView mImage = (ImageView) (image);
        Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }

    @Override
    public void onYesClicked() {
        db.deleteNote(id);
        goToMain();
        Toast.makeText(Edit.this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
    }
}
