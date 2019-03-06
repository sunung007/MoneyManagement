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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.androidtown.moneymanagement.Common.Special;

public class AuthorizationPopup extends AppCompatActivity {

    private EditText mPasswordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_authrization);


        // TODO: 코드 정리
        mPasswordView = findViewById(R.id.edit_authorization_password);
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


        Button mOkButton = findViewById(R.id.button_authorization_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });

        Button mCancelButton = findViewById(R.id.button_authorization_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Special.closeKeyboard(AuthorizationPopup.this);

                setResult(RESULT_CANCELED);
                finish();
            }
        });


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    public void checkPassword() {
        mPasswordView.setError(null);

        boolean cancel = false;

        String password = mPasswordView.getText().toString();
        if(password.length() != 4) {
            mPasswordView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (cancel) {
            mPasswordView.requestFocus();
            mPasswordView.setText("");
        } else {
            Special.closeKeyboard(AuthorizationPopup.this);
            Special.printMessage(getApplicationContext(), R.string.caution_authorize_success);

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
            Special.closeKeyboard(AuthorizationPopup.this);
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }
}
