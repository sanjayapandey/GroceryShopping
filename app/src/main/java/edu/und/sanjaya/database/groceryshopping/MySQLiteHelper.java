package edu.und.sanjaya.database.groceryshopping;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sanjaya on 11/30/17.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    public  static final String TABLE_PRODUCT = "product";
    public  static final String COLUMN_NAME      = "name";
    public  static final String COLUMN_PRICE = "price";

    public static final String COLUMN_ID = "id";
    public  static final String TABLE_COUPON = "coupon";
    public  static final String COLUMN_DISCOUNT = "discount";

    public  static final String TABLE_COUPON_PRODUCT = "coupon_product";
    public  static final String COLUMN_PRODUCT_ID      = "product_id";
    public  static final String COLUMN_COUPON_ID = "coupon_id";

    private static final String DATABASE_NAME  = "applicationdata";
    private static final int DATABASE_VERSION  = 1;

    // create table product(
    //   name varchar primary key,
    //   price double );
    private static final String TABLE_CREATE_PRODUCT = "create table "
            + TABLE_PRODUCT + "( " + COLUMN_NAME + " varchar primary key, "
            + COLUMN_PRICE + " double );";

    private static final String TABLE_CREATE_COUPON = "create table "
            + TABLE_COUPON + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DISCOUNT + " double );";

    private static final String TABLE_CREATE_COUPON_PRODUCT = "create table "
            + TABLE_COUPON_PRODUCT+ "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COUPON_ID + " integer, "+COLUMN_NAME+" varchar );";


    public MySQLiteHelper( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase database ) {
        database.execSQL( TABLE_CREATE_PRODUCT);
        database.execSQL(TABLE_CREATE_COUPON);
        database.execSQL(TABLE_CREATE_COUPON_PRODUCT);
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        Log.w( MySQLiteHelper.class.getName( ), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data" );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_PRODUCT );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_COUPON );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_COUPON_PRODUCT );
        onCreate( db );
    }
}
