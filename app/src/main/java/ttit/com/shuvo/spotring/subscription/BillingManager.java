package ttit.com.shuvo.spotring.subscription;

import static ttit.com.shuvo.spotring.utilities.Constants.SUB_MONTHLY_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.SUB_YEARLY_ID;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingManager implements PurchasesUpdatedListener {

    public interface Listener {
        void onPrices(String monthly, String yearly);
        void onProChanged(boolean isPro, String purchaseToken, String productId);
        void onError(String msg);
    }

    private final Context appContext;
    private final Listener listener;
    private BillingClient billingClient;

    private final Map<String, ProductDetails> productDetailsMap = new HashMap<>();

    public BillingManager(@NonNull Context context, @NonNull Listener listener) {
        this.appContext = context.getApplicationContext();
        this.listener = listener;
        init();
    }

    private void init() {
        billingClient = BillingClient.newBuilder(appContext)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts() // safe to include
                        .build())
                .enableAutoServiceReconnection()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    loadProducts(); // sync Pro status on startup
                } else {
                    listener.onError("Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Play may reconnect automatically, but you can retry if you want
            }
        });
    }

    private void loadProducts() {
        List<QueryProductDetailsParams.Product> products = Arrays.asList(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(SUB_MONTHLY_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(SUB_YEARLY_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();

        billingClient.queryProductDetailsAsync(params, (result, list) -> {
            if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                listener.onError("Failed to load products: " + result.getDebugMessage());
                return;
            }

            for (ProductDetails pd : list.getProductDetailsList()) productDetailsMap.put(pd.getProductId(), pd);

            String monthly = formattedRecurringPrice(productDetailsMap.get(SUB_MONTHLY_ID));
            String yearly  = formattedRecurringPrice(productDetailsMap.get(SUB_YEARLY_ID));

            listener.onPrices(
                    monthly != null ? monthly : "-",
                    yearly  != null ? yearly  : "-"
            );
        });
    }

    private String formattedRecurringPrice(ProductDetails pd) {
        if (pd == null) return null;
        List<ProductDetails.SubscriptionOfferDetails> offers = pd.getSubscriptionOfferDetails();
        if (offers == null || offers.isEmpty()) return null;

        ProductDetails.SubscriptionOfferDetails offer = offers.get(0);
        List<ProductDetails.PricingPhase> phases =
                offer.getPricingPhases().getPricingPhaseList();

        if (phases.isEmpty()) return null;

        // last phase is usually the recurring price
        return phases.get(phases.size() - 1).getFormattedPrice();
    }

    public void buyMonthly(Activity activity) { launch(activity, SUB_MONTHLY_ID); }
    public void buyYearly(Activity activity)  { launch(activity, SUB_YEARLY_ID);  }

    private void launch(Activity activity, String productId) {
        ProductDetails pd = productDetailsMap.get(productId);
        if (pd == null) {
            listener.onError("Product not loaded yet. Try again.");
            loadProducts();
            return;
        }

        List<ProductDetails.SubscriptionOfferDetails> offers = pd.getSubscriptionOfferDetails();
        if (offers == null || offers.isEmpty()) {
            listener.onError("No offer found for: " + productId);
            return;
        }

        String offerToken = offers.get(0).getOfferToken();

        BillingFlowParams.ProductDetailsParams p =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(pd)
                        .setOfferToken(offerToken)
                        .build();

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(p))
                .build();

        BillingResult r = billingClient.launchBillingFlow(activity, flowParams);
        if (r.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            listener.onError("Launch failed: " + r.getDebugMessage());
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            handlePurchases(list);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // user canceled, ignore
            listener.onError("Purchase cancelled");
        } else {
            listener.onError("Purchase failed: " + billingResult.getDebugMessage());
        }
    }

    private void handlePurchases(List<Purchase> purchases) {
        boolean pro = false;
        String p_token = "";
        String productId = "";
        for (Purchase p : purchases) {
            if (p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                pro = true;

                // IMPORTANT: acknowledge subscription purchase
                if (!p.isAcknowledged()) {
                    AcknowledgePurchaseParams ack =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(p.getPurchaseToken())
                                    .build();

                    billingClient.acknowledgePurchase(ack, ackResult -> {});
                }

                p_token = p.getPurchaseToken();
                List<String> products = p.getProducts();
                if (!products.isEmpty()) {
                    productId = products.get(0);
                }
            }
        }

        listener.onProChanged(pro, p_token, productId);
    }

    public void destroy() {
        if (billingClient != null) billingClient.endConnection();
    }
}
