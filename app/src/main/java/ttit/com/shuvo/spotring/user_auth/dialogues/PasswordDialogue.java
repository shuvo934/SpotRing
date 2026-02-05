package ttit.com.shuvo.spotring.user_auth.dialogues;

import android.app.Dialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.user_auth.interfaces.SaveCallListener;

public class PasswordDialogue extends AppCompatDialogFragment {
    ImageView close;
    AlertDialog dialog;

    TextInputLayout passLay;
    TextInputLayout confirmPassLay;
    TextInputEditText pass;
    TextInputEditText confirmPass;

    Button save;

    String main_pass = "";

    Context mContext;

    public PasswordDialogue(Context mContext) {
        this.mContext = mContext;
    }

    private SaveCallListener saveCallListener;
    AppCompatActivity activity;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof SaveCallListener)
            saveCallListener = (SaveCallListener) getActivity();

        View view = inflater.inflate(R.layout.password_dailogue_layout, null);
        passLay = view.findViewById(R.id.user_password_google_login_lay);
        pass = view.findViewById(R.id.user_password_google_login);
        confirmPassLay = view.findViewById(R.id.user_confirm_password_google_login_lay);
        confirmPass = view.findViewById(R.id.user_confirm_password_google_login);
        save = view.findViewById(R.id.save_login_button);

        close = view.findViewById(R.id.close_pass_screen);

        activity = (AppCompatActivity) view.getContext();

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    passLay.setHelperText("");
                }
                else {
                    String nte = "Please Provide Password";
                    passLay.setHelperText(nte);
                }
            }
        });

        pass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = Objects.requireNonNull(pass.getText()).toString();
                String confirm = Objects.requireNonNull(confirmPass.getText()).toString();
                if (!password.isEmpty()) {
                    passLay.setHelperText("");
                    if (!confirm.isEmpty()) {
                        if (!confirm.equals(password)) {
                            String nte = "Password did not match";
                            confirmPassLay.setHelperText(nte);
                        }
                        else {
                            confirmPassLay.setHelperText("");
                        }
                    }
                }
                else {
                    String nte = "Please Provide Password";
                    passLay.setHelperText(nte);
                }
            }
        });

        pass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    pass.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    confirmPassLay.setHelperText("");
                    String password = Objects.requireNonNull(pass.getText()).toString();
                    if (!password.isEmpty()) {
                        passLay.setHelperText("");
                        if (!password.equals(editable.toString())) {
                            String nte = "Password did not match";
                            confirmPassLay.setHelperText(nte);
                        }
                    }
                    else {
                        String nte = "Please Provide Password";
                        passLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Password Again";
                    confirmPassLay.setHelperText(nte);
                }
            }
        });

        confirmPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = Objects.requireNonNull(pass.getText()).toString();
                String confirm = Objects.requireNonNull(confirmPass.getText()).toString();
                if (!password.isEmpty()) {
                    passLay.setHelperText("");
                    if (!confirm.isEmpty()) {
                        if (!confirm.equals(password)) {
                            String nte = "Password did not match";
                            confirmPassLay.setHelperText(nte);
                        }
                        else {
                            confirmPassLay.setHelperText("");
                        }
                    }
                    else {
                        String nte = "Please Provide Password Again";
                        confirmPassLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Password";
                    passLay.setHelperText(nte);
                }
            }
        });

        confirmPass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    confirmPass.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        save.setOnClickListener(v -> {
            String password = Objects.requireNonNull(pass.getText()).toString();
            String confirm_pass = Objects.requireNonNull(confirmPass.getText()).toString();

            if (!password.isEmpty()) {
               passLay.setHelperText("");
                if (!confirm_pass.isEmpty()) {
                    confirmPassLay.setHelperText("");
                    if (password.equals(confirm_pass)) {
                        main_pass = password;
                        if(saveCallListener != null)
                            saveCallListener.onSaveLogin(main_pass);

                        dialog.dismiss();
                    }
                    else {
                        String nte = "Password did not match";
                        confirmPassLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Password Again";
                    confirmPassLay.setHelperText(nte);
                }
            }
            else {
                String nte = "Please Provide Password";
               passLay.setHelperText(nte);
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
