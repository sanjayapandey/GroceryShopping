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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponAddActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
    private CouponDataSource datasource;
    private ProductDataSource productDatasource;
    private EditText etDiscount;
    private String TAG = "CouponAddActivity";
    final Map<Integer,String> checkBoxValues = new HashMap<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_add);
        etDiscount = (EditText) findViewById(R.id.discount);

        datasource = new CouponDataSource( this );
        datasource.open( );
        productDatasource = new ProductDataSource( this );
        productDatasource.open( );
        list = (ListView) findViewById(R.id.listView);
        showList();

    }

    public void save(View arg0){

        List<String> productNames = new ArrayList<String>();

        for (Map.Entry<Integer, String> pair : checkBoxValues.entrySet()) {
            System.out.println("checkbox: "+pair.getKey() +" is: "+ pair.getValue());
            productNames.add(pair.getValue());
            Log.i(TAG,pair.getValue());
        }

        Log.i(TAG,"DIscount:"+Double.valueOf(etDiscount.getText().toString()));
        datasource.createCoupon(Double.valueOf(etDiscount.getText().toString()), productNames);
        Intent i = new Intent( CouponAddActivity.this,CouponAddActivity.class );
        startActivity( i );
        Toast.makeText(getApplicationContext(),"Coupon added successfully!", Toast.LENGTH_LONG).show();
        etDiscount.setText("");

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
            Intent i = new Intent( CouponAddActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( CouponAddActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( CouponAddActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( CouponAddActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    protected void showList(){
        List<Product> products =productDatasource.getAllProducts();
        if(products.size()<1)return;
        for(int i=0;i<products.size();i++){
            HashMap<String,String> product = new HashMap<String,String>();
            product.put("Name",products.get(i).getName());
            product.put("Price",String.valueOf(products.get(i).getPrice()));
            productList.add(product);
        }

        try {
            ListAdapter adapter = new SimpleAdapter(
                    CouponAddActivity.this, productList, R.layout.coupon_product_table_view,
                    new String[]{"", "Name","Price"},
                    new int[]{R.id.checkbox, R.id.name, R.id.price}
            ){
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {

                    // get filled view from SimpleAdapter
                    View itemView=super.getView(position, convertView, parent);
                    // find our button there
                    TextView tv = (TextView) itemView.findViewById(R.id.name);
                    ProductActivity.makeTextViewHyperlink(tv);
                    final CheckBox cb = (CheckBox)itemView.findViewById(R.id.checkbox);
                    final TextView etName = (TextView) itemView.findViewById(R.id.name);
                    final TextView etPrice = (TextView) itemView.findViewById(R.id.price);
                    etPrice.setEnabled(false);

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                //System.out.println("position checked: "+position);
                                checkBoxValues.put(position,etName.getText().toString( ));
                            }
                            else
                            {
                                //System.out.println("position unchecked: "+position);
                                checkBoxValues.remove(position);
                            }
                        }
                    });
                    //REMEMBER: never set value before onclick listener. It will kill you. :)
                    if(checkBoxValues.containsKey(position)) {
                        cb.setChecked(true);
                    }else{
                        cb.setChecked(false);
                    }
                    tv.setOnClickListener( new View.OnClickListener( ) {
                        @Override
                        public void onClick( View v ) {

                            Intent intent = new Intent( CouponAddActivity.this, ProductDetailActivity.class );
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
