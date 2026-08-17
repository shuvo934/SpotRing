package ttit.com.shuvo.spotring.subscription;

import static ttit.com.shuvo.spotring.user_auth.UserLogin.userInfoLists;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EMAIL;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PRODUCT_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_P_TOKEN;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUBSCRIBE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUB_EXPIRE_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUB_EXPIRE_M_SECONDS;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.SUB_MONTHLY_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ttit.com.shuvo.spotring.databinding.ActivitySubscriptionPackBinding;
import ttit.com.shuvo.spotring.utilities.Constants;

public class SubscriptionPack extends AppCompatActivity implements BillingManager.Listener {

    ActivitySubscriptionPackBinding binding;
    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    TextView tvSubtitle, tvMonthlyPrice, tvYearlyPrice , monthlyExpireDate, yearlyExpireDate;
    MaterialButton btnMonthly, btnYearly;

    private BillingManager billing;

    ImageView backButton;

    private Boolean loading = false;
    String parsing_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        binding = ActivitySubscriptionPackBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        View navScrim = binding.navBarSubscribeRoot;
        ViewCompat.setOnApplyWindowInsetsListener(binding.subscriptionRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            ViewGroup.LayoutParams lp = navScrim.getLayoutParams();
            lp.height = systemBars.bottom;
            navScrim.setLayoutParams(lp);
            return insets;
        });

        fullLayout = binding.subscriptionFullLayout;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator = binding.progressIndicatorSubscription;
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;

        backButton = binding.backIconSubscription;

        tvSubtitle = binding.tvSubtitle;
        tvMonthlyPrice = binding.tvMonthlyPrice;
        tvYearlyPrice = binding.tvYearlyPrice;
        btnMonthly = binding.btnMonthly;
        btnYearly = binding.btnYearly;

        monthlyExpireDate = binding.monthlyExpiredDateAfterActivation;
        monthlyExpireDate.setVisibility(View.GONE);
        yearlyExpireDate = binding.yearlyExpiredDateAfterActivation;
        yearlyExpireDate.setVisibility(View.GONE);

        billing = new BillingManager(this, this);

        Intent intentData = getIntent();
        String free_event_count = intentData.getStringExtra("Free_event_count") == null ? "0" : intentData.getStringExtra("Free_event_count");
        String subtitle = "Free users can create up to "+free_event_count+" alarms. Subscribe to unlock more.";

        tvSubtitle.setText(subtitle);

        backButton.setOnClickListener(v -> {
            if (loading) {
                Toast.makeText(this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });

        btnMonthly.setOnClickListener(v -> billing.buyMonthly(this));

        btnYearly.setOnClickListener(v -> billing.buyYearly(this));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(), "Please wait while loading", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billing != null) billing.destroy();
    }

    @Override
    public void onPrices(String monthly, String yearly) {
        runOnUiThread(() -> {
            String mp = monthly + " / month";
            tvMonthlyPrice.setText(mp);
            String yp = yearly + " / year";
            tvYearlyPrice.setText(yp);
            checkValidation();
        });
    }

    public void checkValidation() {
        String url = "https://script.google.com/macros/s/AKfycbzsv_lF5eGCTGmrLjKplFKQkXH9CBMLZl6aNDgvSpRIKk_ezMoDkcPre--M3qpSU_U/exec";
        String token = userInfoLists.get(0).getP_purchase_token();
        String product = userInfoLists.get(0).getP_subscription_pack();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
        if (token != null ) {
            if (!token.isEmpty()) {

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean ok = jsonObject.optBoolean("ok", false);
                                if (ok) {
                                    long expiryMillis = jsonObject.optLong("expiryTimeMillis", 0);

                                    if (expiryMillis > 0) {
                                        Date d = new Date(expiryMillis);
                                        String edd = sdf.format(d);
                                        Date date = Calendar.getInstance().getTime();
                                        boolean subOk = date.getTime() < d.getTime();

                                        fullLayout.setVisibility(View.VISIBLE);
                                        circularProgressIndicator.setVisibility(View.GONE);
                                        loading = false;

                                        if (subOk) {
                                            String expiryText = "Expire on: " + edd;
                                            String btText = "ACTIVATED";
                                            if (product.equals(SUB_MONTHLY_ID)) {
                                                monthlyExpireDate.setText(expiryText);
                                                yearlyExpireDate.setVisibility(View.GONE);
                                                monthlyExpireDate.setVisibility(View.VISIBLE);
                                                btnMonthly.setText(btText);
                                                btnMonthly.setEnabled(false);
                                                btnYearly.setEnabled(true);
                                                String by = "SUBSCRIBE YEARLY";
                                                btnYearly.setText(by);
                                            }
                                            else {
                                                yearlyExpireDate.setText(expiryText);
                                                monthlyExpireDate.setVisibility(View.GONE);
                                                yearlyExpireDate.setVisibility(View.VISIBLE);
                                                btnYearly.setText(btText);
                                                btnYearly.setEnabled(false);
                                                btnMonthly.setEnabled(true);
                                                String bm = "SUBSCRIBE MONTHLY";
                                                btnMonthly.setText(bm);
                                            }
                                        }
                                        else {
                                            String expiryText = "Expired on: " + edd;
                                            String btText = "SUBSCRIBE AGAIN";
                                            if (product.equals(SUB_MONTHLY_ID)) {
                                                monthlyExpireDate.setText(expiryText);
                                                yearlyExpireDate.setVisibility(View.GONE);
                                                monthlyExpireDate.setVisibility(View.VISIBLE);
                                                btnMonthly.setText(btText);
                                                btnMonthly.setEnabled(true);
                                                btnYearly.setEnabled(true);
                                                String by = "SUBSCRIBE YEARLY";
                                                btnYearly.setText(by);
                                            }
                                            else {
                                                yearlyExpireDate.setText(expiryText);
                                                monthlyExpireDate.setVisibility(View.GONE);
                                                yearlyExpireDate.setVisibility(View.VISIBLE);
                                                btnYearly.setText(btText);
                                                btnYearly.setEnabled(true);
                                                btnMonthly.setEnabled(true);
                                                String bm = "SUBSCRIBE MONTHLY";
                                                btnMonthly.setText(bm);
                                            }
                                        }

                                    }
                                    else {
                                        checkManualValidation();
                                    }
                                }
                                else {
                                    checkManualValidation();
                                }
                            }
                            catch (Exception e) {
                                checkManualValidation();
                            }
                        },
                        error -> checkManualValidation()
                )
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("packageName", getPackageName());
                        params.put("purchaseToken", token);
                        return params;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(this).add(request);

            }
            else {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
            }
        }
        else {
            fullLayout.setVisibility(View.VISIBLE);
            circularProgressIndicator.setVisibility(View.GONE);
            loading = false;
        }
    }

    public void checkManualValidation() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        loading = false;

        String exp_ms = userInfoLists.get(0).getP_sub_expire_ms();
        String product = userInfoLists.get(0).getP_subscription_pack();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

        if (exp_ms != null) {
            if (!exp_ms.isEmpty()) {
                long expTime = Long.parseLong(exp_ms);
                Date expDate = new Date(expTime);
                Date n_date = Calendar.getInstance().getTime();
                boolean subOk = n_date.getTime() < expDate.getTime();
                String date = sdf.format(expDate);
                if (subOk) {
                    String expiryText = "Expire on: " + date;
                    String btText = "ACTIVATED";
                    if (product.equals(SUB_MONTHLY_ID)) {
                        monthlyExpireDate.setText(expiryText);
                        yearlyExpireDate.setVisibility(View.GONE);
                        monthlyExpireDate.setVisibility(View.VISIBLE);
                        btnMonthly.setText(btText);
                        btnMonthly.setEnabled(false);
                        btnYearly.setEnabled(true);
                        String by = "SUBSCRIBE YEARLY";
                        btnYearly.setText(by);
                    }
                    else {
                        yearlyExpireDate.setText(expiryText);
                        monthlyExpireDate.setVisibility(View.GONE);
                        yearlyExpireDate.setVisibility(View.VISIBLE);
                        btnYearly.setText(btText);
                        btnYearly.setEnabled(false);
                        btnMonthly.setEnabled(true);
                        String bm = "SUBSCRIBE MONTHLY";
                        btnMonthly.setText(bm);
                    }
                }
                else {
                    String expiryText = "Expired on: " + date;
                    String btText = "SUBSCRIBE AGAIN";
                    if (product.equals(SUB_MONTHLY_ID)) {
                        monthlyExpireDate.setText(expiryText);
                        yearlyExpireDate.setVisibility(View.GONE);
                        monthlyExpireDate.setVisibility(View.VISIBLE);
                        btnMonthly.setText(btText);
                        btnMonthly.setEnabled(true);
                        btnYearly.setEnabled(true);
                        String by = "SUBSCRIBE YEARLY";
                        btnYearly.setText(by);
                    }
                    else {
                        yearlyExpireDate.setText(expiryText);
                        monthlyExpireDate.setVisibility(View.GONE);
                        yearlyExpireDate.setVisibility(View.VISIBLE);
                        btnYearly.setText(btText);
                        btnYearly.setEnabled(true);
                        btnMonthly.setEnabled(true);
                        String bm = "SUBSCRIBE MONTHLY";
                        btnMonthly.setText(bm);
                    }
                }
            }
        }


    }

    @Override
    public void onProChanged(boolean isPro, String purchaseToken, String productId) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;

        Calendar calendar = Calendar.getInstance();

        if (productId.equals(SUB_MONTHLY_ID)) {
            calendar.add(Calendar.MONTH, 1);
        }
        else {
            calendar.add(Calendar.YEAR,1);
        }

        Date exp_date_manual = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String ed = sdf.format(exp_date_manual);

        String url = "https://script.google.com/macros/s/AKfycbzsv_lF5eGCTGmrLjKplFKQkXH9CBMLZl6aNDgvSpRIKk_ezMoDkcPre--M3qpSU_U/exec";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean ok = jsonObject.optBoolean("ok", false);
                        if (ok) {
                            long expiryMillis = jsonObject.optLong("expiryTimeMillis", 0);

                            if (expiryMillis > 0) {
                                Date d = new Date(expiryMillis);
                                String edd = sdf.format(d);
                                updateUser(isPro, purchaseToken, productId, edd, String.valueOf(expiryMillis));

                            }
                            else {
                                Toast.makeText(this, "No Date Found", Toast.LENGTH_LONG).show();
                                updateUser(isPro, purchaseToken, productId, ed, String.valueOf(exp_date_manual.getTime()));
                            }
                        } else {
                            Toast.makeText(this, "API Error", Toast.LENGTH_LONG).show();
                            updateUser(isPro, purchaseToken, productId, ed, String.valueOf(exp_date_manual.getTime()));
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(this, "Response parse Error", Toast.LENGTH_LONG).show();
                        updateUser(isPro, purchaseToken, productId, ed, String.valueOf(exp_date_manual.getTime()));
                    }
                },
                error -> {
                    Toast.makeText(this, "Verification failed: " + error.toString(), Toast.LENGTH_LONG).show();
                    updateUser(isPro, purchaseToken, productId, ed, String.valueOf(exp_date_manual.getTime()));
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("packageName", getPackageName());
                params.put("purchaseToken", purchaseToken);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    public void updateUser(boolean isPro, String token, String product, String date, String ms) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String , Object> user = new HashMap<>();
        if (isPro) {
            user.put(KEY_USER_SUBSCRIBE, "Yes");
        }
        else {
            user.put(KEY_USER_SUBSCRIBE, "No");
        }
        user.put(KEY_USER_PRODUCT_ID, product);
        user.put(KEY_USER_P_TOKEN, token);
        user.put(KEY_USER_SUB_EXPIRE_DATE, date);
        user.put(KEY_USER_SUB_EXPIRE_M_SECONDS, ms);

        database.collection(KEY_USER_TABLE_NAME).whereEqualTo(KEY_USER_EMAIL, userInfoLists.get(0).getP_email())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        database.collection(KEY_USER_TABLE_NAME)
                                .document(documentSnapshot.getId())
                                .update(user)
                                .addOnSuccessListener(unused -> {
                                    fullLayout.setVisibility(View.VISIBLE);
                                    circularProgressIndicator.setVisibility(View.GONE);
                                    loading = false;

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
                                    long expTime = Long.parseLong(ms);
                                    Date expDate = new Date(expTime);
                                    String e_date = sdf.format(expDate);

                                    String expiryText = "Expire on: " + e_date;
                                    String btText = "ACTIVATED";
                                    if (product.equals(SUB_MONTHLY_ID)) {
                                        monthlyExpireDate.setText(expiryText);
                                        yearlyExpireDate.setVisibility(View.GONE);
                                        monthlyExpireDate.setVisibility(View.VISIBLE);
                                        btnMonthly.setText(btText);
                                        btnMonthly.setEnabled(false);
                                        btnYearly.setEnabled(true);
                                        String by = "SUBSCRIBE YEARLY";
                                        btnYearly.setText(by);
                                    }
                                    else {
                                        yearlyExpireDate.setText(expiryText);
                                        monthlyExpireDate.setVisibility(View.GONE);
                                        yearlyExpireDate.setVisibility(View.VISIBLE);
                                        btnYearly.setText(btText);
                                        btnYearly.setEnabled(false);
                                        btnMonthly.setEnabled(true);
                                        String bm = "SUBSCRIBE MONTHLY";
                                        btnMonthly.setText(bm);
                                    }
                                    SharedPreferences sharedLogin = getSharedPreferences(Constants.LOGIN_ACTIVITY_FILE,MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedLogin.edit();
                                    editor1.remove(KEY_USER_SUBSCRIBE);
                                    editor1.remove(KEY_USER_PRODUCT_ID);
                                    editor1.remove(KEY_USER_P_TOKEN);
                                    editor1.remove(KEY_USER_SUB_EXPIRE_DATE);
                                    editor1.remove(KEY_USER_SUB_EXPIRE_M_SECONDS);

                                    editor1.putString(KEY_USER_SUBSCRIBE, isPro ? "Yes" : "No");
                                    editor1.putString(KEY_USER_PRODUCT_ID, product);
                                    editor1.putString(KEY_USER_P_TOKEN, token);
                                    editor1.putString(KEY_USER_SUB_EXPIRE_DATE, date);
                                    editor1.putString(KEY_USER_SUB_EXPIRE_M_SECONDS, ms);
                                    editor1.apply();
                                    editor1.commit();

                                    userInfoLists.get(0).setP_subscribed(isPro ? "Yes" : "No");
                                    userInfoLists.get(0).setP_subscription_pack(product);
                                    userInfoLists.get(0).setP_purchase_token(token);
                                    userInfoLists.get(0).setP_sub_expire_date(date);
                                    userInfoLists.get(0).setP_sub_expire_ms(ms);

                                })
                                .addOnFailureListener(e -> {
                                    parsing_message = e.getLocalizedMessage();
                                    loading = false;
                                    alertMessage(isPro, token, product, date, ms);
                                });

                    }
                })
                .addOnFailureListener(e -> {
                    parsing_message = e.getLocalizedMessage();
                    loading = false;
                    alertMessage(isPro, token, product, date, ms);
                });
    }

    public void alertMessage(boolean isPro, String token, String product, String date, String ms) {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateUser(isPro, token, product, date, ms);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}