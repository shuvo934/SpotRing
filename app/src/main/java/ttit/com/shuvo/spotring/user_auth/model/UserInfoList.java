package ttit.com.shuvo.spotring.user_auth.model;

public class UserInfoList {
    private String p_name;
    private String p_phone;
    private String p_email;
    private String p_password;
    private String p_subscribed;
    private String p_subscription_pack;
    private String p_purchase_token;
    private String p_sub_expire_date;
    private String p_sub_expire_ms;
    private String cloud_user_id;
    private String fcm_token;


    public UserInfoList(String p_name, String p_phone, String p_email, String p_password, String p_subscribed, String p_subscription_pack, String p_purchase_token, String p_sub_expire_date, String p_sub_expire_ms, String cloud_user_id, String fcm_token) {
        this.p_name = p_name;
        this.p_phone = p_phone;
        this.p_email = p_email;
        this.p_password = p_password;
        this.p_subscribed = p_subscribed;
        this.p_subscription_pack = p_subscription_pack;
        this.p_purchase_token = p_purchase_token;
        this.p_sub_expire_date = p_sub_expire_date;
        this.p_sub_expire_ms = p_sub_expire_ms;
        this.cloud_user_id = cloud_user_id;
        this.fcm_token = fcm_token;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_phone() {
        return p_phone;
    }

    public void setP_phone(String p_phone) {
        this.p_phone = p_phone;
    }

    public String getP_email() {
        return p_email;
    }

    public void setP_email(String p_email) {
        this.p_email = p_email;
    }

    public String getP_password() {
        return p_password;
    }

    public void setP_password(String p_password) {
        this.p_password = p_password;
    }

    public String getCloud_user_id() {
        return cloud_user_id;
    }

    public void setCloud_user_id(String cloud_user_id) {
        this.cloud_user_id = cloud_user_id;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getP_subscribed() {
        return p_subscribed;
    }

    public void setP_subscribed(String p_subscribed) {
        this.p_subscribed = p_subscribed;
    }

    public String getP_subscription_pack() {
        return p_subscription_pack;
    }

    public void setP_subscription_pack(String p_subscription_pack) {
        this.p_subscription_pack = p_subscription_pack;
    }

    public String getP_purchase_token() {
        return p_purchase_token;
    }

    public void setP_purchase_token(String p_purchase_token) {
        this.p_purchase_token = p_purchase_token;
    }

    public String getP_sub_expire_date() {
        return p_sub_expire_date;
    }

    public void setP_sub_expire_date(String p_sub_expire_date) {
        this.p_sub_expire_date = p_sub_expire_date;
    }

    public String getP_sub_expire_ms() {
        return p_sub_expire_ms;
    }

    public void setP_sub_expire_ms(String p_sub_expire_ms) {
        this.p_sub_expire_ms = p_sub_expire_ms;
    }
}
