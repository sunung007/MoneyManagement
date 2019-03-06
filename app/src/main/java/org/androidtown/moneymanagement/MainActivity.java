package org.androidtown.moneymanagement;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Enroll.EnrollFragment;
import org.androidtown.moneymanagement.Manage.ManageFragment;
import org.androidtown.moneymanagement.Search.SearchFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long backKeyPressedTime = 0;
    private String fragmentTitle = null;

    private static int previousFragmentID = R.id.nav_check;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    public static FragmentManager previousFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String title = getString(R.string.title_search);
        mToolbar = findViewById(R.id.toolbar_main);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_main, new SearchFragment());
        ft.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        previousFragmentManager = getSupportFragmentManager();

        mNavigationView = findViewById(R.id.navigation_main);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Special.closeKeyboard(MainActivity.this);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_main);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (ManageFragment.isLoading) {
            Special.printMessage(getApplicationContext(), R.string.caution_db_on_load);
        }
        else if ((previousFragmentID == R.id.nav_check
                || getSupportFragmentManager().getBackStackEntryCount() == 0)
                && System.currentTimeMillis() - backKeyPressedTime >= 2000) {

            backKeyPressedTime = System.currentTimeMillis();
            Special.printMessage(getApplicationContext(), R.string.caution_exit_back_button);
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
        Special.closeKeyboard(MainActivity.this);

        Fragment fragment = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        int id = item.getItemId();
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
            fragment = fm.findFragmentById(R.id.fragment_main);
            ft.detach(fragment).attach(fragment);
            ft.commit();

            DrawerLayout drawer = findViewById(R.id.drawer_main);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(id != R.id.nav_enroll && id != R.id.nav_manage && fragment != null) {
            previousFragmentID = id;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.fragment_main, fragment);

            if (fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_main);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    protected void onPause() {
        Special.closeKeyboard(this);
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
            ft.replace(R.id.fragment_main, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_main);
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(requestCode == 5 && resultCode == RESULT_OK) {

            Fragment fragment = new ManageFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            previousFragmentID = R.id.nav_manage;
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.fragment_main, fragment);

            if(fm.getBackStackEntryCount() == 0)
                ft.addToBackStack(null);

            ft.commit();

            if(fragmentTitle != null)
                mToolbar.setTitle(fragmentTitle);

            DrawerLayout drawer = findViewById(R.id.drawer_main);
            drawer.closeDrawer(GravityCompat.START);
        }

        if(resultCode == RESULT_CANCELED) {
            mNavigationView.setCheckedItem(previousFragmentID);

            DrawerLayout drawer = findViewById(R.id.drawer_main);
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}


