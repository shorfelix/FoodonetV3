package com.roa.foodonetv3.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.Feedback;
import com.roa.foodonetv3.services.FoodonetService;

public class ContactUsDialog extends Dialog implements View.OnClickListener {

    private EditText contactEditText;
    private Context context;

    public ContactUsDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_contact_us);
        this.context = context;
        contactEditText = (EditText) findViewById(R.id.contactEditText);
        setTitle(context.getResources().getString(R.string.feedback_feedback));
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
                Feedback feedback = new Feedback(CommonMethods.getDeviceUUID(context), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),message);
                Intent intent = new Intent(context, FoodonetService.class);
                intent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_POST_FEEDBACK);
                intent.putExtra(ReceiverConstants.JSON_TO_SEND,feedback.getFeedbackJson().toString());
                context.startService(intent);
                break;
        }
        this.dismiss();
    }
}
