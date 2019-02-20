package org.androidtown.moneymanagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


public class DevelopersFragment extends Fragment {
    LinearLayout mEmailView;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_developers, container, false);

        mEmailView = view.findViewById(R.id.developer_email);
        mEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebookMessage();
            }
        });

        return view;
    }

    public void openFacebookMessage() {
        String message = "준비중입니다.";
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
        toast.show();
    }

}
