package com.hskl.imst.meineprodukte;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Details extends AppCompatActivity {

    private Long id;
    private TextView mDetails,mVerfallsdatum, beschreibung;
    private Produktdatenbank db;
    private Produkt produkt;
    private ImageView mImage;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) setTheme(R.style.DayThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) setTheme(R.style.NightThemeToolbar);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_UNDEFINED) setTheme(R.style.DayThemeToolbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#60000000"));
        }

        //Collapsing Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        CollapsingToolbarLayout coll_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        coll_toolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));


        mDetails = findViewById(R.id.noteDesc);
        mVerfallsdatum = findViewById(R.id.mVerfallsdatum);
        mImage = findViewById(R.id.mImage);
        beschreibung = findViewById(R.id.beschreibung);


        Intent i = getIntent();
        id = i.getLongExtra("ID",0);

        db = new Produktdatenbank(this);
        produkt = db.getNote(id);

        coll_toolbar.setTitle(produkt.getTitle());
        mDetails.setText(produkt.getContent());
        mVerfallsdatum.setText(produkt.getVerfallsdatum());


        if (mDetails.length() == 0) beschreibung.setVisibility(View.GONE);
        else beschreibung.setVisibility(View.VISIBLE);

        byte[] noteImage = produkt.getImage();
        if (noteImage == null) {
            mImage.setImageResource(R.drawable.default_topbar);
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(produkt.getImage(), 0, produkt.getImage().length);
            mImage.setImageBitmap(bitmap);
        }

        FloatingActionButton fab = findViewById(R.id.edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Details.this, Edit.class).putExtra("ID", produkt.getID()));

           }
        });
    }
}