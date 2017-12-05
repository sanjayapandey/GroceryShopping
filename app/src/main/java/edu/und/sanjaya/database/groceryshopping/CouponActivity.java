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
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> CouponList = new ArrayList<HashMap<String,String>>();
    private CouponDataSource datasource;
    private String TAG = "CouponActivity";
    final Map<Integer,String> checkBoxValues = new HashMap<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        datasource = new CouponDataSource( this );
        datasource.open( );
        list = (ListView) findViewById(R.id.couponListView);
        showList();
    }

    public void addCoupon(View arg0){
        Intent intent = new Intent(CouponActivity.this,CouponAddActivity.class);
        startActivity(intent);
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
            Intent i = new Intent( CouponActivity.this, ProductActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_main ) {
            Intent i = new Intent( CouponActivity.this, MainActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_coupon ) {
            Intent i = new Intent( CouponActivity.this, CouponActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_shopping){
            Intent i = new Intent( CouponActivity.this, ShoppingActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    protected void showList(){
        List<Coupon> coupons =datasource.getAllCoupon();
        if(coupons.size()<1)return;
        for(int i=0;i<coupons.size();i++){
            HashMap<String,String> coupon = new HashMap<String,String>();
            coupon.put("Id",String.valueOf(coupons.get(i).getId()));
            CouponList.add(coupon);
        }

        try {
            ListAdapter adapter = new SimpleAdapter(
                    CouponActivity.this, CouponList, R.layout.coupon_table_view,
                    new String[]{"", "Id"},
                    new int[]{R.id.radio, R.id.id}
            ){
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {

                    // get filled view from SimpleAdapter
                    View itemView=super.getView(position, convertView, parent);
                    // find our button there
                    TextView tv = (TextView) itemView.findViewById(R.id.id);
                    final String id = tv.getText().toString();
                    ProductActivity.makeTextViewHyperlink(tv);
                    final CheckBox cb = (CheckBox) itemView.findViewById(R.id.radio);
                    //final TextView tvId = (TextView) itemView.findViewById(R.id.id);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                //System.out.println("position checked: "+position);
                                checkBoxValues.put(position,id);
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

                            Intent intent = new Intent( CouponActivity.this, CouponDetailActivity.class );
                            intent.putExtra( "id", id );
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

    public void delete(View arg0){

        for (Map.Entry<Integer, String> pair : checkBoxValues.entrySet()) {
            System.out.println("checkbox: "+pair.getKey() +" is: "+ pair.getValue());
            datasource.deleteCoupon(Integer.valueOf(pair.getValue()));
            Log.i(TAG,"Deleted:"+pair.getValue());
        }
        Intent i = new Intent( CouponActivity.this, CouponActivity.class );
        startActivity( i );
        Toast.makeText(getApplicationContext(),"Deleted successfully!", Toast.LENGTH_LONG).show();
    }
}
