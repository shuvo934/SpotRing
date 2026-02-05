package ttit.com.shuvo.spotring.user_auth;

import static android.content.ContentValues.TAG;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EMAIL;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EVENT_COUNT;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PASSWORD;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PHONE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUBSCRIBE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_TF;
import static ttit.com.shuvo.spotring.utilities.Constants.user_email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.dashboard.HomePage;
import ttit.com.shuvo.spotring.databinding.ActivityUserLoginBinding;
import ttit.com.shuvo.spotring.user_auth.dialogues.ForgotPasswordDialogue;
import ttit.com.shuvo.spotring.user_auth.dialogues.PasswordDialogue;
import ttit.com.shuvo.spotring.user_auth.interfaces.SaveCallListener;
import ttit.com.shuvo.spotring.user_auth.model.GoogleUserData;
import ttit.com.shuvo.spotring.user_auth.model.UserInfoList;
import ttit.com.shuvo.spotring.utilities.Constants;

public class UserLogin extends AppCompatActivity implements SaveCallListener {

    String u_email = "";
    String password = "";
    SharedPreferences sharedpreferences;
    private CredentialManager credentialManager;
    private Executor mainExecutor;

    SharedPreferences sharedLogin;
    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    public static ArrayList<UserInfoList> userInfoLists = new ArrayList<>();
    GoogleUserData userData;
    String passwordForSave = "";

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
        getUserName = sharedpreferences.getString(Constants.user_email,null);
        getPassword = sharedpreferences.getString(Constants.user_password,null);
        getChecked = sharedpreferences.getBoolean(Constants.checked,false);

        credentialManager = CredentialManager.create(this);
        mainExecutor = ContextCompat.getMainExecutor(this);

        if (getUserName != null) {
            binding.userEmailLogIn.setText(getUserName);
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

        binding.moveToRegistrationButton.setOnClickListener(view -> {
            binding.emailPassMiss.setVisibility(View.GONE);
            Intent intent = new Intent(UserLogin.this, UserRegistration.class);
            startActivity(intent);
        });

        binding.logInButton.setOnClickListener(view -> {

            closeKeyBoard();

            binding.emailPassMiss.setVisibility(View.GONE);
            u_email = Objects.requireNonNull(binding.userEmailLogIn.getText()).toString();
            password = Objects.requireNonNull(binding.userPasswordLogIn.getText()).toString();

            if (!u_email.isEmpty() && !password.isEmpty()) {

                //mTask = new CheckLogin().execute();
                getUserFromApi();

            } else {
                Toast.makeText(getApplicationContext(), "Please Give Mobile number and Password", Toast.LENGTH_SHORT).show();
            }

        });

        binding.signUpUsingGoogle.setOnClickListener(v1 -> {
            binding.emailPassMiss.setVisibility(View.GONE);
            String webClientId = getString(R.string.default_web_client_id);
            // Try authorized accounts first
//            requestGoogleIdToken(true, webClientId, new TokenCallback() {
//                @Override public void onSuccess(@NonNull GoogleUserData data) {
//                    showData(data);
//                }
//
//                @Override public void onNoCredential() {
//                    // fallback: allow all accounts on device
//
//                }
//
//                @Override public void onError(@NonNull Exception e) {
//                    toast("Google sign-in failed: " + e.getMessage());
//                    Log.e(TAG, "error", e);
//                }
//            });

            binding.progressIndicatorLogIn.setVisibility(View.VISIBLE);
            binding.logInDesignLayout.setVisibility(View.GONE);
            // fallback: allow all accounts on device
            requestGoogleIdToken(webClientId, new TokenCallback() {
                @Override public void onSuccess(@NonNull GoogleUserData data) {
                    String name =  data.getName();
                    String mail = data.getEmailOrId();
                    String photoUrl = data.getPhotoUrl();
                    String idToken =  data.getIdToken();

                    userData = new GoogleUserData(name,mail,photoUrl,idToken);

                    checkValidUser();
                }

                @Override public void onNoCredential() {
                    toast("No Google account available on this device.");
                    binding.progressIndicatorLogIn.setVisibility(View.GONE);
                    binding.logInDesignLayout.setVisibility(View.VISIBLE);
                }

                @Override public void onError(@NonNull Exception e) {
                    toast("Google sign-up failed: " + e.getMessage());
                    binding.progressIndicatorLogIn.setVisibility(View.GONE);
                    binding.logInDesignLayout.setVisibility(View.VISIBLE);
                }
            });
        });

        binding.forgotPasswordText.setOnClickListener(v1 -> {
            ForgotPasswordDialogue forgotPasswordDialogue = new ForgotPasswordDialogue(UserLogin.this);
            forgotPasswordDialogue.show(getSupportFragmentManager(),"forgot_password");
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UserLogin.this)
                        .setTitle("EXIT!")
                        .setMessage("Do you want to exit?")
//                .setIcon(R.drawable.petuk_icon)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> {
                            //Do nothing
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        closeKeyBoard();
        binding.userEmailLogIn.clearFocus();
        binding.userPasswordLogIn.clearFocus();
    }

    @Override
    public void onSaveLogin(String password) {
        if (userData != null) {
            passwordForSave = password;
            registerUser();
        }
        else {
            toast("Could not found Google User Data");
        }
    }

    private interface TokenCallback {
        void onSuccess(@NonNull GoogleUserData data);
        void onNoCredential();
        void onError(@NonNull Exception e);
    }

    private GoogleUserData extractGoogleData(@NonNull GetCredentialResponse result) {

        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential cc = (CustomCredential) credential;

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(cc.getType())) {
                GoogleIdTokenCredential googleCred =
                        GoogleIdTokenCredential.createFrom(cc.getData());

                // ✅ These are the “Google data”
                String idToken = googleCred.getIdToken();          // JWT token
                String name = googleCred.getDisplayName();         // full name (may be null)
                String email = googleCred.getEmail();                 // usually email / unique id depending API
                String photoUrl = googleCred.getProfilePictureUri() != null
                        ? googleCred.getProfilePictureUri().toString()
                        : "";

                // NOTE:
                // In some environments, googleCred.getId() might not be the email.
                // We'll safely return both id + name + photo + token.
                return new GoogleUserData(name, email, photoUrl, idToken);
            }
        }

