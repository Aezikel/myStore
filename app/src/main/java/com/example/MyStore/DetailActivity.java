package com.example.MyStore;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.MyStore.R;
import com.example.MyStore.data.StoreContract.StoreEntry;
import com.example.MyStore.data.StoreDbHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mDetailsChanged = false;
    private Uri mCurrentUri;
    private static final int EXISTING_URI_LOADER = 0;

    private ImageView productImage;
    private FloatingActionButton photoFab;
    private TextInputEditText productName;
    private TextInputEditText priceTag;
    private TextInputEditText quantity;
    private ImageButton addButton;
    private ImageButton removeButton;
    private TextInputEditText description;
    private TextInputEditText supplier;
    private TextInputEditText contact;
    private TextInputEditText email;

    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    private View.OnTouchListener mTouchListener = (v, event) -> {
        mDetailsChanged = true;
        return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCurrentUri = getIntent().getData();

        if (mCurrentUri == null) {
            setTitle("Add item");
        } else {
            setTitle("Edit item");
            getSupportLoaderManager().initLoader(EXISTING_URI_LOADER, null, this);

        }


        productImage = findViewById(R.id.productImage);
        photoFab = findViewById(R.id.fab_product_detail);
        productName = findViewById(R.id.productEditText);
        priceTag = findViewById(R.id.priceEditText);
        quantity = findViewById(R.id.quantityEditText);
        addButton = findViewById(R.id.add_button);
        removeButton = findViewById(R.id.remove_button);
        description = findViewById(R.id.descEditText);
        supplier = findViewById(R.id.supplierEditText);
        contact = findViewById(R.id.contactEditText);
        email = findViewById(R.id.emailEditText);

//        photoFab.setOnTouchListener(mTouchListener);
//        productName.setOnTouchListener(mTouchListener);
//        priceTag.setOnTouchListener(mTouchListener);
//        quantity.setOnTouchListener(mTouchListener);
//        addButton.setOnTouchListener(mTouchListener);
//        removeButton.setOnTouchListener(mTouchListener);
//        description.setOnTouchListener(mTouchListener);
//        supplier.setOnTouchListener(mTouchListener);
//        contact.setOnTouchListener(mTouchListener);
//        email.setOnTouchListener(mTouchListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sQuantity = Integer.parseInt(quantity.getText().toString().trim());
                sQuantity++;
                quantity.setText(String.valueOf(sQuantity));
            }

        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sQuantity = Integer.parseInt(quantity.getText().toString().trim());
                if (sQuantity>=1){
                    sQuantity--;
                    quantity.setText(String.valueOf(sQuantity));

                }

            }

        });

        photoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.home_detail_icon || item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(DetailActivity.this);
            return true;
        } else if (item.getItemId() == R.id.save_detail_icon) {
            saveInventory();
            finish();
            return true;
        } else if (item.getItemId() == R.id.discard_detail_icon) {
            // discard();
            return true;
        }


        return super.onOptionsItemSelected(item);


    }

    private void saveInventory() {

//        productImage.setDrawingCacheEnabled(true);
//        productImage.buildDrawingCache();
//        Bitmap bit = productImage.getDrawingCache();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        byte[] data = stream.toByteArray();
//        bit.compress(Bitmap.CompressFormat.JPEG,100,stream);

        Bitmap bmp = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();

        String name = productName.getText().toString().trim();
        String price = priceTag.getText().toString().trim();
        String qty = quantity.getText().toString().trim();
        String desc = description.getText().toString().trim();
        String supplierName = supplier.getText().toString().trim();
        String contactInfo = contact.getText().toString().trim();
        String mail = email.getText().toString().trim();

        if (mCurrentUri == null && TextUtils.isEmpty(name) //add photo condition and quantity
                && TextUtils.isEmpty(price) && qty == null
                && TextUtils.isEmpty(desc) && TextUtils.isEmpty(supplierName)
                && TextUtils.isEmpty(contactInfo) && TextUtils.isEmpty(mail)) {
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(StoreEntry.PHOTO, byteArray);
        cv.put(StoreEntry.PRODUCT_NAME, name);
        cv.put(StoreEntry.PRICE, price);

        cv.put(StoreEntry.DESCRIPTION, desc);
        cv.put(StoreEntry.SUPPLIER, supplierName);
        cv.put(StoreEntry.CONTACT, contactInfo);
        cv.put(StoreEntry.EMAIL, mail);
        int defaultQuantity = 0;
        if (!TextUtils.isEmpty(qty)) { defaultQuantity = Integer.parseInt(qty);}

        cv.put(StoreEntry.QUANTITY, defaultQuantity);

        if (mCurrentUri == null){


            Uri insertUri = getContentResolver().insert(StoreEntry.CONTENT_URI, cv);

            if(insertUri == null){
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();}
            else{
                Toast.makeText(this, "Successfully added item to inventory", Toast.LENGTH_SHORT).show();}

        }

        else {

            int rowsAffected= getContentResolver().update(mCurrentUri, cv, null,null);
            if(rowsAffected == 0){
                Toast.makeText(this, "Item update failed", Toast.LENGTH_SHORT).show();}
            else{
                Toast.makeText(this, "Item update successful", Toast.LENGTH_SHORT).show();}

        }




    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {StoreEntry.PHOTO,
                StoreEntry.PRODUCT_NAME,
                StoreEntry.PRICE, StoreEntry.QUANTITY,
                StoreEntry.DESCRIPTION, StoreEntry.SUPPLIER,
                StoreEntry.CONTACT, StoreEntry.EMAIL};
        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // GET PHOTO FROM CURSOR
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst()){
            byte[] img = data.getBlob(data.getColumnIndex(StoreEntry.PHOTO));
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            String productName = data.getString(data.getColumnIndex(StoreEntry.PRODUCT_NAME));
            String priceTag = data.getString(data.getColumnIndex(StoreEntry.PRICE));
            int quantity = data.getInt(data.getColumnIndex(StoreEntry.QUANTITY));
            String description = data.getString(data.getColumnIndex(StoreEntry.DESCRIPTION));
            String supplier = data.getString(data.getColumnIndex(StoreEntry.SUPPLIER));
            String contact = data.getString(data.getColumnIndex(StoreEntry.CONTACT));
            String email = data.getString(data.getColumnIndex(StoreEntry.EMAIL));

            this.productImage.setImageBitmap(bitmap);
            this.productName.setText(productName);
            this.priceTag.setText(priceTag);
            this.quantity.setText(String.valueOf(quantity));
            this.description.setText(description);
            this.supplier.setText(supplier);
            this.contact.setText(contact);
            this.email.setText(email);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        this.productName.setText("");
        priceTag.setText("");
        this.quantity.setText(0);
        this.description.setText("");
        this.supplier.setText("");
        this.contact.setText("");
        this.email.setText("");

    }

    public void fabDialog(){
        String[] fabArray = new String[] {"Gallery", "Photo", "Remove"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Set photo")
                .setItems(fabArray, (dialog, which) -> {
                    switch (which){
                        case 0:
                            Intent photoPicker = new Intent(Intent.ACTION_PICK);
                            photoPicker.setType("image/*");
                            startActivityForResult(photoPicker, SELECT_PHOTO );
                            break;

                        case 1:
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAPTURE_PHOTO);
                            break;

                        case 2:
                            productImage.setImageResource(R.color.blue);
                            break;
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    productImage.setImageBitmap(selectedImage);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }else if(requestCode == CAPTURE_PHOTO){
            if(resultCode == RESULT_OK) {
                //onCaptureImageResult(data);
            }
        }
    }

}