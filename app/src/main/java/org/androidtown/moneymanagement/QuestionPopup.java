package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionPopup extends AppCompatActivity {
    String title;
    String content;

    TextView mTitleView;
    TextView mContentView;

    ImageButton mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_question_popup);

        mTitleView = findViewById(R.id.question_title);
        mContentView = findViewById(R.id.question_content);
        mCloseButton = findViewById(R.id.question_close);


        try {
            Intent intent = getIntent();

            title = intent.getStringExtra("title");
            content = intent.getStringExtra("content");

            if(title == null && content == null) {
                throw new Exception();
            }

            mTitleView.setText(title);
            mContentView.setText(content);

        } catch (Exception e) {
            String message;
            message = "Loading failed.";

            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            finish();
            return;
        }

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
