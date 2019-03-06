package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.androidtown.moneymanagement.Common.Special;


public class LoginActivity extends AppCompatActivity {

    public long loginStartTime;
    public boolean autoLoginStop = false;

    private String mEmail;
    private String mPassword;

    private EditText mEmailView;
    private EditText mPasswordView;
    private CheckBox mAutoIdFillCheck;
    private CheckBox mAutoLoginCheck;
    private ProgressBar mProgressBar;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mProgressBar = findViewById(R.id.progressBar_login);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();
        mProgressBar.invalidate();

        mEmailView = findViewById(R.id.edit_login_email);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        autoLoginStop = intent.getBooleanExtra("auto_login_stop", false);

        //TODO: 정리
        mAutoIdFillCheck = findViewById(R.id.checkBox_login_auto_id);
        mAutoLoginCheck = findViewById(R.id.checkBox_login_auto_login);
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

        Button mSignInButton = findViewById(R.id.button_login_sign_in);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mPasswordView = findViewById(R.id.edit_login_password);
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

        View mLoginFormView = findViewById(R.id.constraint_login);
        mLoginFormView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Special.closeKeyboard(LoginActivity.this);
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
        Special.finishLoad(getWindow(), mProgressBar);
        super.onPause();
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if(TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (!cancel) {
            Special.startLoad(getWindow(), mProgressBar);
            loginStartTime = System.currentTimeMillis();
            doLogin();
        }
        else focusView.requestFocus();
    }

    private void doLogin() {

        Special.closeKeyboard(this);

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
                        Special.finishLoad(getWindow(), mProgressBar);

                        if (task.isSuccessful()) {
                            Special.printMessage(getApplicationContext(), R.string.caution_login_success);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else Special.printMessage(getApplicationContext(), R.string.caution_login_fail);
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

