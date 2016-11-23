package com.vk.android.mobileweatherguide;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class NotificationActivity extends AppCompatActivity
{
    private ApplicationDrawerNavigationManager applicationDrawerNavigationManager;
    private boolean isExitConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**** SETUP ****/

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        this.setContentView(R.layout.activity_notification);

        // Add UserPreferenceFragment fragment to activity_user_preference container in activity_user_preference layout.
        FragmentManager fragmentManager = this.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.activity_notification, NotificationFragment.getInstance()).commit();

        this.setTitle(this.getResources().getString(R.string.menu_notifications));

        this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar()));

        /**** END OF SETUP ****/
    }

    @Override
    protected void onStart() // This method is called when the activity is about to become visible.
    {
        super.onStart();

        if(this.getApplicationDrawerNavigationManager() == null) // If applicationDrawerNavigationManager property is not set, set it.
        {
            this.setApplicationDrawerNavigationManager(new ApplicationDrawerNavigationManager(this, this.getSupportActionBar()));
        }
    }

    @Override
    protected void onStop() // This method is called when the activity is no longer visible.
    {
        super.onStop();

        this.setApplicationDrawerNavigationManager(null); // Unset applicationDrawerNavigationManager property.

        this.setApplicationDrawerNavigationManager(null); // Unset applicationDrawerNavigationManager property.
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        // When user presses the back button on their device, display confirmation dialog. Once user confirms that he/she wants to exit, exit the application.
        if(!this.isExitConfirmed())
        {
            // Setup AlertDialog and show it.
            new AlertDialog.Builder(NotificationActivity.this).setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            NotificationActivity.this.setExitConfirmed(true);

                            NotificationActivity.this.onBackPressed(); // Invoke onBackPressed() again to exit the application.
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) { }
                    }).create().show();
        }
        else
        {
            super.onBackPressed();

            // Apply exit animation.
            this.overridePendingTransition(R.anim.activity_exit_animation_in, R.anim.activity_exit_animation_out);
        }
    }

    public boolean isExitConfirmed()
    {
        return this.isExitConfirmed;
    }

    public void setExitConfirmed(boolean isExitConfirmed)
    {
        this.isExitConfirmed = isExitConfirmed;
    }

    /*********************************** NAVIGATION METHODS ***********************************/
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Synchronize the indicator with the state of DrawerLayout by calling syncState() from onPostCreate method.
        this.getApplicationDrawerNavigationManager().getActionBarDrawerToggle().syncState();
    }

    // Setter and getter for applicationDrawerNavigationManager property.
    private void setApplicationDrawerNavigationManager(ApplicationDrawerNavigationManager applicationDrawerNavigationManager)
    {
        this.applicationDrawerNavigationManager = applicationDrawerNavigationManager;
    }

    private ApplicationDrawerNavigationManager getApplicationDrawerNavigationManager()
    {
        return this.applicationDrawerNavigationManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // This listener listens for menu selection on action bar.
    {
        Intent explicitIntent;

        switch (item.getItemId()) // Determine which menu and menu item is selected.
        {
            case R.id.menu_item_location:
                explicitIntent = new Intent(this, LocationActivity.class); // If right side menu Location menu item is selected, start LocationActivity activity.

                this.startActivity(explicitIntent);

                this.finish();

                return true;

            case R.id.menu_item_preferences:
                explicitIntent = new Intent(this, UserPreferenceActivity.class); // If right side menu Preferences menu item is selected, start UserPreferenceActivity activity.

                this.startActivity(explicitIntent);

                this.finish();

                return true;

            default:
                // When action bar hamburger menu item is selected (Left side), pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event.
                return (this.getApplicationDrawerNavigationManager().getActionBarDrawerToggle().onOptionsItemSelected(item) && super.onOptionsItemSelected(item));
        }

    }

    /*********************************** END OF NAVIGATION METHODS ***********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) // This event listener only called once, when the menu is first displayed, and it is used to initialize Activity's standard options menu.
    {
        // Inflate menu defined in app_menu.xml
        this.getMenuInflater().inflate(R.menu.app_menu_notifications, menu);

        return true;
    }
}
