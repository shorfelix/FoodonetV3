package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.ReportFromServer;

import java.util.ArrayList;
import java.util.Locale;

public class ReportsRecyclerAdapter extends RecyclerView.Adapter<ReportsRecyclerAdapter.ReportsHolder> {
    private static final String TAG = "ReportsRecyclerAdapter";
    private Context context;
    private ArrayList<ReportFromServer> reports;

    public ReportsRecyclerAdapter(Context context) {
        this.context = context;
        reports = new ArrayList<>();
    }

    public void updateReports(ArrayList<ReportFromServer> reports){
        this.reports = reports;
        notifyDataSetChanged();
    }

    @Override
    public ReportsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ReportsHolder(inflater.inflate(R.layout.report_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ReportsHolder holder, int position) {
        holder.bindReport(reports.get(position));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class ReportsHolder extends RecyclerView.ViewHolder{
        private TextView textReport;

        public ReportsHolder(View itemView) {
            super(itemView);
            textReport = (TextView) itemView.findViewById(R.id.textReport);
        }

        public void bindReport(ReportFromServer report){
            textReport.setText(String.format(Locale.US,"%1$s - (%2$s %3$s)",
                    CommonMethods.getReportStringFromType(context,report.getReportType()),
                    CommonMethods.getTimeDifference(context,Double.parseDouble(report.getDateOfReport()),CommonMethods.getCurrentTimeSeconds()),
                    context.getResources().getString(R.string.ago)));

        }
    }
}
