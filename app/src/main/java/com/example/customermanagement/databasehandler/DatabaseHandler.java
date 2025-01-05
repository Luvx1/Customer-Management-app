package com.example.customermanagement.databasehandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.customermanagement.model.Customer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CustomerManager";
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_SALARY = "salary";
    private static final String KEY_DATEDELTE = "date_delete";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_GENDER + " INTEGER," + KEY_AGE + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_SALARY + " TEXT," + KEY_DATEDELTE + " TEXT" + ")";
        db.execSQL(CREATE_CUSTOMERS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        // Create tables again
        onCreate(db);
    }

    public long addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, customer.get_Name());
        values.put(KEY_GENDER, customer.getGender() ? 1 : 0);
        values.put(KEY_AGE, customer.get_Age());
        values.put(KEY_ADDRESS, customer.get_Address());
        values.put(KEY_SALARY, customer.get_Salary());

        // Only add date_delete if it's not null
        if (customer.get_DelteDate() != null) {
            values.put(KEY_DATEDELTE, customer.get_DelteDate());
        } else {
            values.putNull(KEY_DATEDELTE);
        }

        long result = db.insert(TABLE_CUSTOMERS, null, values);
        db.close();
        return result;
    }

    public Customer getCustomer(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CUSTOMERS, new String[]{KEY_ID, KEY_NAME, KEY_GENDER, KEY_AGE, KEY_ADDRESS, KEY_SALARY, KEY_DATEDELTE}, KEY_ID + "=?", new String[]{id}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Customer customer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)), cursor.isNull(cursor.getColumnIndexOrThrow(KEY_DATEDELTE)) ? null : cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATEDELTE)));
            cursor.close();
            return customer;
        }
        return null;
    }


    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Customer customer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)), cursor.isNull(cursor.getColumnIndexOrThrow(KEY_DATEDELTE)) ? null : cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATEDELTE)));
                customerList.add(customer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return customerList;
    }


    public int updateCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, customer.get_Name());
        values.put(KEY_GENDER, customer.getGender() ? 1 : 0);
        values.put(KEY_AGE, customer.get_Age());
        values.put(KEY_ADDRESS, customer.get_Address());
        values.put(KEY_SALARY, customer.get_Salary());

        return db.update(TABLE_CUSTOMERS, values, KEY_ID + " = ?", new String[]{String.valueOf(customer.get_ID())});
    }


    public void deleteCustomer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(KEY_DATEDELTE, currentDateTime);

        db.update(TABLE_CUSTOMERS, values, KEY_ID + " = ?", new String[]{id});

        db.close();
    }


    public Customer getFirstCustomer() {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer firstCustomer = null;

        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_DATEDELTE + " IS NULL ORDER BY " + KEY_ID + " ASC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                firstCustomer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)));
            }
            cursor.close();
        }

        return firstCustomer;
    }

    public Customer getLastCustomer() {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer lastCustomer = null;

        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_DATEDELTE + " IS NULL ORDER BY " + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lastCustomer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)));
            }
            cursor.close();
        }

        return lastCustomer;
    }

    public Customer getPreviousCustomer(String currentCustomerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer previousCustomer = null;
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_ID + " < ? AND " + KEY_DATEDELTE + " IS NULL ORDER BY " + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentCustomerId});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                previousCustomer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)));
            }
            cursor.close();
        }

        return previousCustomer;
    }

    public Customer getNextCustomer(String currentCustomerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer nextCustomer = null;

//        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_ID + " > ? ORDER BY " + KEY_ID + " ASC LIMIT 1";
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_ID + " > ? AND " + KEY_DATEDELTE + " IS NULL ORDER BY " + KEY_ID + " ASC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentCustomerId});

        if (cursor != null && cursor.moveToFirst()) {
            nextCustomer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)));
            cursor.close();
        }

        return nextCustomer;
    }

    public int updateCustomerSalary(String customerId, String newSalary) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SALARY, newSalary);

        int result = db.update(TABLE_CUSTOMERS, values, KEY_ID + " = ?", new String[]{customerId});

        db.close();
        return result;
    }

    public ArrayList<Customer> searchCustomers(String searchText) {
        ArrayList<Customer> customerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_NAME + " LIKE ? OR " + KEY_ADDRESS + " LIKE ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + searchText + "%", "%" + searchText + "%"});

        if (cursor.moveToFirst()) {
            do {
                Customer customer = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GENDER)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SALARY)));
                customerList.add(customer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return customerList;
    }

}
