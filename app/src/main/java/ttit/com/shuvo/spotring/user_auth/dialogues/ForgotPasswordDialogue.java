package ttit.com.shuvo.spotring.user_auth.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ttit.com.shuvo.spotring.R;

public class ForgotPasswordDialogue extends AppCompatDialogFragment {

    ImageView close;
    AlertDialog dialog;

    TextInputLayout emailLay;
    TextInputEditText emailAddress;

    Button send;

    String mail_address = "";

    Context mContext;

    public ForgotPasswordDialogue(Context mContext) {
        this.mContext = mContext;
    }

    AppCompatActivity activity;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.forgot_password_dialogue_layout, null);
        emailLay = view.findViewById(R.id.forgot_email_password_lay);
        emailAddress = view.findViewById(R.id.forgot_email_password);
        send = view.findViewById(R.id.send_mail_button);

        close = view.findViewById(R.id.close_forgot_pass_screen);

        activity = (AppCompatActivity) view.getContext();

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        emailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    if (editable.toString().contains("@")) {
                       emailLay.setHelperText("");
                    }
                    else {
                        String nte = "Invalid Email";
                        emailLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Email";
                    emailLay.setHelperText(nte);
                }
            }
        });

        emailAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    emailAddress.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        send.setOnClickListener(v -> {
            mail_address = Objects.requireNonNull(emailAddress.getText()).toString();

            if (!mail_address.isEmpty()) {
                if (mail_address.contains("@")) {
                    emailLay.setHelperText("");
                    String mmm = "info@techterrain-it.com";
                    String bodyText =  "Hello Support Team,\n\n" +
                            "I am unable to recover my account password and need assistance to regain access.\n\n" +
                            "Account Information:\n" +
                            "--------------------\n" +
                            "User Name: \n\n" +
                            "Registered Email: " + mail_address + "\n\n" +
                            "Registered Phone (if any): \n\n"+
                            "Thank you for your support.\n";

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:"+mmm+"?subject="+"Bakorkhani Password Recovery"
                            +"&body="+Uri.encode(bodyText));
                    intent.setData(data);
                    dialog.dismiss();
                    try {
                        startActivity(intent);

                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(mContext, "There is no email app found.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    String nte = "Invalid Email";
                    emailLay.setHelperText(nte);
                }
            }
            else {
                String nte = "Please Provide Email";
                emailLay.setHelperText(nte);
            }
        });

        close.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
