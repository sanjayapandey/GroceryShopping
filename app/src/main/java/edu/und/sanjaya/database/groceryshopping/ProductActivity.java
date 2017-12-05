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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
    private ProductDataSource datasource;
    private String TAG = "ProductActivity";
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        datasource = new ProductDataSource( this );
        datasource.open( );
        list = (ListView) findViewById(R.id.listView);
        showList();
    }

    public void addProduct(View arg0){
        Intent intent = new Intent(ProductActivity.this,ProductAddActivity.class);
        startActivity(intent);
    }
    public void update(View arg0){
        CheckBox cb;
        ListView mainListView = (ListView) findViewById(R.id.listView);
        for (int x = 0; x<mainListView.getChildCount();x++){
            cb = (CheckBox)mainListView.getChildAt(x).findViewById(R.id.checkbox);
            if(cb.isChecked()){
                TextView etName = (TextView) mainListView.getChildAt(x).findViewById(R.id.name);
                EditText etPrice = (EditText)mainListView.getChildAt(x).findViewById(R.id.price);
                datasource.updateProduct(etName.getText().toString(),Double.valueOf(etPrice.getText().toString()));
                Log.i(TAG,"Updated:"+etPrice.getText().toString());
            }
        }
        Intent i = new Intent( ProductActivity.this, ProductActivity.class );
        startActivity( i );
        Toast.makeText(getApplicationContext(),"Purchase successfully!", Toast.LENGTH_LONG).show();
    }
    public void onRadioButtonClicked(View arg0){
        TableRow tablerow = (TableRow)arg0.getParent();
        EditText price = (EditText) tablerow.getChildAt(2);
        price.setEnabled(true);
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
            Intent i = new Intent( ProductActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( ProductActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( ProductActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( ProductActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    protected void showList(){
        List<Product> products =datasource.getAllProducts();
        if(products.size()<1)return;
        for(int i=0;i<products.size();i++){
            HashMap<String,String> product = new HashMap<String,String>();
            product.put("Name",products.get(i).getName());
            product.put("Price",String.valueOf(products.get(i).getPrice()));
            productList.add(product);
        }

        try {
             ListAdapter adapter = new SimpleAdapter(
                    ProductActivity.this, productList, R.layout.table_view,
                    new String[]{"", "Name","Price"},
                    new int[]{R.id.checkbox, R.id.name, R.id.price}
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

                    /*
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                //System.out.println("position checked: "+position);
                                Product product = new Product(etName.getText().toString( ),
                                        Double.valueOf(etPrice.getText().toString( )));
                                checkBoxValues.put(position,product);
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
                        //etPrice.setText(String.valueOf(checkBoxValues.get(position).getPrice()));
                        etPrice.setEnabled(true);
                    }else{
                        cb.setChecked(false);
                        etPrice.setEnabled(false);
                    }
                    */

                    tv.setOnClickListener( new View.OnClickListener( ) {
                        @Override
                        public void onClick( View v ) {

                            Intent intent = new Intent( ProductActivity.this, ProductDetailActivity.class );
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
