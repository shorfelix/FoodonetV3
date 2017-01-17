package com.roa.foodonetv3.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.roa.foodonetv3.R;

public class NewGroupDialog extends Dialog implements View.OnClickListener {
    private EditText editGroupName;
    private OnNewGroupClickListener listener;

    public NewGroupDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_new_group);
        listener = (OnNewGroupClickListener) context;
        editGroupName = (EditText) findViewById(R.id.editGroupName);
        setTitle(context.getResources().getString(R.string.dialog_new_group));
        findViewById(R.id.buttonCancel).setOnClickListener(this);
        findViewById(R.id.buttonCreate).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonCancel:

                break;

            case R.id.buttonCreate:
                listener.onNewGroupClick(editGroupName.getText().toString());
                break;
        }
        this.dismiss();
    }

    public interface OnNewGroupClickListener{
        void onNewGroupClick(String groupName);
    }
}
