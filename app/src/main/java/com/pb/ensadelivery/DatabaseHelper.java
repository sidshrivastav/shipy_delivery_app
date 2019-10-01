package com.pb.ensadelivery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pb.ensadelivery.models.OrderModel;

import java.util.ArrayList;
import java.util.List;

 
/**
 * Created by ravi on 15/03/18.
 */
 
public class DatabaseHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "orders_db";
 
 
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // create notes table
        db.execSQL(OrderModel.CREATE_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + "OrderDelivery");
 
        // Create tables again
        onCreate(db);
    }

    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + "OrderDelivery");
    }

    public long insertOrder(OrderModel order) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put("orderId", order.getOrderId());
        values.put("lat", order.getDeliveryLatitude());
        values.put("lang", order.getDeliveryLongitute());
        values.put("address", order.getDeliveryAddress());
        values.put("specialCode", order.getSpecialCode());

        // insert row
        long id = db.insert("OrderDelivery", null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public int getOrdersCount() {
        String countQuery = "SELECT  * FROM " + "OrderDelivery";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public OrderModel getNearbyOrder(double latitude, double longitude) {

        String selectQuery = "SELECT  * FROM " + "OrderDelivery";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String orderLat = cursor.getString(cursor.getColumnIndex("lat"));
                String orderLang = cursor.getString(cursor.getColumnIndex("lang"));

                Log.d("Order Lat: ", orderLat);
                Log.d("Order Lang: ", orderLang);

                double distance = distance(Double.valueOf(orderLat),Double.valueOf(orderLang),latitude,longitude);
                if(distance<0.5){
                    OrderModel order = new OrderModel();
                    order.setOrderId(cursor.getInt(cursor.getColumnIndex("orderId")));
                    order.setDeliveryLatitude(cursor.getString(cursor.getColumnIndex("lat")));
                    order.setDeliveryLongitute(cursor.getString(cursor.getColumnIndex("lang")));
                    order.setDeliveryAddress(cursor.getString(cursor.getColumnIndex("address")));
                    order.setSpecialCode(cursor.getString(cursor.getColumnIndex("specialCode")));

                    cursor.close();

                    return order;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return null;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}