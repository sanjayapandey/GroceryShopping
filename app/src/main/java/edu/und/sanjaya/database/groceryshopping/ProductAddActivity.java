package edu.und.sanjaya.database.groceryshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class ProductAddActivity extends AppCompatActivity {
    private ProductDataSource datasource;
    private EditText etName;
    private EditText etPrice;
    private String TAG = "ProductAddActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);
        etName = (EditText) findViewById(R.id.name);
        etPrice = (EditText) findViewById(R.id.price);

        datasource = new ProductDataSource( this );
        datasource.open( );
        List<Product> values = datasource.getAllProducts( );

        for (Product product:values){
            Log.i(TAG,product.toString());
        }

    }

    public void save(View arg0){
        datasource.createProduct(etName.getText().toString(), Double.valueOf(etPrice.getText().toString()));
        Log.i(TAG,etName.getText().toString()+etPrice.getText().toString());
        Toast.makeText(getApplicationContext(),"Product added successfully!", Toast.LENGTH_LONG).show();
        etName.setText("");
        etPrice.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        // noinspection SimplifiableIfStatement
        if ( id == R.id.action_product ) {
            Intent i = new Intent( ProductAddActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( ProductAddActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( ProductAddActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( ProductAddActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
}
