package com.bandwidth.androidreference.activity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.bandwidth.androidreference.CallService;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.utils.NotificationHelper;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.androidreference.utils.SaveManager;
import com.bandwidth.androidreference.fragment.AccountInfoFragment;
import com.bandwidth.androidreference.fragment.DialerFragment;
import com.bandwidth.androidreference.fragment.IncomingCallFragment;
import com.bandwidth.androidreference.fragment.RegisterFragment;
import com.bandwidth.androidreference.intent.BWSipIntent;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private MainActivity mainActivity;
    private CallService callService;
    private Menu menu;
    private LocalBroadcastManager broadcastManager;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CallService.LocalBinder binder = (CallService.LocalBinder) service;
            callService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CallService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        broadcastManager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {


            if (getResources().getString(R.string.application_server_url).equals("https://YOUR_APPLICATION_SERVER_URL_GOES_HERE")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.dialog_readme_title))
                        .setMessage(getResources().getString(R.string.dialog_readme_message))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
            else {
                if (SaveManager.getUser(this) != null) {
                    DialerFragment dialerFragment = new DialerFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, dialerFragment)
                            .commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new RegisterFragment())
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(BWSipIntent.INCOMING_CALL)) {
            IncomingCallFragment incomingCallFragment = new IncomingCallFragment();
            incomingCallFragment.setFromNumber(intent.getStringExtra(BWSipIntent.INCOMING_CALL));
            goToFragment(incomingCallFragment, true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public void setMenuVisible(boolean visible) {
        if (menu != null) {
            menu.setGroupVisible(R.id.menu_items, visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_account_info) {
            showAccountInfo();
            return true;
        }
        else if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void goToFragment(Fragment fragment) {
        goToFragment(fragment, false);
    }

    public void goToFragment(Fragment fragment, boolean addToBackStack) {
        setContentView(R.layout.activity_main);
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

    }

    @Override
    public void onBackStackChanged() {
        boolean showBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private void showAccountInfo() {
        goToFragment(new AccountInfoFragment(), true);
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_title_sign_out))
                .setMessage(getResources().getString(R.string.dialog_message_sign_out))
                .setPositiveButton(getResources().getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveManager.removeUser(mainActivity);
                        broadcastManager.sendBroadcast(new Intent(BWSipIntent.DEREGISTER));
                        goToFragment(new RegisterFragment());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }
}
