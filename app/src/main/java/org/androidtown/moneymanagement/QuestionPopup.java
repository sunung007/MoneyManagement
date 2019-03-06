package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import org.androidtown.moneymanagement.Common.Special;

public class QuestionPopup extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_question);

        TextView mTitleView = findViewById(R.id.text_question_title);
        TextView mContentView = findViewById(R.id.text_question_content);


        try {
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            if(title == null && content == null) throw new Exception();

            mTitleView.setText(title);
            mContentView.setText(content);

        } catch (Exception e) {
            Special.printMessage(getApplicationContext(), R.string.caution_db_load_fail);
            finish();
            return;
        }

        ImageButton mCloseButton = findViewById(R.id.imageButton_question_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
