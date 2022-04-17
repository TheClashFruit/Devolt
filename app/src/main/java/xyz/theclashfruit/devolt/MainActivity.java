package xyz.theclashfruit.devolt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.hcaptcha.sdk.HCaptcha;
import com.hcaptcha.sdk.HCaptchaConfig;
import com.hcaptcha.sdk.HCaptchaException;
import com.hcaptcha.sdk.HCaptchaSize;
import com.hcaptcha.sdk.HCaptchaTheme;
import com.hcaptcha.sdk.HCaptchaTokenResponse;
import com.hcaptcha.sdk.tasks.OnFailureListener;
import com.hcaptcha.sdk.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextInputLayout textEmail;
    private TextInputLayout textPassword;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        HCaptchaConfig config = HCaptchaConfig.builder()
                .siteKey("3daae85e-09ab-4ff6-9f24-e8f4f335e433")
                .locale("en")
                .size(HCaptchaSize.NORMAL)
                .loading(true)
                .resetOnTimeout(true)
                .theme(HCaptchaTheme.DARK)
                .build();


        loginButton = findViewById(R.id.loginButton);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);

        loginButton.setOnClickListener(view -> {
            HCaptcha.getClient(MainActivity.this).verifyWithHCaptcha(config)
                    .addOnSuccessListener(response -> {
                        String userResponseToken = response.getTokenResult();

                        JSONObject jsonBody = null;

                        try {
                            jsonBody = new JSONObject("{\"email\":\"".concat(textEmail.getEditText().getText().toString()).concat("\",\"password\":\"").concat(textPassword.getEditText().getText().toString()).concat("\",\"friendly_name\":\"Devolt v1 - Revite Native Android Client\",\"captcha\":\"").concat(userResponseToken).concat("\"}"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://api.revolt.chat/auth/session/login", jsonBody,
                                reqResponse -> Log.d("MainActivity", "Response: " + reqResponse),
                                error -> Log.e("MainActivity", "Error: " + error.toString()));

                        stringRequest.setTag("rq");
                        requestQueue.add(stringRequest);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error code: " + e.getStatusCode());
                        Log.e("MainActivity", "Error msg: " + e.getMessage());
                    });
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("rq");
        }
    }
}