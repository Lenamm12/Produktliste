package com.hskl.imst.meineprodukte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
//Main Activity: Hauptscreen mit RecyclerView

        // Angezeigte Elemente auf der "Startseite"
        private  ImageView mImageView;
        private Toolbar toolbar;
        private RecyclerView recyclerView;
        private Adapter adapter;
        private List<Produkt> produkte;


        //Image Request Codes
        private static final int IMAGE_PICK_CODE = 1000;
        private static final int PERMISSION_CODE = 1001;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) setTheme(R.style.DayTheme);
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) setTheme(R.style.NightTheme);
                if (currentNightMode == Configuration.UI_MODE_NIGHT_UNDEFINED) setTheme(R.style.DayTheme);



                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);


                // Toolbar wird mit der ID referenziert und farblich angepasst
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));

                //Floatingcontext/longclick binden an recyclerview
                //constraint = findViewById(R.id.constraint);
                //registerForContextMenu(constraint);

                mImageView = findViewById(R.id.mImage); //ImageView

                //Erstellen einer neuen Datenbank
                Produktdatenbank db = new Produktdatenbank(this);

                produkte = db.getProdukte();
                recyclerView = findViewById(R.id.listOfProdukte);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new Adapter(this, produkte);
                recyclerView.setAdapter(adapter);


                FloatingActionButton fab = findViewById(R.id.add);
                fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, AddProdukt.class));
                        }
                });
                }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.main_menu, menu); //activate options menu_add

                //Suchfunktion
                MenuItem searchItem = menu.findItem(R.id.search); //REFERENZ MENÜITEM
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setIconified(false);
                searchView.setMaxWidth(Integer.MAX_VALUE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                                return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                                adapter.getFilter().filter(newText);
                                return false;
                        }
                });
                return true;
        }

}