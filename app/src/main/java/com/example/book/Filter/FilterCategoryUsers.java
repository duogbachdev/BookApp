package com.example.book.Filter;

import android.widget.Filter;

import com.example.book.Adapter.AdapterCategoryUsers;
import com.example.book.Model.book.ModelCategory;

import java.util.ArrayList;

public class FilterCategoryUsers extends Filter {

    //arraylist in which we want to search
    ArrayList<ModelCategory> filterListUser;
    //adapter
    AdapterCategoryUsers adapterCategoryUsers;

    public FilterCategoryUsers(ArrayList<ModelCategory> filterListUser, AdapterCategoryUsers adapterCategoryUsers) {
        this.filterListUser = filterListUser;
        this.adapterCategoryUsers = adapterCategoryUsers;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null and empty
        if (constraint != null && constraint.length() > 0){
            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();

            for (int i=0; i<filterListUser.size(); i++){
                //validate
                if (filterListUser.get(i).getCategory().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterListUser.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterListUser.size();
            results.values = filterListUser;
        }
        return results; //don't miss it
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter changes
        adapterCategoryUsers.categoryArrayList = (ArrayList<ModelCategory>) results.values;
        //notify change
        adapterCategoryUsers.notifyDataSetChanged();

    }
}
