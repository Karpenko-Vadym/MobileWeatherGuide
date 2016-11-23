package com.vk.android.mobileweatherguide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

// Custom view that renders a square based on minimum dimension. Used for rendering SVG icons.
public class SquareView extends View
{
    public SquareView(Context context)
    {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Measure the view and its content to determine the measured width and the measured height.
    @Override
    public void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get dimensions of height and width.
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int side;

        // Set the side of the square to minimum dimension.
        if(width > height)
        {
            side = height;
        }
        else
        {
            side = width;
        }

        // Apply minimum dimension to the sides of the square (Rectangle with equal sides).
        this.setMeasuredDimension(side, side);

        this.invalidate();

        this.requestLayout();
    }
}
