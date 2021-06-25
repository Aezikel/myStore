package com.example.MyStore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.MyStore.data.StoreContract.StoreEntry;
import com.example.MyStore.data.StoreDbHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


public class StoreAdapter extends CursorAdapter {
    private static boolean buttonClick = false;
    int itemQuantity;
    String qtyString;

    public StoreAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView item_image = view.findViewById(R.id.empty_image_item);
        MaterialTextView product_name = view.findViewById(R.id.product_text);
        MaterialTextView quantity = view.findViewById(R.id.quantity_text);
        MaterialTextView price = view.findViewById(R.id.price_text);
        MaterialButton button = view.findViewById(R.id.sale_button);

        byte[] image = cursor.getBlob(cursor.getColumnIndex(StoreEntry.PHOTO));
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0 , image.length);
//        int imgHeight = item_image.getHeight();
//        int imgWidth = item_image.getWidth();
//        item_image.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth,imgHeight,false));
        item_image.setImageBitmap(bmp);


        product_name.setText(cursor.getString(cursor.getColumnIndex(StoreEntry.PRODUCT_NAME)));
        price.setText(cursor.getString(cursor.getColumnIndex(StoreEntry.PRICE)));
        itemQuantity = cursor.getInt(cursor.getColumnIndex(StoreEntry.QUANTITY));


        button.setOnClickListener(v -> {
            final String id = cursor.getString(cursor.getColumnIndex(StoreEntry.ID));
            if (itemQuantity>=1){
                itemQuantity--;
                StoreDbHelper mDbHelper = new StoreDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(StoreEntry.QUANTITY, itemQuantity);
                String selection = StoreEntry.ID + "=?";
                String[] selectionArgs = new String[]{id};
                db.update(StoreEntry.TABLE_NAME,values, selection, selectionArgs);


                String qtyToast = "1 " + product_name.getText() + " sold";
                Toast.makeText(context, qtyToast, Toast.LENGTH_SHORT).show();
                setQuantityText(quantity);

                db.close();

            }
            else  {
                Toast.makeText(context, "Item Sold out", Toast.LENGTH_SHORT ).show();}

        });

        setQuantityText(quantity);

    }

    private void setQuantityText(MaterialTextView quantity) {
        qtyString = String.valueOf(itemQuantity);
        quantity.setText(qtyString);

    }
}
