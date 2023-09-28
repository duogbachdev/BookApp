package com.example.book.Filter;

import android.widget.Filter;

import com.example.book.Adapter.AdapterReport;
import com.example.book.Model.report.ModelReport;

import java.util.ArrayList;

public class FilterReport extends Filter {
        //array list
        ArrayList<ModelReport> filterList;
        //adapter
        AdapterReport adapterReport;

        public FilterReport(ArrayList<ModelReport> filterList, AdapterReport adapterReport) {
            this.filterList = filterList;
            this.adapterReport = adapterReport;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //value should not be null and empty
            if (constraint != null && constraint.length() > 0){
                //change to upper case, or lower case to avoid case sensitivity
                constraint = constraint.toString().toUpperCase();
                ArrayList<ModelReport> filteredModels = new ArrayList<>();

                for (int i=0; i<filterList.size(); i++){
                    //validate
                    if (filterList.get(i).getReason().toUpperCase().contains(constraint)){
                        //add to filtered list
                        filteredModels.add(filterList.get(i));
                    }
                }

                results.count = filteredModels.size();
                results.values = filteredModels;
            }
            else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results; //don't miss it
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //apply filter changes
            adapterReport.reportArrayList = (ArrayList<ModelReport>) results.values;
            //notify change
            adapterReport.notifyDataSetChanged();

        }


}
