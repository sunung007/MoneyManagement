package org.androidtown.moneymanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int previousFragmentID = R.id.nav_check;
    static FragmentManager previousFragmentManager;

    long backKeyPressedTime = 0;
    int backCount = 0;
    String fragmentTitle = null;

    Toolbar mToolbar;
    NavigationView mNavigationView;

    private boolean isAuthrorized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = getString(R.string.title_search);
        mToolbar = findViewById(R.id.main_toolbar);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);

        // Set first fragment to search mode.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment, new SearchStudentFragment());
        ft.commit();

        // Set drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        previousFragmentManager = getSupportFragmentManager();

        // Set navigation view
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
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
        else if (ManageStudentFragment.isLoading) {
            String message = "데이터베이스를 로딩 중입니다.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if ((previousFragmentID == R.id.nav_check
                || getSupportFragmentManager().getBackStackEntryCount() == 0)
                && System.currentTimeMillis() - backKeyPressedTime >= 2000) {

            backKeyPressedTime = System.currentTimeMillis();
            backCount++;

            String message = "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.";
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Close soft key.
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


        // Initialize fragment, and set about animation part.
        Fragment fragment = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        // When the navigation item is selected, change fragment.
        if(id == R.id.nav_check) {
            fragmentTitle = "납부자 검색";
            fragment = new SearchStudentFragment();
        }
        else if (id == R.id.nav_enroll && previousFragmentID != id) {
            fragmentTitle = "납부자 신규 등록";
            Intent intent = new Intent(getApplicationContext(), AuthorizationPopup.class);
            startActivityForResult(intent, 4);
        }
        else if (id == R.id.nav_manage && previousFragmentID != id) {
            fragmentTitle = "납부자 전체 명단 관리";
            Intent intent = new Intent(getApplicationContext(), AuthorizationPopup.class);
            startActivityForResult(intent, 5);
        }
        else if (id == R.id.developer) {
            fragmentTitle = "개발자 정보";
            fragment = new DevelopersFragment();
        }
        else if (id == R.id.logout) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if(firebaseUser != null) {
                firebaseAuth.signOut();
            }

            String message = "로그아웃";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("auto_login_stop", true);

            startActivity(intent);
            finish();

            return true;
        }


        // If fragment to open is same with current fragment,
        // do not change.
        if(previousFragmentID == id) {
            fragment = fm.findFragmentById(R.id.main_fragment);
            ft.detach(fragment).attach(fragment);
            ft.commit();

            // Close drawer layout.
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

            // Close drawer layout.
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    protected void onPause() {
        // Close soft key.
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 4 && resultCode == RESULT_OK) {

            Fragment fragment = new EnrollFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            // If fragment to open is same with current fragment,
            // do not change.
            previousFragmentID = R.id.nav_enroll;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_fragment, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            // Close drawer layout.
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(requestCode == 5 && resultCode == RESULT_OK) {

            Fragment fragment = new ManageStudentFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            // If fragment to open is same with current fragment,
            // do not change.
            previousFragmentID = R.id.nav_manage;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_fragment, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            // Close drawer layout.
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        if(resultCode == RESULT_CANCELED) {
            mNavigationView.setCheckedItem(previousFragmentID);

            // Close drawer layout.
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }



}


