package com.example.MyStore.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract  {
    private StoreContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.MyStore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String STORE_PATH = "store";


    public static final class StoreEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, STORE_PATH);

        public static final String TABLE_NAME = "store";

        public static final String ID = BaseColumns._ID;

        public static final String PHOTO = "photo";

        public static final String PRODUCT_NAME = "product";

        public static final String PRICE = "price";

        public static final String QUANTITY = "quantity";

        public static final String DESCRIPTION = "description";

        public static final String SUPPLIER = "supplier";

        public static final String CONTACT = "contact";

        public static final String EMAIL = "email";


    }
}
