package com.vk.android.mobileweatherguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;

// ApplicationDrawerNavigationManager class represents a manager that manages drawer related operations
public class ApplicationDrawerNavigationManager
{
    private Activity currentActivity;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle; // ActionBarDrawerToggle allows to connect DrawerLayout and ActionBar.
    private ActionBar actionBar;

    public ApplicationDrawerNavigationManager(Activity currentActivity, ActionBar actionBar)
    {
        this.setCurrentActivity(currentActivity);

        this.setActionBar(actionBar);

        this.setDrawer();
    }

    private void setDrawer()
    {
        this.setDrawerLayout((DrawerLayout) this.getCurrentActivity().findViewById(R.id.drawer_layout)); // Get reference of drawer layout.

        this.setDrawerListView((ListView) this.getCurrentActivity().findViewById(R.id.drawer_list_view)); // Get reference of drawer list view.

        // Get drawer items from drawer_items.xml string arrays.
        ArrayList<String> drawerItems = new ArrayList<String>(Arrays.asList(this.getCurrentActivity().getResources().getStringArray(R.array.drawer_items)));

        // Remove drawer item from ListView for the current activity (In other words, do not display current activity in the menu).
        if (this.getCurrentActivity() instanceof MainActivity)
        {
            drawerItems.remove(this.getCurrentActivity().getResources().getString(R.string.drawer_current_weather));
        }

        // TODO: Include remaining activities ^


        this.getDrawerListView().setAdapter(new ArrayAdapter<>(this.getCurrentActivity(), R.layout.support_simple_spinner_dropdown_item, drawerItems)); // Set adapter for drawer list view with the list of drawer items.

        this.getDrawerLayout().setScrimColor(this.getCurrentActivity().getColor(R.color.colorNightShade)); // Set drawer overlay color.

        // Handle drawer item selection.
        this.getDrawerListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Set actions for each of the items on the menu
                if(parent.getItemAtPosition(position).toString().equals(getCurrentActivity().getResources().getString(R.string.drawer_current_weather)))
                {
                    // Allow drawer navigation to close before starting new activity.
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Intent explicitIntent = new Intent(getCurrentActivity(), MainActivity.class);

                            getCurrentActivity().startActivity(explicitIntent);
                        }
                    }, 235);
                }

                // TODO: Include remaining activities ^

                // Close drawer navigation.
                getDrawerLayout().closeDrawer(getDrawerListView());
            }
        });

        // Set ActionBarDrawerToggle.
        this.setActionBarDrawerToggle(new ActionBarDrawerToggle(this.getCurrentActivity(), this.getDrawerLayout(), R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerOpened(View view)
            {
                super.onDrawerOpened(view);

                // When drawer opens, change action bar title to "Main Menu".
                getActionBar().setTitle(getCurrentActivity().getResources().getString(R.string.drawer_main_menu));
            }

            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);

                // When drawer closes, change action bar title to the title of the current activity.
                getActionBar().setTitle(getCurrentActivity().getTitle().toString());
            }
        });

        this.getDrawerLayout().addDrawerListener(this.getActionBarDrawerToggle()); // Add ActionBarDrawerToggle to the list of listeners.

        this.getActionBar().setDisplayHomeAsUpEnabled(true); // Enable left-facing caret on the left side of activity bar and when pressed, activity receives a call to onOptionsItemSelected().
    }


    // Setters and getters for ApplicationDrawerNavigationManager properties.
    private Activity getCurrentActivity()
    {
        return this.currentActivity;
    }

    private void setCurrentActivity(Activity currentActivity)
    {
        this.currentActivity = currentActivity;
    }

    private DrawerLayout getDrawerLayout()
    {
        return this.drawerLayout;
    }

    private void setDrawerLayout(DrawerLayout drawerlayout)
    {
        this.drawerLayout = drawerlayout;
    }

    private ListView getDrawerListView()
    {
        return this.drawerListView;
    }

    private void setDrawerListView(ListView drawerListView)
    {
        this.drawerListView = drawerListView;
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle()
    {
        return this.actionBarDrawerToggle;
    }

    private void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle)
    {
        this.actionBarDrawerToggle = actionBarDrawerToggle;
    }

    private ActionBar getActionBar()
    {
        return this.actionBar;
    }

    private void setActionBar(ActionBar actionBar)
    {
        this.actionBar = actionBar;
    }
}
