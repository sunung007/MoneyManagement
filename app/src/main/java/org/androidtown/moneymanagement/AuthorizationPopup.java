package org.androidtown.moneymanagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class AuthorizationPopup extends AppCompatActivity {

    EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_authrization_popup);

        mPasswordView = findViewById(R.id.authorization_password);

        Button mOkButton = findViewById(R.id.button_authorization_ok);
        Button mCancelButton = findViewById(R.id.button_authorization_cancel);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if(inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER) {
                    checkPassword();
                    return true;
                }
                return false;
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mPasswordView.getText().length() == 4) {
                    checkPassword();
                }
            }
        });

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void checkPassword() {
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        String password = mPasswordView.getText().toString();
        if(password.length() != 4) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Close soft key.
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            assert inputMethodManager != null;
            if(inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }

            String message = "인증에 성공하였습니다.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            setResult(RESULT_OK);
            finish();
        }

    }

    public boolean isPasswordValid(String _password) {
        return _password.equals(getResources().getString(R.string.code));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            assert inputMethodManager != null;
            if(inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }

            return false;
        }

        return super.dispatchTouchEvent(ev);
    }
}
