package edu.und.sanjaya.database.groceryshopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanjaya on 11/30/17.
 */

public class ProductDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[ ] allColumns = { MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_PRICE };

    public ProductDataSource( Context context ) {
        dbHelper = new MySQLiteHelper( context );
    }

    public void open( ) throws SQLException {
        database = dbHelper.getWritableDatabase( );
    }

    public void close( ) {
        dbHelper.close( );
    }

    public void createProduct( String name, double price ) {
        // insert into  comments  values( 'Very nice' );
        ContentValues values = new ContentValues( );
        values.put( MySQLiteHelper.COLUMN_NAME, name );
        values.put(MySQLiteHelper.COLUMN_PRICE,price);
        database.insert( MySQLiteHelper.TABLE_PRODUCT, null, values );
    }

    public void updateProduct( String name, double price ) {
        // delete from  comments  where  _id = id;
        ContentValues values = new ContentValues( );
        values.put( MySQLiteHelper.COLUMN_NAME, name );
        values.put(MySQLiteHelper.COLUMN_PRICE,price);
        System.out.println( "Product updated with name: " + name );
        database.update(MySQLiteHelper.TABLE_PRODUCT, values,
                MySQLiteHelper.COLUMN_NAME + " = '" + name+"'", null );
    }
    public void deleteProduct( Product product ) {
        // delete from  comments  where  _id = id;
        String name = product.getName( );
        System.out.println( "Product deleted with name: " + name );
        database.delete( MySQLiteHelper.TABLE_PRODUCT,
                MySQLiteHelper.COLUMN_NAME + " = " + name, null );
    }

    public List<Product> getAllProducts( ) {
        // select  _id, comment  from  comments;
        List<Product> products = new ArrayList<>( );
        Cursor cursor = database.query( MySQLiteHelper.TABLE_PRODUCT,
                allColumns, null, null, null, null, null );
        cursor.moveToFirst( );
        while ( !cursor.isAfterLast( ) ) {
            Product product = cursorToProduct( cursor );
            products.add( product );
            cursor.moveToNext( );
        }
        // Make sure to close the cursor.
        cursor.close( );
        return products;
    }

    private Product cursorToProduct( Cursor cursor ) {
        Product product = new Product( );
        product.setName(cursor.getString( 0 ) );
        product.setPrice( cursor.getDouble( 1) );
        return product;
    }

    public Product getProduct(String name){
        String query = "SELECT * FROM "+MySQLiteHelper.TABLE_PRODUCT+" WHERE "+MySQLiteHelper.COLUMN_NAME+"='" + name+"'";
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Product product = cursorToProduct( cursor );
        return product;
    }

}