        throw new IllegalStateException("Unexpected credential type");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void requestGoogleIdToken(@NonNull String webClientId,
                                      @NonNull TokenCallback callback) {

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                mainExecutor,
                new androidx.credentials.CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        try {
                            GoogleUserData data = extractGoogleData(result);
                            callback.onSuccess(data);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        if (e instanceof NoCredentialException) {
                            callback.onNoCredential();
                        } else {
                            callback.onError(e);
                        }
                    }
                }
        );
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
                .whereEqualTo(KEY_USER_EMAIL, u_email)
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
                            editor.remove(Constants.user_email);
                            editor.remove(Constants.user_password);
                            editor.remove(Constants.checked);

                            editor.putString(Constants.user_email,u_email);
                            editor.putString(Constants.user_password,password);
                            editor.putBoolean(Constants.checked,true);
                            editor.apply();
                            editor.commit();
                        }
                        else {
                            System.out.println("Not Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(Constants.user_email);
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

    public void checkValidUser() {
        binding.progressIndicatorLogIn.setVisibility(View.VISIBLE);
        binding.logInDesignLayout.setVisibility(View.GONE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_USER_TABLE_NAME)
                .whereEqualTo(KEY_USER_EMAIL,userData.getEmailOrId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {

                        binding.progressIndicatorLogIn.setVisibility(View.GONE);
                        binding.logInDesignLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), userData.getEmailOrId()+" is already registered.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        binding.progressIndicatorLogIn.setVisibility(View.GONE);
                        binding.logInDesignLayout.setVisibility(View.VISIBLE);
                        PasswordDialogue passwordDialogue = new PasswordDialogue(UserLogin.this);
                        passwordDialogue.show(getSupportFragmentManager(),"Password_save");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressIndicatorLogIn.setVisibility(View.GONE);
                    binding.logInDesignLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(UserLogin.this, "Sign Up failed. Please try again", Toast.LENGTH_SHORT).show();
                });
    }

    public void registerUser() {
        binding.progressIndicatorLogIn.setVisibility(View.VISIBLE);
        binding.logInDesignLayout.setVisibility(View.GONE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String , Object> user = new HashMap<>();

        String is_subscribed = "No";
        String event_count = "5";
        user.put(KEY_USER_NAME, userData.getName());
        user.put(KEY_USER_EMAIL, userData.getEmailOrId());
        user.put(KEY_USER_PHONE, "");
        user.put(KEY_USER_PASSWORD, passwordForSave);
        user.put(KEY_USER_SUBSCRIBE, is_subscribed);
        user.put(KEY_USER_EVENT_COUNT, event_count);

        database.collection(KEY_USER_TABLE_NAME)
                .add(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String cloud_user_id = task.getResult().getId();
                        userInfoLists = new ArrayList<>();
                        userInfoLists.add(new UserInfoList(userData.getName(),"",userData.getEmailOrId(),passwordForSave,is_subscribed, "",event_count,cloud_user_id,""));

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(Constants.user_email);
                        editor.remove(Constants.user_password);
                        editor.remove(Constants.checked);

                        editor.putString(Constants.user_email,userData.getEmailOrId());
                        editor.putString(Constants.user_password,passwordForSave);
                        editor.putBoolean(Constants.checked,true);
                        editor.apply();
                        editor.commit();

                        SharedPreferences.Editor editor1 = sharedLogin.edit();
                        editor1.remove(KEY_USER_NAME);
                        editor1.remove(KEY_USER_PHONE);
                        editor1.remove(KEY_USER_EMAIL);
                        editor1.remove(KEY_USER_PASSWORD);
                        editor1.remove(KEY_USER_ID);
                        editor1.remove(KEY_USER_SUBSCRIBE);
                        editor1.remove(KEY_USER_EVENT_COUNT);
                        editor1.remove(LOGIN_TF);

                        editor1.putString(KEY_USER_NAME, userData.getName());
                        editor1.putString(KEY_USER_PHONE, "");
                        editor1.putString(KEY_USER_EMAIL, userData.getEmailOrId());
                        editor1.putString(KEY_USER_PASSWORD, passwordForSave);
                        editor1.putString(KEY_USER_ID,cloud_user_id);
                        editor1.putString(KEY_USER_SUBSCRIBE, is_subscribed);
                        editor1.putString(KEY_USER_EVENT_COUNT, event_count);
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
                })
                .addOnFailureListener(e -> {
                    binding.progressIndicatorLogIn.setVisibility(View.GONE);
                    binding.logInDesignLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(UserLogin.this, "Sign Up failed. Please try again", Toast.LENGTH_SHORT).show();
                });

    }
}