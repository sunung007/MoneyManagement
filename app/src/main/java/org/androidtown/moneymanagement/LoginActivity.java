package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    public long loginStartTime;
    public boolean autoLoginStop = false;

    private String mEmail;
    private String mPassword;

    private EditText mEmailView;
    private EditText mPasswordView;

    private ProgressBar mProgressBar;
    private CheckBox mAutoIdFillCheck;
    private CheckBox mAutoLoginCheck;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mProgressBar = findViewById(R.id.login_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();
        mProgressBar.invalidate();

        mEmailView = findViewById(R.id.email);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        autoLoginStop = intent.getBooleanExtra("auto_login_stop", false);

        mAutoIdFillCheck = findViewById(R.id.auto_id_fill_checkBox);
        mAutoLoginCheck = findViewById(R.id.auto_login_checkBox);
        mAutoLoginCheck.setVisibility(View.GONE);
        mAutoIdFillCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    mAutoLoginCheck.setChecked(true);
                    mAutoLoginCheck.setVisibility(View.VISIBLE);
                } else {
                    mAutoLoginCheck.setChecked(false);
                    mAutoLoginCheck.setVisibility(View.GONE);
                }
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                }
                return false;
            }
        });

        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    attemptLogin();
                }
                return false;
            }
        });

        View mLoginFormView = findViewById(R.id.login_form);
        mLoginFormView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }
            }
        });


        sharedPreferences = getSharedPreferences("id_and_password", Activity.MODE_PRIVATE);
        mEmail = sharedPreferences.getString("id", "");
        mPassword = sharedPreferences.getString("password", "");

        boolean isAutoIdFillChecked = sharedPreferences.getBoolean("auto_id_fill_checked", false);
        boolean isAutoLoginChecked = sharedPreferences.getBoolean("auto_login_checked", false);

        if(isAutoIdFillChecked) {
            mEmailView.setText(mEmail);
            mAutoIdFillCheck.setChecked(true);

            if(isAutoLoginChecked) {
                mPasswordView.setText(mPassword);
                mAutoLoginCheck.setChecked(true);

                if(!autoLoginStop) attemptLogin();
            }
        }
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.GONE);
        super.onPause();
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loginStartTime = System.currentTimeMillis();

            doLogin();
        }
    }

    private void doLogin() {

        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            assert inputMethodManager != null;
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }
        } catch (Exception ignored) {

        }

        if(mAutoIdFillCheck.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("id", mEmail);
            editor.putBoolean("auto_id_fill_checked", true);

            if(mAutoLoginCheck.isChecked()) {
                editor.putString("password", mPassword);
                editor.putBoolean("auto_login_checked", true);
            }

            editor.apply();
        }

        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        mProgressBar.setVisibility(View.GONE);

                        String message;

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            message = getResources().getString(R.string.caution_login_success);
                            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                            toast.show();

                            startActivity(intent);
                            finish();
                        } else {
                            message = getResources().getString(R.string.caution_login_fail);
                            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                            toast.show();                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty();
    }

}

