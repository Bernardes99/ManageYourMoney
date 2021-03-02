package com.example.manageyourmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    private static final String DATABASE_NAME = "manage_database.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    static final String USER_ID = "userid";
    static final String USERNAME = "username";
    static final String NAME = "name";
    static final String SURNAME = "surname";
    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    static final String TABLE_ONE = "users";


    // Create table SQL instructions
    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE " +  TABLE_ONE + " (" +
                    USER_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                    USERNAME + " TEXT, " +
                    NAME + " TEXT, " +
                    SURNAME + " TEXT, " +
                    EMAIL + " TEXT, " +
                    PASSWORD + " TEXT);";

    // Cards table
    static final String CARD_ID = "cardid";
    static final String TYPE = "type";
    // static final String USER_ID = "userid";
    static final String CARD_NAME = "cardname";
    static final String CARD_BALANCE = "cardbalance";
    static final String TABLE_TWO = "cards";


    // Create table SQL instructions
    // Type true -> card, Type false -> hand money
    private static final String CARDS_TABLE_CREATE =
            "CREATE TABLE " +  TABLE_TWO + " (" +
                    CARD_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                    TYPE + " BOOLEAN, " +
                    CARD_BALANCE + " FLOAT, " +
                    USER_ID + " INTEGER, " +
                    CARD_NAME + " TEXT);";


    // Earnings
    static final String EARNINGS_ID = "earningsid";
    //static final String CARD_ID = "cardid";
    static final String QUANTITY = "quantity";
    static final String CATEGORY = "category";
    static final String DESCRIPTION = "description";
    static final String DATE = "date";
    static final String TABLE_THREE = "earnings";


    // Create table SQL instructions
    private static final String EARNINGS_TABLE_CREATE =
            "CREATE TABLE " +  TABLE_THREE + " (" +
                    EARNINGS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                    CARD_ID + " INTEGER, " +
                    QUANTITY + " FLOAT, " +
                    CATEGORY + " TEXT, " +
                    DESCRIPTION + " TEXT, " +
                    DATE + " TEXT);";

    // Expenses
    static final String EXPENSES_ID = "expensesid";
    //static final String CARD_ID = "cardid";
    //static final String QUANTITY = "quantity";
    //static final String CATEGORY = "category";
    //static final String DESCRIPTION = "description";
    //static final String DATE = "date";
    static final String TABLE_FOUR = "expenses";


    // Create table SQL instructions
    private static final String EXPENSES_TABLE_CREATE =
            "CREATE TABLE " +  TABLE_FOUR + " (" +
                    EXPENSES_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                    CARD_ID + " INTEGER, " +
                    QUANTITY + " FLOAT, " +
                    CATEGORY + " TEXT, " +
                    DESCRIPTION + " TEXT, " +
                    DATE + " TEXT);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE); //Create users table
        db.execSQL(CARDS_TABLE_CREATE); //Create cards table
        db.execSQL(EARNINGS_TABLE_CREATE); //Create earnings table
        db.execSQL(EXPENSES_TABLE_CREATE); //Create expenses table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // ----- Regist User -----
    // Returns true if the username is already taken
    public boolean check_username(String test_username) {

        boolean exists_username=false;


        String querySQL = "SELECT "+ USERNAME + " FROM " + TABLE_ONE + " WHERE " + USERNAME + " = '" + test_username + "';";

        db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querySQL, null);

        // add each row to the notes titles IDs list
        if (cursor.moveToFirst()) {
            exists_username=true;
        }

        cursor.close();

        return exists_username;
    }

    // Returns true if the email is already used
    public boolean check_email(String test_email) {

        boolean exists_email=false;


        String querySQL = "SELECT "+ EMAIL + " FROM " + TABLE_ONE + " WHERE " + EMAIL + " = '" + test_email + "';";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            exists_email=true;
        }

        cursor.close();

        return exists_email;
    }

    public void add_user(String username, String name, String surname, String email, String password) {


        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        int user_ID=generate_user_ID();

        cv.put(USER_ID, user_ID);
        cv.put(USERNAME, username);
        cv.put(NAME, name);
        cv.put(SURNAME, surname);
        cv.put(EMAIL, email);
        cv.put(PASSWORD, password);

        db.insert(TABLE_ONE, null, cv);
    }

    // generates a new ID
    public int generate_user_ID(){

        String querySQL = "SELECT " + USER_ID + " FROM " + TABLE_ONE;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        int lastID=0;

        // Will keep the last value
        if (cursor.moveToFirst()) {
            do {
                lastID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        int user_ID=lastID + 1;

        cursor.close();

        return user_ID;
    }


    // ----- Login User -----
    // Returns true if the login is done
    public boolean login_user(String try_username, String try_password) {

        boolean login=false;

        String querySQL = "SELECT "+ USERNAME + " FROM " + TABLE_ONE + " WHERE " + USERNAME + " = '" + try_username + "' AND " + PASSWORD + " = '" + try_password + "';";

        db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            login=true;
        }

        cursor.close();

        return login;
    }

    // Getting user ID by the username
    public int get_userID(String username) {

        int ID=0;

        String querySQL = "SELECT "+ USER_ID + " FROM " + TABLE_ONE + " WHERE " + USERNAME + " = '" + username + "';";

        db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            do {
                ID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return ID;
    }

    // ----- Adding the cards and hand money -----

    // new card or hand money (card_type=true -> card ; card_type=false -> hand money/wallet
    public void new_card(int user_ID, boolean card_type, String new_card_name)
    {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int card_ID=generate_card_ID();

        cv.put(CARD_ID, card_ID);
        cv.put(TYPE, card_type);
        cv.put(USER_ID, user_ID);
        cv.put(CARD_NAME, new_card_name);

        db.insert(TABLE_TWO, null, cv);
    }

    public int generate_card_ID(){

        String querySQL = "SELECT " + CARD_ID + " FROM " + TABLE_TWO;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        int lastID=0;

        // Will keep the last value
        if (cursor.moveToFirst()) {
            do {
                lastID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        int card_ID=lastID + 1;

        cursor.close();

        return card_ID;
    }


    // ----- Getting Cards -----
    public ArrayList<CardObject> getListCards(int user_ID) {

        ArrayList<CardObject> CardObjectsList = new ArrayList<CardObject>();

        String querySQL = "SELECT * FROM " + TABLE_TWO + " WHERE " + USER_ID + " = " + user_ID + " ORDER BY " + USER_ID + ";";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        // adding the cards objects to the list
        if (cursor.moveToFirst()) {
            do {
                String cardName = cursor.getString(cursor.getColumnIndex(CARD_NAME));
                int type = cursor.getInt(cursor.getColumnIndex(TYPE));
                boolean type_b=true;
                if (type==0){
                    type_b=false;
                }
                int cardID= cursor.getInt(cursor.getColumnIndex(CARD_ID));

                double balance = cursor.getFloat(cursor.getColumnIndex(CARD_BALANCE));

                CardObject new_cardObject = new CardObject(cardID, type_b, user_ID, cardName, balance);

                CardObjectsList.add(new_cardObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return CardObjectsList;
    }

    public ArrayList<TransactionObject> getListIncomes(int card_ID) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<TransactionObject> AllIncomesList = new ArrayList<TransactionObject>();

        String querySQL = "SELECT * FROM " + TABLE_THREE + " WHERE " + CARD_ID + " = " + card_ID + ";";
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
                String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                int id = cursor.getInt(cursor.getColumnIndex(EARNINGS_ID));
                double quantity = cursor.getFloat(cursor.getColumnIndex(QUANTITY));
                Date date = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(DATE)));

                TransactionObject new_TransactionObject = new TransactionObject(id, category, description, quantity, date);

                AllIncomesList.add(new_TransactionObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return AllIncomesList;
    }

    public  ArrayList<TransactionObject> getIncomes_Statistics (int card_ID) {
        String querySQL = "SELECT " + CATEGORY + ", SUM(" + QUANTITY + ") FROM " + TABLE_THREE + " WHERE " + CARD_ID + " = " + card_ID + " GROUP BY " + CATEGORY + ";";
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        ArrayList<TransactionObject> income_stats = new ArrayList<TransactionObject>();

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
                double quantity_sum = cursor.getFloat(cursor.getColumnIndex("SUM(" + QUANTITY + ")"));
                DecimalFormat df = new DecimalFormat("0.00");
                df.format(quantity_sum);
                TransactionObject new_TransactionObject = new TransactionObject(category, quantity_sum);
                income_stats.add(new_TransactionObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return income_stats;

    }

    public ArrayList<TransactionObject> getListExpenses(int card_ID) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<TransactionObject> AllExpensesList = new ArrayList<TransactionObject>();

        String querySQL = "SELECT * FROM " + TABLE_FOUR + " WHERE " + CARD_ID + " = " + card_ID + ";";
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
                String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                int id = cursor.getInt(cursor.getColumnIndex(EXPENSES_ID));
                double quantity = cursor.getFloat(cursor.getColumnIndex(QUANTITY));
                Date date = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(DATE)));

                TransactionObject new_TransactionObject = new TransactionObject(id, category, description, quantity, date);

                AllExpensesList.add(new_TransactionObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return AllExpensesList;
    }

    public  ArrayList<TransactionObject> getExpenses_Statistics (int card_ID) {
        String querySQL = "SELECT " + CATEGORY + ", SUM(" + QUANTITY + ") FROM " + TABLE_FOUR + " WHERE " + CARD_ID + " = " + card_ID + " GROUP BY " + CATEGORY + ";";
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        ArrayList<TransactionObject> expense_stats = new ArrayList<TransactionObject>();

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
                double quantity_sum = cursor.getFloat(cursor.getColumnIndex("SUM(" + QUANTITY + ")"));
                DecimalFormat df = new DecimalFormat("0.00");
                df.format(quantity_sum);
                TransactionObject new_TransactionObject = new TransactionObject(category, quantity_sum);
                expense_stats.add(new_TransactionObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return expense_stats;

    }

    // Global user balance
    public double getUserBalance(int user_ID) {
        double totalBalance=0;
        ArrayList<CardObject> CardObjectsList = new ArrayList<CardObject>();
        CardObjectsList=getListCards(user_ID);

        for (int i=0; i<CardObjectsList.size(); i++)
        {
            totalBalance+= CardObjectsList.get(i).getBalance();
        }

        return totalBalance;
    }

    // ----- Add a transaction -----
    public void addTransaction(boolean positive_quantity, double quantity, int card_id, String category, String description, String date) {

        if (positive_quantity){
            addIncome(quantity, card_id, category, description, date);
        }
        else{
            addExpense(quantity, card_id, category, description, date);
        }
        updateCardBalance(positive_quantity, card_id, quantity);

    }

    public void updateCardBalance(boolean positive_quantity, int card_id, double quantity){

        double current_balance = getCurrentBalance(card_id);
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (!positive_quantity){
            quantity=-quantity; // despesas entram como valor negativo no balanço
        }

        double update_balance = current_balance + quantity;

        cv.put(CARD_BALANCE, update_balance);
        db.update(TABLE_TWO, cv, CARD_ID + " = " + card_id, null);

    }

    public double getCurrentBalance(int card_id){

        String querySQL = "SELECT " + CARD_BALANCE + " FROM " + TABLE_TWO + " WHERE " + CARD_ID + " = " + card_id + ";";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        double currentBalance=0;

        // Will keep the last value, in this case the only result
        if (cursor.moveToFirst()) {
            do {
                currentBalance = cursor.getFloat(cursor.getColumnIndex(CARD_BALANCE));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return currentBalance;
    }

    public void addIncome(double quantity, int card_id, String category, String description, String date){

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int earning_ID = generate_earning_ID();

        cv.put(EARNINGS_ID, earning_ID);
        cv.put(CARD_ID, card_id);
        cv.put(QUANTITY, quantity);
        cv.put(CATEGORY, category);
        cv.put(DESCRIPTION, description);

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = simpleDateFormat.parse("2018-10-18");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        //Date in dd-MM-yyyy
        cv.put(DATE, date);

        //Date data = new Date();
        //SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy");
        //String today = formatador.format(data);
        //cv.put(DATE, today);

        db.insert(TABLE_THREE, null, cv);

    }

    public void addExpense(double quantity, int card_id, String category, String description, String date){

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int expense_ID = generate_expense_ID();

        cv.put(EXPENSES_ID, expense_ID);
        cv.put(CARD_ID, card_id);
        cv.put(QUANTITY, quantity);
        cv.put(CATEGORY, category);
        cv.put(DESCRIPTION, description);

        //Date in dd-MM-yyyy
        cv.put(DATE, date);

        //Date data = new Date();
        //SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy");
        //String today = formatador.format(data);
        //cv.put(DATE, today);

        db.insert(TABLE_FOUR, null, cv);
    }

    public int generate_earning_ID(){

        String querySQL = "SELECT " + EARNINGS_ID + " FROM " + TABLE_THREE;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        int lastID=0;

        // Will keep the last value
        if (cursor.moveToFirst()) {
            do {
                lastID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        int earning_ID=lastID + 1;

        cursor.close();

        return earning_ID;
    }

    public int generate_expense_ID(){

        String querySQL = "SELECT " + EXPENSES_ID + " FROM " + TABLE_FOUR;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        int lastID=0;

        // Will keep the last value
        if (cursor.moveToFirst()) {
            do {
                lastID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        int expense_ID=lastID + 1;

        cursor.close();

        return expense_ID;
    }

    public void delete_card(int card_ID){

        db = this.getWritableDatabase();

        db.delete(TABLE_TWO, CARD_ID + " = " + card_ID + ";", null);
        db.delete(TABLE_THREE, CARD_ID + " = " + card_ID + ";", null);
        db.delete(TABLE_FOUR, CARD_ID + " = " + card_ID + ";", null);

    }

    public void change_card_name(int card_id, String new_card_name){

        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(CARD_NAME, new_card_name);
        db.update(TABLE_TWO, cv, CARD_ID + " = " + card_id, null);

    }

    public UserObject get_user(int user_id){

        String querySQL = "SELECT * FROM " + TABLE_ONE + " WHERE " + USER_ID + " = " + user_id + ";";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        String username="";
        String name="";
        String surname="";
        String email="";
        String password="";

        if (cursor.moveToFirst()) {
            do {
                username=cursor.getString(cursor.getColumnIndex(USERNAME));
                name=cursor.getString(cursor.getColumnIndex(NAME));
                surname=cursor.getString(cursor.getColumnIndex(SURNAME));
                email=cursor.getString(cursor.getColumnIndex(EMAIL));
                password=cursor.getString(cursor.getColumnIndex(PASSWORD));
            } while (cursor.moveToNext());
        }

        UserObject new_userObject = new UserObject(user_id, username, name, surname, email, password);

        cursor.close();

        return new_userObject;
    }


    // ----- Edit User -----
    // different from register
    // Returns true if the username is already taken
    public boolean check_username_change(String test_username, int user_id) {

        boolean exists_username=false;


        String querySQL = "SELECT "+ USERNAME + " FROM " + TABLE_ONE + " WHERE " + USERNAME + " = '" + test_username + "' AND " + USER_ID + " != " + user_id +";";

        db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querySQL, null);

        // add each row to the notes titles IDs list
        if (cursor.moveToFirst()) {
            exists_username=true;
        }

        cursor.close();

        return exists_username;
    }

    // Returns true if the email is already used
    public boolean check_email_change(String test_email, int user_id) {

        boolean exists_email=false;

        String querySQL = "SELECT "+ EMAIL + " FROM " + TABLE_ONE + " WHERE " + EMAIL + " = '" + test_email + "' AND " + USER_ID + " != " + user_id + ";";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);

        if (cursor.moveToFirst()) {
            exists_email=true;
        }

        cursor.close();

        return exists_email;
    }

    public void update_user_info(int user_id, String new_username, String new_name, String new_surname, String new_email){

        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(USERNAME, new_username);
        cv.put(NAME, new_name);
        cv.put(SURNAME, new_surname);
        cv.put(EMAIL, new_email);
        db.update(TABLE_ONE, cv, USER_ID + " = " + user_id, null);

    }

    public void change_password(int user_id, String new_password){

        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(PASSWORD, new_password);
        db.update(TABLE_ONE, cv, USER_ID + " = " + user_id, null);

    }
}
