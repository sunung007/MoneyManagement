package org.androidtown.moneymanagement.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidtown.moneymanagement.QuestionPopup;

import java.util.Objects;

public class Special {

    public static void printMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void printMessage(Context context, int stringId) {
        String message = context.getResources().getString(stringId);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.show();
    }


    public static boolean startLoad(Window window, ProgressBar progressBar) {
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        return true;
    }

    public static boolean finishLoad(Window window, ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        return false;
    }


    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null && imm.isActive()) {
            try {
                imm.hideSoftInputFromWindow(Objects
                        .requireNonNull(activity.getCurrentFocus())
                        .getWindowToken(), 0);
            } catch (Exception e) {
                imm.hideSoftInputFromWindow(new View(activity).getWindowToken(), 0);
            }
        }
    }


    public static View.OnClickListener questionButton(
            final Context context,
            final int titleId,
            final int contentId) {

        View.OnClickListener onClickListener;
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionPopup.class);

                String title = context.getString(titleId);
                String content = context.getString(contentId);
                intent.putExtra("title", title);
                intent.putExtra("content", content);

                context.startActivity(intent);
            }
        };

        return onClickListener;
    }
}
