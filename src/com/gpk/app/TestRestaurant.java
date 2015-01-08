package com.gpk.app;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.gpk.app.model.RestaurantModel;
import com.loopj.android.image.SmartImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TestRestaurant extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	private ListView lv_restraunt;
	private ProgressDialog progressbar;
	private LocationClient mLocationClient = null;
	private  ArrayList<String> restauraunt_name_list;
	private  ArrayList<String> restauraunt_image_list;
	private  ArrayList<Double> restauraunt_latitude;
	private  ArrayList<Double> restauraunt_longitude;
	private  ArrayList<Integer> restaurant_distance;
	private ArrayList<String> restaurant_objectId;
	private ArrayList<RestaurantModel> res_model_list;
	RestaurantModel restModel;
	RestaurantDetails objres;
	private String res_address;
	
	List<ParseObject> ob;
	//private SmartImageView image;
	private String url;
	Location mlocation = null;
	private static final int DELIVERY_CHARGES_UNIT = 10;
	private static final int DELIVERY_TIME_UNIT = 10;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaraunt);
		
		
		ActionBar bar = getActionBar();
		//bar.setIcon(R.drawable.gpk_logo);
		
		//ActionBar actionBar = Restaurant.this.getActionBar();
		//actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));	
		
		/*ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#81a3d0"));     
        ab.setBackgroundDrawable(colorDrawable);*/
		
		 ActionBar actionBar = getActionBar();
	        if (actionBar == null) {
	        	Log.e("AAAAA", "AAA");
	        	//getSupportActionBar().setTitle("GPK");
	           // actionBar.setDisplayHomeAsUpEnabled(true);
	           /* getSupportActionBar().setDisplayShowHomeEnabled(true);
	            getSupportActionBar().setDisplayUseLogoEnabled(true);
	            getSupportActionBar().setLogo(R.drawable.testlogo);*/
	           
	        }
        
		
		
		// Create the LocationRequest object
	    mLocationClient = new LocationClient(this, this, this);
	    
	    res_model_list = new ArrayList<RestaurantModel>();
	    
	    lv_restraunt = (ListView)findViewById(R.id.lv_restraunt);
	    
	    lv_restraunt.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
				String objectId = res_model_list.get(position).getObjectId();
				String res_name = res_model_list.get(position).getRestaurant_name();
			    String res_image = res_model_list.get(position).getImage();
			    //new RestaurantDetails(res_name, objectId, res_image);
				Intent intent = new Intent(TestRestaurant.this, BaseActivity.class);
				GpkApp.objectId = objectId;
				GpkApp.resName = res_name;
				GpkApp.res_image = res_image;
				GpkApp.resAddress = res_address;

				
				//GpkApp.isfrombackpress = false;
				startActivity(intent);
				
				//finish();
				
				//Toast.makeText(getBaseContext(), objectId  +position, Toast.LENGTH_SHORT).show();
				
			}
		});
		//image = (SmartImageView)findViewById(R.id.imageView1);
		
		if(mLocationClient != null && mLocationClient.isConnected()){
			   // Get the current location's latitude & longitude
			       mlocation = mLocationClient.getLastLocation();
			}else {
				mLocationClient.connect();
			}
		
		 new DataFromParseTask().execute();
	}
	

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	
	/**
     * On selecting action bar icons
     * */
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
            return true;
      
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 */
	
	
	private class DataFromParseTask extends AsyncTask<Void, Void, Void>{

		ProgressDialog mProgressDialog = new ProgressDialog(TestRestaurant.this);
		@Override
		protected void onPreExecute() {
			
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
			 restauraunt_name_list = new ArrayList<String>();
			 //restauraunt_address_list = new ArrayList<String>();
			 restauraunt_image_list =  new ArrayList<String>();
			 restauraunt_longitude = new ArrayList<Double>();
			 restauraunt_latitude = new ArrayList<Double>();
			 restaurant_distance = new ArrayList<Integer>();
			 restaurant_objectId = new ArrayList<String>();
			 
			 
			 
			super.onPreExecute();
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			
			 /*restauraunt_name_list.clear();
			 restaurant_distance.clear();
			 restauraunt_address_list.clear();
			 restauraunt_latitude.clear();
			 restauraunt_longitude.clear();
			 restauraunt_image_list.clear();*/
			 
			 /*
			  * For Restaurant Class
			  */
			 ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
						GpkApp.CLASS_RESTAURENTS);
				query.orderByDescending("_created_at");
				try {
					
					ob = query.find();
					// Retrieve object "name" from Parse.com database
					for (ParseObject country : ob) {
						restModel = new RestaurantModel();
						restauraunt_name_list.add((String) country.get("name"));
						restModel.setRestaurant_name((String) country.get("name"));
						
						//Object Id
						restaurant_objectId.add((String) country.getObjectId());
						restModel.setObjectId((String) country.getObjectId());
						
						//Image
						ParseFile image = (ParseFile) country.get("image");
						url = image.getUrl();
						restauraunt_image_list.add(url);
						restModel.setImage(url);
						
						//GeoPoints
						ParseGeoPoint location = (ParseGeoPoint)country.getParseGeoPoint("location");
						restauraunt_longitude.add(location.getLongitude());
						restauraunt_latitude.add(location.getLatitude());
						
						//Address
						res_address = (String) country.get("address");
						
						long Resultant_distance = getDistanceMeters(GpkApp.user_latitude, GpkApp.user_longitude, location.getLongitude(), location.getLatitude());
			        	int resultantditance = safeLongToInt(Resultant_distance);
			        	restaurant_distance.add(resultantditance);
			        	restModel.setDistance(resultantditance);
			        	
			        	res_model_list.add(restModel);
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			Log.e("test", restaurant_objectId.get(0));
			Log.e("test", restaurant_objectId.get(1));
			lv_restraunt.setAdapter(new RestaurantListAdapter(TestRestaurant.this, android.R.layout.simple_list_item_1, res_model_list));
			
		}
		
	}
	
	/**
	 * Calculating the distance 
	 */
	private static double toRadians(double x)
    {
	 double PIx = 3.141592653589793;
        return x * PIx / 180;
    }

	private static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {

    double l1 = toRadians(lat1);
    double l2 = toRadians(lat2);
    double g1 = toRadians(lng1);
    double g2 = toRadians(lng2);

    double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
    if(dist < 0) {
        dist = dist + Math.PI;
    }

    return Math.round(dist * 6378100)/1000;
}
	
	/**
	 * Converting long to integer 
	 * @param long
	 * @return integer 
	 */
	private static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	
	private class RestaurantListAdapter extends ArrayAdapter<RestaurantModel>{

		Context context;
		ArrayList<RestaurantModel> list_model = new ArrayList<RestaurantModel>();
		RestaurantModel rm;
		public RestaurantListAdapter(Context context, 
				int textViewResourceId, List<RestaurantModel> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.list_model = (ArrayList<RestaurantModel>)objects;
						
		}
		
		
		@SuppressLint("ViewHolder")
		@Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			rm = list_model.get(position);
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.restaurant_list_row, parent, false);
		    SmartImageView imageView = (SmartImageView) rowView.findViewById(R.id.resImage);
		    TextView tv_res_name = (TextView)rowView.findViewById(R.id.res_name);
		    TextView tv_res_order = (TextView)rowView.findViewById(R.id.resMinOrder);
		    TextView tv_delivery = (TextView)rowView.findViewById(R.id.res_delivery);
		    
		    tv_res_name.setText(rm.getRestaurant_name());
		    imageView.setImageUrl(rm.getImage());
		    
		    Log.e("distance check", ""+rm.getDistance()); 
		    int test = rm.getDistance()%1000;
		    
		 // if(rm.getDistance()%1000 <= 5){
			  if(test <= 5){
			  tv_res_order.setText("Min Order Rs.300");
		  }else {
			  tv_res_order.setText("Min Order Rs.500");
		}
		 // tv_delivery.setText(ShowDeliveryCharges(rm.getDistance()));
		  
		  tv_delivery.setText(ShowDeliveryTime(rm.getDistance()));
		    return rowView;
		  }
	}
	
	/*
	 * 
	 */
