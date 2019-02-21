package org.androidtown.moneymanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidtown.moneymanagement.Enroll.EnrollFragment;
import org.androidtown.moneymanagement.Manage.ManageFragment;
import org.androidtown.moneymanagement.Search.SearchFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public int backCount = 0;
    private long backKeyPressedTime = 0;
    private String fragmentTitle = null;

    private static int previousFragmentID = R.id.nav_check;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private static Window thisWindow;
    public static FragmentManager previousFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = getString(R.string.title_search);
        mToolbar = findViewById(R.id.main_toolbar);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment, new SearchFragment());
        ft.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        thisWindow = getWindow();

        previousFragmentManager = getSupportFragmentManager();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (ManageFragment.isLoading) {
            String message = getResources().getString(R.string.caution_db_on_load);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if ((previousFragmentID == R.id.nav_check
                || getSupportFragmentManager().getBackStackEntryCount() == 0)
                && System.currentTimeMillis() - backKeyPressedTime >= 2000) {

            backKeyPressedTime = System.currentTimeMillis();
            backCount++;

            String message = getResources().getString(R.string.caution_exit_back_button);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (System.currentTimeMillis() - backKeyPressedTime < 2000){
            finish();
        }
        else {
            previousFragmentID = R.id.nav_check;
            mNavigationView.setCheckedItem(R.id.nav_check);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        assert inputMethodManager != null;
        if(inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }


        Fragment fragment = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        if(id == R.id.nav_check) {
            fragmentTitle = getResources().getString(R.string.title_search);
            fragment = new SearchFragment();
        }
        else if (id == R.id.nav_enroll && previousFragmentID != id) {
            fragmentTitle = getResources().getString(R.string.title_enroll_subtitle);
            Intent intent = new Intent(getApplicationContext(), AuthorizationPopup.class);
            startActivityForResult(intent, 4);
        }
        else if (id == R.id.nav_manage && previousFragmentID != id) {
            fragmentTitle = getResources().getString(R.string.title_manage);
            Intent intent = new Intent(getApplicationContext(), AuthorizationPopup.class);
            startActivityForResult(intent, 5);
        }
        else if (id == R.id.developer) {
            fragmentTitle = getResources().getString(R.string.title_developers_info);
            fragment = new DevelopersFragment();
        }
        else if (id == R.id.logout) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if(firebaseUser != null) {
                firebaseAuth.signOut();
            }

            String message = getResources().getString(R.string.title_menu_logout);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("auto_login_stop", true);

            startActivity(intent);
            finish();

            return true;
        }


        if(previousFragmentID == id) {
            fragment = fm.findFragmentById(R.id.main_fragment);
            ft.detach(fragment).attach(fragment);
            ft.commit();

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(id != R.id.nav_enroll && id != R.id.nav_manage && fragment != null) {
            previousFragmentID = id;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_fragment, fragment);

            if (fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    protected void onPause() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        assert inputMethodManager != null;
        if(inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        }

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 4 && resultCode == RESULT_OK) {
            Fragment fragment = new EnrollFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            previousFragmentID = R.id.nav_enroll;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_fragment, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(requestCode == 5 && resultCode == RESULT_OK) {

            Fragment fragment = new ManageFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            previousFragmentID = R.id.nav_manage;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_fragment, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        if(resultCode == RESULT_CANCELED) {
            mNavigationView.setCheckedItem(previousFragmentID);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public static void screenUntouchable() {
        thisWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void screenTouchable () {
        thisWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}


