package com.android.recipesuggester.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.recipesuggester.R;
import com.android.recipesuggester.custom.MyToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordDialog extends Dialog {

    private EditText reset_EDT_email;
    private Button reset_BTN_reset;
    private FirebaseAuth auth;

    public ResetPasswordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resetpassword);

        auth = FirebaseAuth.getInstance();

        findViews();
        bindButtonListeners();
    }

    private void bindButtonListeners() {
        reset_BTN_reset.setOnClickListener(resetListener);
    }

    private void findViews() {
        reset_EDT_email = findViewById(R.id.reset_EDT_email);
        reset_BTN_reset = findViewById(R.id.reset_BTN_reset);
    }

    // Reset password listener
    View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = reset_EDT_email.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                reset_EDT_email.setError(getContext().getResources().getString(R.string.reset_error_wrong_email));
                return;
            }
            sendPasswordResetEmail(email);
        }
    };

    // Sending the password reset email
    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyToast.getInstance().showToast(R.string.reset_successful, getContext());
                } else {
                    MyToast.getInstance().showToast(R.string.reset_unsuccessful, getContext());
                }
            }
        });
    }
}