private String ShowDeliveryCharges(int distanceee){
	
	String testDistance = String.valueOf(distanceee);
	float distance = Float.parseFloat(testDistance);
	
		String charges = null;
		float NewDistance = (distance/1000);
		if(NewDistance<5){
			charges = ""+"0 Rs.";
		}else{
		float dis = NewDistance-5;
		float charge = (dis)*DELIVERY_CHARGES_UNIT;
			charges = "Dilevery Rs. "+String.format("%.0f", charge)+" Rs.";
		}
		return charges;
	}
	

private String ShowDeliveryTime(float distance){
	
	String time = null;
	float NewDistance = (distance/1000);
	if(NewDistance <5){
		time = ""+"30 Minutes";
	}else{
	float dis = NewDistance-5;
	float deliveryTimeInMinits = ((dis)*DELIVERY_TIME_UNIT)+30;
	int timeRoundOf =  Integer.parseInt(String.format("%.0f", deliveryTimeInMinits));
	if(timeRoundOf>=60){
			String Minite,hours;
			float getValue = timeRoundOf/60;
				if(getValue<10){
					hours  = "0"+String.format("%.0f", getValue) ;
				} else{
				hours  =String.format("%.0f", getValue) ;
				}
				float getMinite = timeRoundOf%60;
				if(getMinite<10){
				Minite = "0"+String.format("%.0f", getMinite);
				}else{
				Minite = ""+String.format("%.0f", getMinite);
				}
				time = "Delivery Time:"+" "+hours+" Hour "+Minite;
	}else{
		//time = "Delivery Time:"+" "+"00:"+timeRoundOf +"Minutes";
		time = "Delivery Time:"+" "+timeRoundOf +" Minutes";
		 }
		
	}
	return time;
}


	

	   
	@Override
	public void onStart() {
		super.onStart();
		 // Connect the client.
		Log.e("aaa", "on start");
		
		mLocationClient.connect();
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		// Disconnect the client.
				mLocationClient.disconnect();
				Log.e("aaa", "on stop");
				super.onStop();
	}
	

	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@SuppressLint("NewApi")
	@Override
	public void onConnected(Bundle arg0) {
		 Log.e("here",  "Connected");
		 
			
				progressbar = new ProgressDialog(this);
				progressbar.setMessage("Loading...");
				progressbar.show();
			
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD  && Geocoder.isPresent())
			      {
					try{
							if(mLocationClient != null && mLocationClient.isConnected()){
							   // Get the current location's latitude & longitude
							       mlocation = mLocationClient.getLastLocation();
							}else {
								mLocationClient.connect();
							}
							
							       GpkApp.user_latitude = mlocation.getLatitude();
							       GpkApp.user_longitude = mlocation.getLongitude();
							     
									
							       Log.e("AAAAAA", GpkApp.user_latitude +"  "+GpkApp.user_longitude);
							      
				      
					}catch (IllegalStateException ex) {
						ex.printStackTrace();
				        // This will catch the exception, handle as needed
				    }
			      }else {
					Toast.makeText(this,
					"Unable to fetch your Address Your Device doesn't supports Geocoder", Toast.LENGTH_SHORT).show();
			      }
				 if(progressbar.isShowing()){
					 progressbar.dismiss();
				}
				 /*fetchUserSportsfromParse();
				setimage();*/
				
				/*if(restauraunt_name_list!= null || restaurant_distance!= null ||restauraunt_address_list!= null || restauraunt_latitude!= null
			|| restauraunt_longitude!= null	|| restauraunt_image_list!= null){
					
					 restauraunt_name_list.clear();
					 restaurant_distance.clear();
					 restauraunt_address_list.clear();
					 restauraunt_latitude.clear();
					 restauraunt_longitude.clear();
					 restauraunt_image_list.clear();
				}
				 */
				// new DataFromParseTask().execute();
	}

	@Override
	public void onDisconnected() {
	}
}
