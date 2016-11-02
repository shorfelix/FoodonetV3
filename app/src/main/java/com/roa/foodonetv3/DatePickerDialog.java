package com.roa.foodonetv3;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by roa on 02/11/2016.
 */

public class DatePickerDialog extends Dialog implements View.OnClickListener {

    private DatePicker picker;
    private Context context;
    private OnMyDialogResult dialogResult;

    public DatePickerDialog(Context context, OnMyDialogResult dialogResult) {
        super(context);
        this.context = context;
        this.dialogResult = dialogResult;
        setContentView(R.layout.date_picker_dialog);
        picker = (DatePicker) findViewById(R.id.datePicker);
        findViewById(R.id.chooseDateButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int year = picker.getYear();
        int month = picker.getMonth()+1;
        int day = picker.getDayOfMonth();
        String dateSt = String.format("%1$d/%2$02d/%3$02d", year,month,day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        long dateInMillis = calendar.getTimeInMillis();
        dialogResult.finish(dateInMillis, dateSt);

        this.dismiss();

    }

    public interface OnMyDialogResult{
        void finish(long endingDate, String date);
    }
}
