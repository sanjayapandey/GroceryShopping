package edu.und.sanjaya.database.groceryshopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanjaya on 11/30/17.
 */

public class CouponDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[ ] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_DISCOUNT };
    private String[ ] columnsCouponDetails = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_COUPON_ID,MySQLiteHelper.COLUMN_NAME };

    public CouponDataSource(Context context ) {
        dbHelper = new MySQLiteHelper( context );
    }

    public void open( ) throws SQLException {
        database = dbHelper.getWritableDatabase( );
    }

    public void close( ) {
        dbHelper.close( );
    }

    public void createCoupon( double discount , List<String> productName ) {
        // insert into  comments  values( 'Very nice' );
        ContentValues values = new ContentValues( );
        values.put( MySQLiteHelper.COLUMN_DISCOUNT, discount );
        long id = database.insert( MySQLiteHelper.TABLE_COUPON, null, values );
        for (String name:productName) {
            //insert into coupon_product table
            ContentValues values1 = new ContentValues();
            values1.put(MySQLiteHelper.COLUMN_COUPON_ID, id);
            values1.put(MySQLiteHelper.COLUMN_NAME, name);
            database.insert(MySQLiteHelper.TABLE_COUPON_PRODUCT,null, values1 );
        }
    }

    public void updateCoupon(int id, double discount ) {
        // delete from  comments  where  _id = id;
        ContentValues values = new ContentValues( );
        values.put( MySQLiteHelper.COLUMN_DISCOUNT, discount );

        System.out.println( "update coupon with id: " + id );
        database.update(MySQLiteHelper.TABLE_COUPON, values,
                MySQLiteHelper.COLUMN_ID + " = " + id+"", null );
    }
    public void deleteCoupon( int id ) {
        // delete from  coupon  where  _id = id;

        System.out.println( "Coupon deleted with id: " + id );
        database.delete( MySQLiteHelper.TABLE_COUPON,
                MySQLiteHelper.COLUMN_ID + " = " + id, null );
        database.delete(MySQLiteHelper.TABLE_COUPON_PRODUCT,
                MySQLiteHelper.COLUMN_COUPON_ID+"="+id,null);
    }

    public List<Coupon> getAllCoupon( ) {
        // select  _id, comment  from  comments;
        List<Coupon> coupons = new ArrayList<>( );
        Cursor cursor = database.query( MySQLiteHelper.TABLE_COUPON,
                allColumns, null, null, null, null, null );
        cursor.moveToFirst( );
        while ( !cursor.isAfterLast( ) ) {
            Coupon coupon = cursorToCoupon( cursor );
            coupons.add( coupon );
            cursor.moveToNext( );
        }
        // Make sure to close the cursor.
        cursor.close( );
        return coupons;
    }

    private Coupon cursorToCoupon( Cursor cursor ) {
        Coupon coupon = new Coupon( );
        coupon.setId(cursor.getInt( 0 ) );
        coupon.setDiscount( cursor.getDouble( 1 ) );
        return coupon;
    }

    public Coupon getCoupon(int id){
        List<String> productNames = new ArrayList<>();
        String query = "SELECT id,discount FROM "+MySQLiteHelper.TABLE_COUPON+" WHERE "+MySQLiteHelper.COLUMN_ID+"=" + id+"";
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Coupon coupon = cursorToCoupon( cursor );
        // Make sure to close the cursor.
        cursor.close( );
        //get datas

        String query1 = "SELECT name FROM "+MySQLiteHelper.TABLE_COUPON_PRODUCT+" WHERE "+MySQLiteHelper.COLUMN_COUPON_ID+"=" + id+"";
        Cursor  cursor1 = database.rawQuery(query1,null);

        cursor1.moveToFirst( );
        while ( !cursor1.isAfterLast( ) ) {
            productNames.add(cursor1.getString(0));
            cursor1.moveToNext( );
        }
        coupon.setProductNames(productNames);
        // Make sure to close the cursor.
        cursor1.close( );
        return coupon;
    }

    public List<CouponProduct> getAllCouponProduct( ) {
        // select  _id, comment  from  comments;
        List<CouponProduct> couponProducts = new ArrayList<>( );
        Cursor cursor = database.query( MySQLiteHelper.TABLE_COUPON_PRODUCT,
                columnsCouponDetails, null, null, null, null, null );
        cursor.moveToFirst( );
        while ( !cursor.isAfterLast( ) ) {
            CouponProduct couponProduct = cursorToCouponProduct( cursor );
            couponProducts.add( couponProduct );
            cursor.moveToNext( );
        }
        // Make sure to close the cursor.
        cursor.close( );
        return couponProducts;
    }

    private CouponProduct cursorToCouponProduct( Cursor cursor ) {
        CouponProduct couponProduct = new CouponProduct( );
        couponProduct.setId(cursor.getInt( 0 ) );
        couponProduct.setCouponId(cursor.getInt( 1));
        couponProduct.setProductName(cursor.getString(2));
        return couponProduct;
    }

    public List<Coupon> getLargestDiscountCoupons(Double budget){
        //get all coupon having total sum lessthen or equal to budget
        // select  _id, comment  from  comments;
        List<Coupon> coupons = new ArrayList<>( );
        Cursor cursor = database.query( MySQLiteHelper.TABLE_COUPON,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_DISCOUNT +" DESC");
        cursor.moveToFirst( );
        while ( !cursor.isAfterLast( ) ) {
            //check if the tototal amount is less or not to budget.
            Coupon coupon = getCoupon(cursor.getInt( 0 ));
            //calculate the total cost
            double totalCost = 0;
            for(String name: coupon.getProductNames()){
                //find product by name
                Product product = getProduct(name);
                totalCost = totalCost+product.getPrice();
            }
            System.out.println("Total cost for coupon:"+coupon.toString()+"is:"+totalCost);
            if ((totalCost-coupon.getDiscount()) <=budget){
                coupons.add( coupon );
            }
            cursor.moveToNext( );
        }
        // Make sure to close the cursor.
        cursor.close( );

        /*
        //selected items array
        List<String> productNames = new ArrayList<String>();
        double remainingBudget = budget;
        //define empty elibible coupons
        List<Coupon> eligibleCoupons = new ArrayList<>();

        //data are already sorted based on the discount
        for (int i=0;i<coupons.size();i++){
            Coupon coupon = coupons.get(i);
            //check if budget is remaining for purchase and items are not already selected.
            if(remainingBudget>=getTotalCost(coupon.getId()) && Collections.disjoint(productNames,coupon.getProductNames())){
                System.out.println("The eligible is: "+coupon.toString());
                eligibleCoupons.add(coupon);
                productNames.addAll(coupon.getProductNames());
                remainingBudget = remainingBudget-getTotalCost(coupon.getId());
            }
        }
        */

        /*
        After discussion
         */

        //define empty elibible coupons
        List<Coupon> eligibleCoupons = new ArrayList<>();
        List<Coupon> finalCoupons = new ArrayList<>();
        double maximumDiscount = 0;

        for(int i=0;i<coupons.size();i++) {
            Coupon coupon = coupons.get(i);
            System.out.println("Main coupon: "+coupon.toString());
            //selected items array
            List<String> productNames = new ArrayList<String>();
            double remainingBudget = budget;
            eligibleCoupons = new ArrayList<>();
            double maximumCurrentDiscount = 0;
            eligibleCoupons.add(coupon);
            productNames.addAll(coupon.getProductNames());
            remainingBudget = remainingBudget-getTotalCost(coupon.getId())+coupon.getDiscount();
            maximumCurrentDiscount+=coupon.getDiscount();

            for(int j=0;j<coupons.size();j++){
                Coupon coupon1 = coupons.get(j);
                if (coupon1.getId()==coupon.getId())
                    continue;
                System.out.println(">>>>>Inside"+coupon1.toString());
                System.out.println("REmaining budget: "+remainingBudget);
                //check if budget is remaining or not
                if(remainingBudget>=(getTotalCost(coupon1.getId())-coupon1.getDiscount()) && Collections.disjoint(productNames,coupon1.getProductNames())){
                    System.out.println("The eligible is: "+coupon1.toString());
                    eligibleCoupons.add(coupon1);
                    productNames.addAll(coupon1.getProductNames());
                    remainingBudget = remainingBudget-getTotalCost(coupon1.getId())+coupon1.getDiscount();
                    maximumCurrentDiscount+=coupon1.getDiscount();
                }else{
                    System.out.println("invalid");
                }
            }

            if (maximumCurrentDiscount>maximumDiscount){
                System.out.println("Eligible coupons with discount: "+ maximumCurrentDiscount);
                for (Coupon coupon3:eligibleCoupons){
                    System.out.println(coupon3.toString());
                }
                maximumDiscount = maximumCurrentDiscount;
                finalCoupons = new ArrayList<>();
                finalCoupons.addAll(eligibleCoupons);
            }

        }


        /*
        //get the largest discount
        if(coupons.size()==0) return new Coupon();

        double largest = coupons.get(0).getDiscount();
        int largestCouponId = coupons.get(0).getId();
        for (int i=1;i<coupons.size();i++){
            System.out.println("Check discount for:"+coupons.get(i).toString());
            if (coupons.get(i).getDiscount()>=largest){
                largestCouponId = coupons.get(i).getId();
            }
        }

        return getCoupon(largestCouponId);
        */
        return finalCoupons;
    }

    private Product getProduct(String name){
        String query = "SELECT * FROM "+MySQLiteHelper.TABLE_PRODUCT+" WHERE "+MySQLiteHelper.COLUMN_NAME+"='" + name+"'";
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Product product = cursorToProduct( cursor );
        return product;
    }
    private Product cursorToProduct( Cursor cursor ) {
        Product product = new Product( );
        product.setName(cursor.getString( 0 ) );
        product.setPrice( cursor.getDouble( 1) );
        return product;
    }

    public Double getTotalCost(int id){
        double totalAmount =0;
        String query1 = "SELECT name FROM "+MySQLiteHelper.TABLE_COUPON_PRODUCT+" WHERE "+MySQLiteHelper.COLUMN_COUPON_ID+"=" + id+"";
        Cursor  cursor1 = database.rawQuery(query1,null);

        cursor1.moveToFirst( );
        while ( !cursor1.isAfterLast( ) ) {
            Product product = getProduct(cursor1.getString(0));
            totalAmount = totalAmount+product.getPrice();
            cursor1.moveToNext( );
        }
        // Make sure to close the cursor.
        cursor1.close( );
        return totalAmount;
    }

    public boolean checkIfExists(Coupon coupon,Product product){
        String query = "SELECT * FROM "+MySQLiteHelper.TABLE_COUPON_PRODUCT+" WHERE "+MySQLiteHelper.COLUMN_COUPON_ID+"=" + coupon.getId()+" AND "+MySQLiteHelper.COLUMN_NAME+"='"+product.getName()+"'";
        Cursor  cursor = database.rawQuery(query,null);
        boolean isExists = false;
        cursor.moveToFirst( );
        if (cursor.moveToFirst()) {
            isExists = true;
        }
       return isExists;
    }

    public double getMaximumDiscountOverPurchase(List<Product> purchaseList) {
        Map<Integer,List<String>> map = new HashMap<>();
        List<Coupon> coupons = getAllCoupon();

        for (int i=0;i<purchaseList.size();i++){
            for (Coupon coupon:coupons){
                //for the first time, create empty map for each coupon
                if (!map.containsKey(coupon.getId())) {
                    map.put(coupon.getId(), new ArrayList<String>());
                }
                if (checkIfExists(coupon,purchaseList.get(i))){
                    List<String> names = map.get(coupon.getId());
                    names.add(purchaseList.get(i).getName());
                    map.put(coupon.getId(),names);
                }
            }
        }

        //check if map are full or not
        List<Coupon> validCoupons = new ArrayList<>();
        long i = 0;
        for (Map.Entry<Integer, List<String>> pair : map.entrySet()) {
            Coupon coupon = getCoupon(pair.getKey());
            List<String> names = pair.getValue();
            //check the length of names
            if(names.size() == coupon.getProductNames().size()){
                validCoupons.add(coupon);
                System.out.println("Valid coupon is: "+coupon.toString());
            }

        }

        //maximize the discount
        double maximumDiscount = 0;
        List<String> purchaseNames = new ArrayList<>();

        for(Coupon validCoupon: validCoupons){
            double currentMaximum = validCoupon.getDiscount();
            for (Product product:purchaseList){
                purchaseNames.add(product.getName());
            }

            //remove products from purchase list
            purchaseNames.removeAll(validCoupon.getProductNames());

            for (Coupon validCoupon1:validCoupons){
                //check if items exists or not
                if (purchaseNames.size()==0)
                    break;

                //check with remaining available coupon
                if (validCoupon1.getId()!=validCoupon.getId()){
                    //all remaining names should be exists on this coupon
                    boolean isValidCoupon = purchaseNames.containsAll(validCoupon1.getProductNames());
                    if (isValidCoupon){
                        currentMaximum+=validCoupon1.getDiscount();
                        purchaseNames.removeAll(validCoupon1.getProductNames());
                    }

                }
            }
            if (currentMaximum>maximumDiscount){
                maximumDiscount = currentMaximum;
            }

        }
        return maximumDiscount;
    }

    public void reset(){
        dbHelper.onUpgrade(database,1,2);
    }

}
