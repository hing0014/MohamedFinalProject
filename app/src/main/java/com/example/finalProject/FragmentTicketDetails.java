package com.example.finalProject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Uses the passed in data to populate the previous Views Frame layout with a new fragment_ticket_details.
 *  <p>
 * Course Name: CST8288_010
 * Class name: FragmentTicketDetails
 * Date: November 23, 2020
 *
 * @version 1.0
 * @author Chris HIng
 */
public class FragmentTicketDetails extends Fragment
{
    private AppCompatActivity parentActivity;

    public final static String ITEM_CITY = "CITY";
    public final static String ITEM_NAME = "EVENT NAME";
    public final static String ITEM_START_DATE = "START DATE";
    public final static String ITEM_MIN_PRICE = "MIN PRICE";
    public final static String ITEM_MAX_PRICE = "MAX PRICE";
    public final static String ITEM_URL = "URL";
    public final static String ITEM_IMAGE_STRING = "IMAGE";
    Bitmap image;
    String city;
    String eventName;
    String startDate;
    double ticketPriceMin;
    double ticketPriceMax;
    String eventUrl;

    /**
     * Manages the fragment data.
     * <p>
     * Inflates the fragment_ticket_details into the passed in view.
     * The Bundle passed in hold the data required to populate the Views.
     *
     * @param inflater The LayoutInflater used to get the View for modification.
     * @param container The LayoutInflater used to get the View for modification.
     * @param savedInstanceState The Bundle of data used to update the views.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle dataToSet = getArguments();
        View result =  inflater.inflate(R.layout.fragment_ticket_details, container, false);

        ImageView imagev = result.findViewById(R.id.eventImage);
        TextView cityv = result.findViewById(R.id.city);
        TextView namev = result.findViewById(R.id.name);
        TextView startDatev = result.findViewById(R.id.startDate);
        TextView ticketPriceMinv = result.findViewById(R.id.ticketPriceMin);
        TextView ticketPriceMaxv = result.findViewById(R.id.ticketPriceMax);

        image = TicketMaster.decodeBase64(dataToSet.getString(ITEM_IMAGE_STRING));
        city = dataToSet.getString(ITEM_CITY);
        eventName = dataToSet.getString(ITEM_NAME);
        startDate = dataToSet.getString(ITEM_START_DATE);
        ticketPriceMin = dataToSet.getDouble(ITEM_MIN_PRICE);
        ticketPriceMax = dataToSet.getDouble(ITEM_MAX_PRICE);
        eventUrl = dataToSet.getString(ITEM_URL);

        imagev.setImageBitmap(image);
        cityv.setText(city);
        namev.setText(eventName);
        startDatev.setText(startDate);
        ticketPriceMinv.setText(String.valueOf(ticketPriceMin));
        ticketPriceMaxv.setText(String.valueOf(ticketPriceMax));

        Button back = result.findViewById(R.id.back);
        back.setOnClickListener( clk ->
        {
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            if(!TicketMaster.isTablet)
            {
                parentActivity.onBackPressed();
            }
        });

        Button saveToFavorites = result.findViewById(R.id.saveToFavorites);
        saveToFavorites.setOnClickListener( clk ->
        {
            SQLiteDatabase dataBase = TicketMaster.getDatabase();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(TicketMasterOpener.COL_CITY, city);
            newRowValues.put(TicketMasterOpener.COL_EVENT_NAME, eventName);
            newRowValues.put(TicketMasterOpener.COL_START_DATE, startDate);
            newRowValues.put(TicketMasterOpener.COL_MIN_PRICE, ticketPriceMin);
            newRowValues.put(TicketMasterOpener.COL_MAX_PRICE, ticketPriceMax);
            newRowValues.put(TicketMasterOpener.COL_IMAGE_STRING, TicketMaster.encodeTobase64(image));
            newRowValues.put(TicketMasterOpener.COL_URL, eventUrl);
            dataBase.insert(TicketMasterOpener.TABLE_NAME, null, newRowValues);
            Toast.makeText(parentActivity.getApplicationContext(),R.string.added, Toast.LENGTH_SHORT).show();
        });


        Button urlButton = result.findViewById(R.id.urlButton);
        urlButton.setOnClickListener( clk ->
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
            alertDialogBuilder.setTitle(getResources().getString(R.string.urlChoice))
                    .setPositiveButton(getResources().getString(R.string.yes), (click, arg) ->
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl));
                        startActivity(browserIntent);
                    }).setNegativeButton("No", (click, arg) -> {  }).create().show();
        });
        return result;
    }
    /**
     * Defines the AppCompatActivity
     * <p>
     * Inflates the fragment_ticket_details into the passed in view.
     * The Bundle passed in hold the data required to populate the Views.
     *
     * @param context The Context used to define where the data came from.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
