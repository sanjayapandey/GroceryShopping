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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
    private ProductDataSource productDataSource;
    private CouponDataSource couponDataSource;
    private String TAG = "ShoppingActivity";
    final Map<Integer,String> checkBoxValues = new HashMap<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        productDataSource = new ProductDataSource( this );
        productDataSource.open( );
        couponDataSource = new CouponDataSource(this);
        couponDataSource.open();

        list = (ListView) findViewById(R.id.listView);
        showList();
    }

    public void purchase(View arg0){

        List<Product> purchaseList = new ArrayList<>();

        for (Map.Entry<Integer, String> pair : checkBoxValues.entrySet()) {
            System.out.println("checkbox: "+pair.getKey() +" is: "+ pair.getValue());
            Product product = productDataSource.getProduct(pair.getValue());
            purchaseList.add(product);
        }

        doPurchase(purchaseList);
        Toast.makeText(getApplicationContext(),"Purchase successfully!", Toast.LENGTH_LONG).show();
    }

    public void doPurchase(List<Product> purchaseList){

        double totalDiscount = couponDataSource.getMaximumDiscountOverPurchase(purchaseList);
        double totalAmount = 0;
        for (Product product:purchaseList){
            totalAmount+=product.getPrice();
            System.out.println(totalAmount);
        }
        System.out.println("Total discount: "+totalDiscount);
        setContentView(R.layout.activity_purchase_summary);
        TextView tvInfo = (TextView) findViewById(R.id.information);
        TextView tvTotal = (TextView) findViewById(R.id.total);
        tvInfo.setText("You saved $"+totalDiscount);
        tvTotal.setText("Total amount: $"+(totalAmount-totalDiscount));
        list = (ListView) findViewById(R.id.listView);
        showPurchasedList(purchaseList);
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
            Intent i = new Intent( ShoppingActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( ShoppingActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( ShoppingActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( ShoppingActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    protected void showList(){
        productList = new ArrayList<HashMap<String,String>>();
        List<Product> products =productDataSource.getAllProducts();
        if(products.size()<1)return;
        for(int i=0;i<products.size();i++){
            HashMap<String,String> product = new HashMap<String,String>();
            product.put("Name",products.get(i).getName());
            product.put("Price",String.valueOf(products.get(i).getPrice()));
            productList.add(product);
        }

        try {
            ListAdapter adapter = new SimpleAdapter(
                    ShoppingActivity.this, productList, R.layout.coupon_product_table_view,
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

                            Intent intent = new Intent( ShoppingActivity.this, ProductDetailActivity.class );
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

    protected void showPurchasedList( List<Product> products ){
        productList = new ArrayList<HashMap<String,String>>();
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
                    ShoppingActivity.this, productList, R.layout.shopping_table_view,
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

                            Intent intent = new Intent( ShoppingActivity.this, ProductDetailActivity.class );
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
