package org.androidtown.moneymanagement;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class DevelopersFragment extends Fragment {

    private ImageView message1;
    private TextView message2;

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_developers, container, false);

        message1 = view.findViewById(R.id.developer_message1);
        message1.setOnClickListener(messageOnClickListener);
        message1.setOnTouchListener(mEmailViewOnTouchListener);

        message2 = view.findViewById(R.id.developer_message2);
        message2.setOnClickListener(messageOnClickListener);
        message2.setOnTouchListener(mEmailViewOnTouchListener);
        message2.setTextColor(ContextCompat.getColor(
                Objects.requireNonNull(getContext()), R.color.colorPrimaryLight));


        return view;
    }

    View.OnClickListener messageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openFacebookMessage();
        }
    };


    View.OnTouchListener mEmailViewOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();

            if(action == MotionEvent.ACTION_DOWN) {
                message1.setColorFilter(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimary));
                message2.setTextColor(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimary));
            }
            else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                message1.setColorFilter(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimaryLight));
                message2.setTextColor(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimaryLight));
            }

            return false;
        }
    };


    public void openFacebookMessage() {
        String uri = "http://m.me/sunung007";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

}
