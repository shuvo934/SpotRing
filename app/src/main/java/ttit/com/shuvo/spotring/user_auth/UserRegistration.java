package ttit.com.shuvo.spotring.user_auth;

import static ttit.com.shuvo.spotring.utilities.Constants.*;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import ttit.com.shuvo.spotring.databinding.ActivityUserRegistrationBinding;

public class UserRegistration extends AppCompatActivity {

    private ActivityUserRegistrationBinding binding;

    String name = "";
    String email = "";
    String phone = "";
    String pass = "";
    String confirm_pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.registrationCardView.setVisibility(View.VISIBLE);
        binding.progressIndicatorRegistration.setVisibility(View.GONE);

        binding.userNameRegistration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    binding.userNameRegistrationLay.setHelperText("");
                }
                else {
                    String nte = "Please Provide Name";
                    binding.userNameRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userNameRegistration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userNameRegistration.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        binding.userEmailRegistration.addTextChangedListener(new TextWatcher() {
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
                        binding.userEmailRegistrationLay.setHelperText("");
                    }
                    else {
                        String nte = "Invalid Email";
                        binding.userEmailRegistrationLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Email";
                    binding.userEmailRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userEmailRegistration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userEmailRegistration.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        binding.userPhoneRegistration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userPhoneRegistration.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        binding.userPasswordRegistration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    binding.userPasswordRegistrationLay.setHelperText("");
                }
                else {
                    String nte = "Please Provide Password";
                    binding.userPasswordRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userPasswordRegistration.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pass = Objects.requireNonNull(binding.userPasswordRegistration.getText()).toString();
                String confirm = Objects.requireNonNull(binding.userConfirmPasswordRegistration.getText()).toString();
                if (!pass.isEmpty()) {
                    binding.userPasswordRegistrationLay.setHelperText("");
                    if (!confirm.isEmpty()) {
                        if (!confirm.equals(pass)) {
                            String nte = "Password did not match";
                            binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                        }
                        else {
                            binding.userConfirmPasswordRegistrationLay.setHelperText("");
                        }
                    }
                }
                else {
                    String nte = "Please Provide Password";
                    binding.userPasswordRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userPasswordRegistration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userPasswordRegistration.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        binding.userConfirmPasswordRegistration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    binding.userConfirmPasswordRegistrationLay.setHelperText("");
                    String pass = Objects.requireNonNull(binding.userPasswordRegistration.getText()).toString();
                    if (!pass.isEmpty()) {
                        binding.userPasswordRegistrationLay.setHelperText("");
                        if (!pass.equals(editable.toString())) {
                            String nte = "Password did not match";
                            binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                        }
                    }
                    else {
                        String nte = "Please Provide Password";
                        binding.userPasswordRegistrationLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Password Again";
                    binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userConfirmPasswordRegistration.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pass = Objects.requireNonNull(binding.userPasswordRegistration.getText()).toString();
                String confirm = Objects.requireNonNull(binding.userConfirmPasswordRegistration.getText()).toString();
                if (!pass.isEmpty()) {
                    binding.userPasswordRegistrationLay.setHelperText("");
                    if (!confirm.isEmpty()) {
                        if (!confirm.equals(pass)) {
                            String nte = "Password did not match";
                            binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                        }
                        else {
                            binding.userConfirmPasswordRegistrationLay.setHelperText("");
                        }
                    }
                    else {
                        String nte = "Please Provide Password Again";
                        binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Password";
                    binding.userPasswordRegistrationLay.setHelperText(nte);
                }
            }
        });

        binding.userConfirmPasswordRegistration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userConfirmPasswordRegistration.clearFocus();
                    closeKeyBoard();
                    return false; // consume.
                }
            }
            return false;
        });

        binding.registrationButton.setOnClickListener(v -> {
            name = Objects.requireNonNull(binding.userNameRegistration.getText()).toString();
            email = Objects.requireNonNull(binding.userEmailRegistration.getText()).toString();
            phone = Objects.requireNonNull(binding.userPhoneRegistration.getText()).toString();
            pass = Objects.requireNonNull(binding.userPasswordRegistration.getText()).toString();
            confirm_pass = Objects.requireNonNull(binding.userConfirmPasswordRegistration.getText()).toString();

            if (!name.isEmpty()) {
                binding.userNameRegistrationLay.setHelperText("");
                if (!email.isEmpty()) {
                    if (email.contains("@")) {
                        binding.userEmailRegistrationLay.setHelperText("");
                        if (!pass.isEmpty()) {
                            binding.userPasswordRegistrationLay.setHelperText("");
                            if (!confirm_pass.isEmpty()) {
                                binding.userConfirmPasswordRegistrationLay.setHelperText("");
                                if (pass.equals(confirm_pass)) {
                                    checkValidUser();
                                }
                                else {
                                    String nte = "Password did not match";
                                    binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                                }
                            }
                            else {
                                String nte = "Please Provide Password Again";
                                binding.userConfirmPasswordRegistrationLay.setHelperText(nte);
                            }
                        }
                        else {
                            String nte = "Please Provide Password";
                            binding.userPasswordRegistrationLay.setHelperText(nte);
                        }
                    }
                    else {
                        String nte = "Invalid Email";
                        binding.userEmailRegistrationLay.setHelperText(nte);
                    }
                }
                else {
                    String nte = "Please Provide Email";
                    binding.userEmailRegistrationLay.setHelperText(nte);
                }
            }
            else {
                String nte = "Please Provide Name";
                binding.userNameRegistrationLay.setHelperText(nte);
            }
        });

        binding.moveToLoginTextView.setOnClickListener(v -> finish());
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void checkValidUser() {
        binding.registrationCardView.setVisibility(View.GONE);
        binding.progressIndicatorRegistration.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_USER_TABLE_NAME)
                .whereEqualTo(KEY_USER_EMAIL,email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {

                        binding.registrationCardView.setVisibility(View.VISIBLE);
                        binding.progressIndicatorRegistration.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), email+" is already registered.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        registerUser();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.registrationCardView.setVisibility(View.VISIBLE);
                    binding.progressIndicatorRegistration.setVisibility(View.GONE);
                    Toast.makeText(UserRegistration.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                });
    }
    public void registerUser() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String , Object> user = new HashMap<>();

//        HashMap<String, Object> locs = new HashMap<>();

        user.put(KEY_USER_NAME, name);
        user.put(KEY_USER_EMAIL, email);
        user.put(KEY_USER_PHONE, phone != null ? phone : "");
        user.put(KEY_USER_PASSWORD, pass);
        user.put(KEY_USER_SUBSCRIBE, "No");
        user.put(KEY_USER_EVENT_COUNT, "5");

//        locs.put("Latitude",23.792535);
//        locs.put("Longitude",90.404198);
//        locs.put(KEY_USER_PHONE, phone);

        database.collection(KEY_USER_TABLE_NAME)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(UserRegistration.this, "Registration success. Please Login to continue.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.registrationCardView.setVisibility(View.VISIBLE);
                    binding.progressIndicatorRegistration.setVisibility(View.GONE);
                    Toast.makeText(UserRegistration.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                });

//        database.collection("geoFences")
//                .add(locs)
//                .addOnSuccessListener(reference -> {
//                    Toast.makeText(UserRegistration.this, "Registration success. Please Sign In to continue.", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    binding.registrationCardView.setVisibility(View.VISIBLE);
//                    binding.progressIndicatorRegistration.setVisibility(View.GONE);
//                    Toast.makeText(UserRegistration.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
//                });

    }
}