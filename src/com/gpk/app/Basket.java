package com.gpk.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.gpk.app.model.MenuListModel;
import com.gpk.app.model.SelectedMenuListModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Basket extends Fragment {
	
	private ListView lv_selected_itemsList;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GpkApp.BasketonCreate =true;
		
		View rootView = inflater.inflate(R.layout.basket, container, false);
		
		
		
		lv_selected_itemsList = (ListView)rootView.findViewById(R.id.lv_selected_list);
		if(GpkApp.selected_menuList.size() > 0){
			lv_selected_itemsList.setAdapter(new MenuListAdapter(getActivity(), android.R.layout.simple_list_item_1, GpkApp.selected_menuList));
		}else {
			Toast.makeText(getActivity(), "your kart is empty...", Toast.LENGTH_SHORT).show();
		}
		
		
		//Toast.makeText(getActivity(), GpkApp.selected_menuList.get(0).getMenuName().toString(), Toast.LENGTH_SHORT).show();
		
		for(int i=0; i<GpkApp.selected_menuList.size();i++){
			Log.e("check list in basket", GpkApp.selected_menuList.get(i).getMenuName()
										+" "+GpkApp.selected_menuList.get(i).getMenuId().toString());
		}
		
		/*// Get a set of the entries
        Set<Entry<String, ArrayList<SelectedMenuListModel>>> setMap = GpkApp.selectedHashMap.entrySet();
        // Get an iterator
        Iterator<Entry<String, ArrayList<SelectedMenuListModel>>> iteratorMap = setMap.iterator();
        while(iteratorMap.hasNext()) {
	             Map.Entry<String, ArrayList<SelectedMenuListModel>> entry =
	                     (Map.Entry<String, ArrayList<SelectedMenuListModel>>) iteratorMap.next();
	             String key = entry.getKey();
	             for(int k=0;k<GpkApp.selectedHashMap.size();k++){
	            	 String values = entry.getValue().get(k).getMenuName().toString();
	            	 Log.e("check Iterator in absket", "Key = '" + key+" values: "+values);
	             }
	            
	            
	             Log.e("check Iterator in absket",
	            		 "Key = '" + key);
        }*/
	             
		return rootView;
	}
	
	
	/**
	 * MenuListAdapter populates the items list with name and price
	 * @author Ankur
	 *
	 */
	private class MenuListAdapter extends ArrayAdapter<SelectedMenuListModel>{

		Context context;
		ArrayList<SelectedMenuListModel> list_model = new ArrayList<SelectedMenuListModel>();
		SelectedMenuListModel rm;
		private ViewHolder viewHolder;
		public MenuListAdapter(Context context, 
				int textViewResourceId, ArrayList<SelectedMenuListModel> menuListparse) {
			super(context, textViewResourceId, menuListparse);
			this.context = context;
			this.list_model = (ArrayList<SelectedMenuListModel>)menuListparse;
		}
		
		
		@SuppressLint("ViewHolder")
		@Override
		  public View getView(final int position, View convertView, ViewGroup parent) {
			rm = list_model.get(position);
			
			 
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    View rowView = inflater.inflate(R.layout.basket_row, parent, false);

		    viewHolder = new ViewHolder();
		    viewHolder.position = position;
		    
		    viewHolder.ed_quantity = (EditText)rowView.findViewById(R.id.editText1);
		    viewHolder.tv_menuName = (TextView)rowView.findViewById(R.id.textView1);
		    viewHolder.tv_menuActualPrice = (TextView)rowView.findViewById(R.id.tv_actual_price);
		    viewHolder.tv_quantityPrice = (TextView)rowView.findViewById(R.id.tv_quanity_price);
			
		    viewHolder.ed_quantity.setText(Integer.toString(rm.getQuantity()));
		    viewHolder.tv_menuName.setText(rm.getMenuName());
		    viewHolder.tv_menuActualPrice.setText("Rs. "+Integer.toString(rm.getPrice()));
		    
		    int totalPrice = rm.getPrice() * rm.getQuantity();
		    viewHolder.tv_quantityPrice.setText(Integer.toString(totalPrice));
		  
		    return rowView;
		  }
	}
	
	static class ViewHolder {
		EditText ed_quantity;
		TextView tv_menuName;
		TextView tv_menuActualPrice;
		TextView tv_quantityPrice;
	    int position;
	}
	
}
	
