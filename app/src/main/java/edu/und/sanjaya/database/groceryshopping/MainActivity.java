package edu.und.sanjaya.database.groceryshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
    private String TAG = "MainActivity";
    private CouponDataSource datasource;
    private ProductDataSource productDataSource;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new CouponDataSource( this );
        datasource.open( );
        productDataSource = new ProductDataSource( this );
        productDataSource.open( );

    }

    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }

    public void search(View arg0){
        EditText etBudget = (EditText) findViewById(R.id.budget);
        setContentView(R.layout.activity_main_result);
        TextView tvBudget = (TextView) findViewById(R.id.information1);
        tvBudget.setText("Your budget: $"+etBudget.getText().toString());
        TextView tvInfo = (TextView) findViewById(R.id.information);
        Double budget = Double.valueOf(etBudget.getText().toString());
        Log.i(TAG,budget.toString());

        List<Coupon> eligibleCoupons = datasource.getLargestDiscountCoupons(budget);
        double totalDiscount =0;
        double totalCost =0;
        List<String> eligibleProductNames = new ArrayList<>();
        for (Coupon coupon:eligibleCoupons){
            eligibleProductNames.addAll(coupon.getProductNames());
            totalDiscount+=coupon.getDiscount();
            totalCost+=datasource.getTotalCost(coupon.getId());
        }
        if (eligibleCoupons.size()>0){
            list = (ListView) findViewById(R.id.listView);

            showList(eligibleProductNames);
            tvInfo.setText("$" + (totalCost - totalDiscount) + " after $" + totalDiscount + " off!");
        }else{
            tvInfo.setText("Not matching coupon found!");
        }
        /*
        Coupon coupon = datasource.getLargestDiscountCoupon(budget);
        if (coupon.getId()!=0) {
            Log.i(TAG, "Largetst value:" + coupon.toString());
            double totalCost = datasource.getTotalCost(coupon.getId());
            list = (ListView) findViewById(R.id.listView);

            showList(coupon.getProductNames());
            tvInfo.setText("$" + (totalCost - coupon.getDiscount()) + " after " + coupon.getDiscount() + " off!");
        }else{
            tvInfo.setText("Not matching coupon found!");
        }
        etBudget.setEnabled(false);
        */

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
            Intent i = new Intent( MainActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( MainActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( MainActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( MainActivity.this, ShoppingActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_reset){
            datasource.reset();
            Intent i = new Intent( MainActivity.this, ProductActivity.class );
            startActivity( i );
    }
        return super.onOptionsItemSelected( item );
    }

    protected void showList(List<String> productNames){
        List<Product> products = new ArrayList<>();
        for (String name:productNames){
            products.add(productDataSource.getProduct(name));
        }
        if(products.size()<1)return;
        for(int i=0;i<products.size();i++){
            HashMap<String,String> product = new HashMap<String,String>();
            product.put("Sequence",String.valueOf(i+1));
            product.put("Name",products.get(i).getName());
            product.put("Price",String.valueOf(products.get(i).getPrice()));
            productList.add(product);
        }

        try {
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, productList, R.layout.shopping_table_view,
                    new String[]{"Sequence", "Name","Price"},
                    new int[]{R.id.sequence, R.id.name, R.id.price}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    // get filled view from SimpleAdapter
                    View itemView=super.getView(position, convertView, parent);
                    // find our button there
                    TextView tv = (TextView) itemView.findViewById(R.id.name);
                    ProductActivity.makeTextViewHyperlink(tv);
                    final CheckBox cb = (CheckBox)itemView.findViewById(R.id.checkbox);
                    final TextView etName = (TextView) itemView.findViewById(R.id.name);
                    final TextView etPrice = (TextView) itemView.findViewById(R.id.price);
                    etPrice.setEnabled(false);
                    tv.setOnClickListener( new View.OnClickListener( ) {
                        @Override
                        public void onClick( View v ) {

                            Intent intent = new Intent( MainActivity.this, ProductDetailActivity.class );
                            intent.putExtra( "name", etName.getText().toString( ) );
                            intent.putExtra( "price", etPrice.getText().toString( ) );
                            startActivity( intent );
                        }
                    } );
                    return itemView;
                }
            };

            list.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
