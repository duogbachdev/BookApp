package com.example.book.Filter;

import android.widget.Filter;

import com.example.book.Adapter.AdapterAdminReport;
import com.example.book.Model.report.ModelReport;

import java.util.ArrayList;

public class FilterAdminReport extends Filter {
        //array list
        ArrayList<ModelReport> filterAdminList;
        //adapter
        AdapterAdminReport adapterAdminReport;

        public FilterAdminReport(ArrayList<ModelReport> filterAdminList, AdapterAdminReport adapterAdminReport) {
            this.filterAdminList = filterAdminList;
            this.adapterAdminReport = adapterAdminReport;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //value should not be null and empty
            if (constraint != null && constraint.length() > 0){
                //change to upper case, or lower case to avoid case sensitivity
                constraint = constraint.toString().toUpperCase();
                ArrayList<ModelReport> filteredModels = new ArrayList<>();

                for (int i=0; i<filterAdminList.size(); i++){
                    //validate
                    if (filterAdminList.get(i).getReason().toUpperCase().contains(constraint)){
                        //add to filtered list
                        filteredModels.add(filterAdminList.get(i));
                    }
                }

                results.count = filteredModels.size();
                results.values = filteredModels;
            }
            else {
                results.count = filterAdminList.size();
                results.values = filterAdminList;
            }
            return results; //don't miss it
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //apply filter changes
            adapterAdminReport.reportArrayList = (ArrayList<ModelReport>) results.values;
            //notify change
            adapterAdminReport.notifyDataSetChanged();

        }


}
