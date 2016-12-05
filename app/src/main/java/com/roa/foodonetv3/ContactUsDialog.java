package com.roa.foodonetv3;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by roa on 05/12/2016.
 */

public class ContactUsDialog extends Dialog implements View.OnClickListener {

    private EditText contactEditText;
    private Context context;

    public ContactUsDialog(Context context) {
        super(context);
        setContentView(R.layout.contact_us_dialog);
        this.context = context;
        contactEditText = (EditText) findViewById(R.id.contactEditText);
        findViewById(R.id.buttonCancel).setOnClickListener(this);
        findViewById(R.id.buttonSend).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCancel:

                break;

            case R.id.buttonSend:
                String message = contactEditText.getText().toString();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
        }
        this.dismiss();
    }
}
