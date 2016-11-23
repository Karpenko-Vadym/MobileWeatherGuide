package com.vk.android.mobileweatherguide;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// https://developer.android.com/training/volley/requestqueue.html
public class RequestQueueSingleton // RequestQueueSingleton class encapsulates RequestQueue and other Volley functionality.
{
    private static RequestQueueSingleton requestQueueSingleton; // TODO: Verify warning about context leak.
    private RequestQueue requestQueue;
    private static Context context;

    private RequestQueueSingleton(Context context)
    {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    // Getter and setter for RequestQueueSingleton class.
    public static synchronized RequestQueueSingleton getInstance(Context context)
    {
        if(requestQueueSingleton == null)
        {
            requestQueueSingleton = new RequestQueueSingleton(context);
        }

        return requestQueueSingleton;
    }

    public RequestQueue getRequestQueue()
    {
        if(this.requestQueue == null)
        {
            this.requestQueue = Volley.newRequestQueue(context.getApplicationContext()); // Instantiating RequestQueue with application context in order to make it last for life time of the application.
        }

        return this.requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue)
    {
        this.requestQueue = requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) // Add request to request queue
    {
        this.getRequestQueue().add(request);
    }
}
