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
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProdukt extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText noteTitle,noteDetails,noteVerfallsdatum;
    private ImageView noteImage;
    private final int REQUEST_CODE_GALLERY = 999;
    private CollapsingToolbarLayout coll_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) setTheme(R.style.DayThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) setTheme(R.style.NightThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_UNDEFINED) setTheme(R.style.DayThemeToolbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#60000000"));
        }

        toolbar = findViewById(R.id.toolbar);
        noteTitle = findViewById(R.id.noteTitle);
        noteDetails = findViewById(R.id.noteDetails);
        noteVerfallsdatum = findViewById(R.id.noteVerfallsdatum);
        noteImage = findViewById(R.id.noteImage);
        coll_toolbar = findViewById(R.id.collapsing_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        coll_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        coll_toolbar.setTitle(getString(R.string.shortLabel));


        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                coll_toolbar.setTitle("Neues Produkt");
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0) coll_toolbar.setTitle(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        FloatingActionButton addImageButton = (FloatingActionButton) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        AddProdukt.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            Produkt produkt = new Produkt(
                    noteTitle.getText().toString(),
                    noteDetails.getText().toString(),
                    noteVerfallsdatum.getText().toString(),
                    imageViewToByte(noteImage));
            Produktdatenbank db = new Produktdatenbank(this);
            if (noteTitle.length() == 0) {
                noteTitle.setError(getString(R.string.enter_title));
                Toast.makeText(this,  getString(R.string.required), Toast.LENGTH_SHORT).show();
            }
            else {
                db.addNote(produkt);
                Toast.makeText(this,  produkt.getTitle() + " " + getString(R.string.create), Toast.LENGTH_SHORT).show();
                goToMain();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static byte[] imageViewToByte(ImageView image) {
            ImageView noteImage = image;
            Bitmap bitmap = ((BitmapDrawable) noteImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageInByte = baos.toByteArray();
            return imageInByte;
    }


    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

     @Override
     public void onBackPressed() {
        super.onBackPressed();
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

                ImageView noteImage = findViewById(R.id.noteImage);
                noteImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ImageView noteImage = findViewById(R.id.noteImage);
            noteImage.setImageResource(R.drawable.default_topbar);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}