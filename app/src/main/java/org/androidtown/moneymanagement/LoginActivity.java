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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// There are many parts that can be deleted.
// But, later, we may add the function that search ID and password in DB server.
// When we do that, we must use that function.
// So now, I just put them in baskets.

public class LoginActivity extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");

    static final int REQUEST_READ_CONTACTS = 0;

    EditText mEmailView;
    EditText mPasswordView;

    View mLoginFormView;
    ProgressBar mProgressBar;
    CheckBox mAutoIdFillCheck;
    CheckBox mAutoLoginCheck;

    private FirebaseAuth firebaseAuth;

    private String mEmail;
    private String mPassword;

    public boolean autoLoginStop = false;

    // For auto login.
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set soft keyboard not to hide when it is up.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mProgressBar = findViewById(R.id.login_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();
        mProgressBar.invalidate();

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);

        mAutoIdFillCheck = findViewById(R.id.auto_id_fill_checkBox);
        mAutoLoginCheck = findViewById(R.id.auto_login_checkBox);
        mAutoLoginCheck.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        autoLoginStop = intent.getBooleanExtra("auto_login_stop", false);

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

        // Login button listener
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Edit action listener
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                }
                return false;
            }
        });

        // Enter key listener on password edit text.
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    attemptLogin();
                }
                return false;
            }
        });

        // To hide soft keyboard.
        mLoginFormView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
                }
            }
        });


        // Auto login field.
        sharedPreferences = getSharedPreferences("id_and_password", Activity.MODE_PRIVATE);
        mEmail = sharedPreferences.getString("id", "");
        mPassword = sharedPreferences.getString("password", "");

        Boolean isAutoIdFillChecked = sharedPreferences.getBoolean("auto_id_fill_checked", false);
        Boolean isAutoLoginChecked = sharedPreferences.getBoolean("auto_login_checked", false);

        if(isAutoIdFillChecked) {
            mEmailView.setText(mEmail);
            mAutoIdFillCheck.setChecked(true);

            if(isAutoLoginChecked) {
                mPasswordView.setText(mPassword);
                mAutoLoginCheck.setChecked(true);

                if(!autoLoginStop)
                    attemptLogin();
            }
        }
    }


    // Attempts to sign in or register the account specified by the login form.
    // If there are form errors (invalid email, missing fields, etc.), the
    // errors are presented and no actual login attempt is made.
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressBar.setVisibility(View.VISIBLE);

            doLogin();
        }
    }

    private void doLogin() {

        try {
            // Close soft key.
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {

        }

        if(mAutoIdFillCheck.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("id", mEmail);
            editor.putBoolean("auto_id_fill_checked", true);

            if(mAutoLoginCheck.isChecked()) {
                editor.putString("password", mPassword);
                editor.putBoolean("auto_login_checked", true);
            }

            editor.commit();
        }


        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String resultMessage;

                        if (task.isSuccessful()) {
                            mProgressBar.setVisibility(View.GONE);

                            // Change intent
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            resultMessage = "로그인 성공";
                            Toast toast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                            toast.show();

                            startActivity(intent);
                            finish();
                        } else {
                            mProgressBar.setVisibility(View.GONE);

                            resultMessage = "로그인 실패";
                            Toast toast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT);
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

