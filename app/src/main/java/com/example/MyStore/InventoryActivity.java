package com.example.MyStore;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.MyStore.data.StoreContract.StoreEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;




public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
        {

    private final static int URI_LOADER = 0;
            ListView lv;
            StoreAdapter storeAdapter;

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, DetailActivity.class);
            startActivity(intent);
        });

        lv = findViewById(R.id.listView);
        View emptyView = findViewById(R.id.empty_view);
        lv.setEmptyView(emptyView);

        storeAdapter = new StoreAdapter(this,  null);
        lv.setAdapter(storeAdapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
                    Intent i = new Intent(InventoryActivity.this, DetailActivity.class);
                    Uri inputUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                    i.setData(inputUri);
                    startActivity(i);

                });
        



                    invalidateOptionsMenu();

                    getSupportLoaderManager().initLoader(URI_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
                MenuItem mItem = menu.findItem(R.id.deleteAll);
                    if (lv.getAdapter().getCount() == 0){
                        mItem.setEnabled(false);
                    }else mItem.setEnabled(true);

                return super.onPrepareOptionsMenu(menu); }

            @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.deleteAll:
                        int delRows = getContentResolver().delete(StoreEntry.CONTENT_URI, null, null);
                        String delSuccessfulText = delRows + " items deleted";
                        if(delRows < 0 )
                        {Toast.makeText(this, "Error deleting items", Toast.LENGTH_SHORT).show();}
                        else
                            Toast.makeText(this, delSuccessfulText, Toast.LENGTH_SHORT).show();

                        return true;

//                    case R.id.settings:
//                        // do nothing for now
//                        return true;
//
//                    case R.id.about:
//                        // do nothing for now
//                        return true;

                    case R.id.close:
                        finish();
                       return true;

                }
                return super.onOptionsItemSelected(item);
            }

            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

                switch (id){
                    case URI_LOADER:
            String[] projection = new String[]{
                    StoreEntry.ID,
                    StoreEntry.PHOTO,
                    StoreEntry.PRODUCT_NAME,
                    StoreEntry.QUANTITY,
                    StoreEntry.PRICE};

           return new CursorLoader(this, StoreEntry.CONTENT_URI, projection, null, null, null);

                    default: return null;
                }

            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                storeAdapter.swapCursor(data);


            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                storeAdapter.swapCursor(null);

            }
        }