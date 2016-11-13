package com.roa.foodonetv3;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialog extends Dialog implements View.OnClickListener {

    private DatePicker picker;
    private EndDateDialogListener listener;

    public DatePickerDialog(Context context) {
        super(context);
        listener = (EndDateDialogListener) context;
        setContentView(R.layout.date_picker_dialog);
        picker = (DatePicker) findViewById(R.id.datePicker);
        findViewById(R.id.chooseDateButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int year = picker.getYear();
        int month = picker.getMonth()+1;
        int day = picker.getDayOfMonth();
        String dateSt = String.format(Locale.US,"%1$d/%2$02d/%3$02d", year,month,day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day,23,59,59);
        long dateInMillis = calendar.getTimeInMillis();
        listener.onEndDatePicked(dateInMillis, dateSt);

        this.dismiss();

    }

    public interface EndDateDialogListener {
        void onEndDatePicked(long endingDate, String date);
    }
}
