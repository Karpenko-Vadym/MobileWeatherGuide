package com.vk.android.mobileweatherguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Apply enter animation.
        this.overridePendingTransition(R.anim.activity_enter_animation_in, R.anim.activity_enter_animation_out);

        setContentView(R.layout.activity_error);

        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide(); // Hide action bar.
        }

        // Set navigation bar (Located at the bottom) color.
        this.getWindow().setNavigationBarColor(this.getResources().getColor(R.color.colorTeal, null));

        String message = "Additional Information: " + (getIntent().hasExtra("message") ? getIntent().getStringExtra("message") : this.getString(R.string.error_details_not_available));

        ((TextView) this.findViewById(R.id.error_details)).setText(message);
    }

    @Override
    public void onBackPressed() // This listener listens for navigation bar back button.
    {
        super.onBackPressed();

        // Apply exit animation.
        this.overridePendingTransition(R.anim.activity_exit_animation_in, R.anim.activity_exit_animation_out);
    }
}
