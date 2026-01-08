package ttit.com.shuvo.spotring.alarm_ui;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.geofences.services.GeoFenceService;

public class AlarmStartUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActivityAppearOnLockScreen();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_start_up);

        Intent intent = getIntent();
        String ev_name = intent.getStringExtra("EV_NA");
        String ev_notes = intent.getStringExtra("EV_NO");
        String ev_type = intent.getStringExtra("EV_TY");
        String tr_ty = intent.getStringExtra("TR_TY");

        MaterialButton stopButton = findViewById(R.id.stop_alarm);
        TextView ev_n = findViewById(R.id.event_triggered_with_event_name);
        TextView ev_no = findViewById(R.id.event_notes_triggered_in_fences);
        TextView ev_t = findViewById(R.id.event_type_triggered_in_fences);

        String t = tr_ty +" :  "+ev_name;
        ev_n.setText(t);
        String n = "(" + ev_notes + ")";
        ev_no.setText(n);
        ev_t.setText(ev_type);

        stopButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, GeoFenceService.class);
            stopService(serviceIntent); // Stops the foreground service
            finish(); // Close the activity
        });

    }

    private void makeActivityAppearOnLockScreen() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Dismiss keyguard (Unlock the screen)
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        }
    }
}