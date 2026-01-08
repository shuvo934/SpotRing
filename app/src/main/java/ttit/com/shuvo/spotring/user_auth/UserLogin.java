package ttit.com.shuvo.spotring.user_auth;

import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EMAIL;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EVENT_COUNT;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PASSWORD;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PHONE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUBSCRIBE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_TF;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import ttit.com.shuvo.spotring.dashboard.HomePage;
import ttit.com.shuvo.spotring.databinding.ActivityUserLoginBinding;
import ttit.com.shuvo.spotring.user_auth.model.UserInfoList;
import ttit.com.shuvo.spotring.utilities.Constants;

public class UserLogin extends AppCompatActivity {

    String mobile = "";
    String password = "";
    SharedPreferences sharedpreferences;

    SharedPreferences sharedLogin;
    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    public static ArrayList<UserInfoList> userInfoLists = new ArrayList<>();

    private ActivityUserLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        binding.progressIndicatorLogIn.setVisibility(View.GONE);

        sharedLogin = getSharedPreferences(Constants.LOGIN_ACTIVITY_FILE,MODE_PRIVATE);

        sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES,MODE_PRIVATE);
        getUserName = sharedpreferences.getString(Constants.user_mobile,null);
        getPassword = sharedpreferences.getString(Constants.user_password,null);
        getChecked = sharedpreferences.getBoolean(Constants.checked,false);

        if (getUserName != null) {
            binding.userPhoneLogIn.setText(getUserName);
        }
        if (getPassword != null) {
            binding.userPasswordLogIn.setText(getPassword);
        }
        binding.rememberCheckbox.setChecked(getChecked);

        binding.userPasswordLogIn.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    binding.userPasswordLogIn.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        binding.moveToRegistrationTextView.setOnClickListener(view -> {
            Intent intent = new Intent(UserLogin.this, UserRegistration.class);
            startActivity(intent);
        });

        binding.logInButton.setOnClickListener(view -> {

            closeKeyBoard();

            binding.emailPassMiss.setVisibility(View.GONE);
            mobile = Objects.requireNonNull(binding.userPhoneLogIn.getText()).toString();
            password = Objects.requireNonNull(binding.userPasswordLogIn.getText()).toString();

            if (!mobile.isEmpty() && !password.isEmpty()) {

                //mTask = new CheckLogin().execute();
                getUserFromApi();

            } else {
                Toast.makeText(getApplicationContext(), "Please Give Mobile number and Password", Toast.LENGTH_SHORT).show();
            }

        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UserLogin.this)
                        .setTitle("EXIT!")
                        .setMessage("Do you want to exit?")
//                .setIcon(R.drawable.petuk_icon)
                        .setPositiveButton("Yes", (dialog, which) -> System.exit(0))
                        .setNegativeButton("No", (dialog, which) -> {
                            //Do nothing
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        closeKeyBoard();
        binding.userPhoneLogIn.clearFocus();
        binding.userPasswordLogIn.clearFocus();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event){
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    private void closeKeyBoard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getUserFromApi() {
        binding.progressIndicatorLogIn.setVisibility(View.VISIBLE);
        binding.logInDesignLayout.setVisibility(View.GONE);
        FirebaseFirestore cloudDatabase = FirebaseFirestore.getInstance();
        cloudDatabase.collection(KEY_USER_TABLE_NAME)
                .whereEqualTo(KEY_USER_PHONE, mobile)
                .whereEqualTo(KEY_USER_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String cloud_user_id = documentSnapshot.getId();
                        String p_name = documentSnapshot.getString(KEY_USER_NAME);
                        String p_phone = documentSnapshot.getString(KEY_USER_PHONE);
                        String p_email = documentSnapshot.getString(KEY_USER_EMAIL);
                        String p_password = documentSnapshot.getString(KEY_USER_PASSWORD);
                        String p_subscribed = documentSnapshot.getString(KEY_USER_SUBSCRIBE);
                        String p_event_count = documentSnapshot.getString(KEY_USER_EVENT_COUNT);
                        userInfoLists = new ArrayList<>();
                        userInfoLists.add(new UserInfoList(p_name,p_phone,p_email,p_password,p_subscribed, "",p_event_count,cloud_user_id,""));

                        if (binding.rememberCheckbox.isChecked()) {
                            System.out.println("Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(Constants.user_mobile);
                            editor.remove(Constants.user_password);
                            editor.remove(Constants.checked);

                            editor.putString(Constants.user_mobile,mobile);
                            editor.putString(Constants.user_password,password);
                            editor.putBoolean(Constants.checked,true);
                            editor.apply();
                            editor.commit();
                        }
                        else {
                            System.out.println("Not Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(Constants.user_mobile);
                            editor.remove(Constants.user_password);
                            editor.remove(Constants.checked);

                            editor.apply();
                            editor.commit();
                        }

                        SharedPreferences.Editor editor1 = sharedLogin.edit();
                        editor1.remove(KEY_USER_NAME);
                        editor1.remove(KEY_USER_PHONE);
                        editor1.remove(KEY_USER_EMAIL);
                        editor1.remove(KEY_USER_PASSWORD);
                        editor1.remove(KEY_USER_ID);
                        editor1.remove(KEY_USER_SUBSCRIBE);
                        editor1.remove(KEY_USER_EVENT_COUNT);
                        editor1.remove(LOGIN_TF);

                        editor1.putString(KEY_USER_NAME, p_name);
                        editor1.putString(KEY_USER_PHONE, p_phone);
                        editor1.putString(KEY_USER_EMAIL, p_email);
                        editor1.putString(KEY_USER_PASSWORD, p_password);
                        editor1.putString(KEY_USER_ID,cloud_user_id);
                        editor1.putString(KEY_USER_SUBSCRIBE, p_subscribed);
                        editor1.putString(KEY_USER_EVENT_COUNT, p_event_count);
                        editor1.putBoolean(LOGIN_TF,true);

                        editor1.apply();
                        editor1.commit();

                        binding.progressIndicatorLogIn.setVisibility(View.GONE);
                        binding.logInDesignLayout.setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        intent.putExtra("FROM_LOGIN",true);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        binding.progressIndicatorLogIn.setVisibility(View.GONE);
                        binding.logInDesignLayout.setVisibility(View.VISIBLE);
                        binding.emailPassMiss.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressIndicatorLogIn.setVisibility(View.GONE);
                    binding.logInDesignLayout.setVisibility(View.VISIBLE);

                    AlertDialog dialog = new AlertDialog.Builder(UserLogin.this)
                            .setMessage("Slow Internet or Please Check Your Internet Connection")
                            .setPositiveButton("Retry", null)
                            .show();

                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(v -> {

                        getUserFromApi();
                        dialog.dismiss();
                    });
                });
    }
}