package edu.und.sanjaya.database.groceryshopping;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CouponDetailActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
    private CouponDataSource datasource;
    private String TAG = "CouponDetailActivity";
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        datasource = new CouponDataSource( this );
        datasource.open( );

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        Coupon coupon = datasource.getCoupon(Integer.valueOf(id));
        Log.i(TAG,"id is::::"+coupon);

        TextView etId = (TextView) findViewById(R.id.id);
        TextView etDiscount = (TextView) findViewById(R.id.discount);
        etId.setText(id);
        etDiscount.setText(String.valueOf(coupon.getDiscount()));

        list = (ListView) findViewById(R.id.couponListView);
        showList(coupon.getProductNames());

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
            Intent i = new Intent( CouponDetailActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( CouponDetailActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( CouponDetailActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( CouponDetailActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }

    protected void showList(List<String> products){
        if(products.size()<1)return;
        for(int i=0;i<products.size();i++){
            HashMap<String,String> name = new HashMap<String,String>();
            name.put("name",String.valueOf(products.get(i)));
            productList.add(name);
        }

        try {
            ListAdapter adapter = new SimpleAdapter(
                    CouponDetailActivity.this, productList, R.layout.coupon_product_detail_table_view,
                    new String[]{"name"},
                    new int[]{ R.id.name}
            ){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    // get filled view from SimpleAdapter
                    View itemView=super.getView(position, convertView, parent);
                    // find our button there
                    TextView tv = (TextView) itemView.findViewById(R.id.name);
                    final String name = tv.getText().toString();
                    ProductActivity.makeTextViewHyperlink(tv);

                    tv.setOnClickListener( new View.OnClickListener( ) {
                        @Override
                        public void onClick( View v ) {

                            Intent intent = new Intent( CouponDetailActivity.this, ProductDetailActivity.class );
                            intent.putExtra( "name", name );
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
    // Sets a hyperlink style to the textview.
    public static void makeTextViewHyperlink( TextView tv ) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( tv.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv.setText( ssb, TextView.BufferType.SPANNABLE );
        tv.setLinkTextColor(Color.BLUE);
    }
}
