package uoit.ca.pricebrowsing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";
    private static final String TABLE_NAME = "PRODUCTS";
    private static final String COL_0="PRODUCT_ID";
    private static final String COL_1 = "PRODUCT_NAME";
    private static final String COL_2 = "PRODUCT_DESCRIPTION";
    private static final String COL_3 = "PRODUCT_PRICE";


    public ProductDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //Creating DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_0 + " Integer PRIMARY KEY AUTOINCREMENT," +
                COL_1 +  " Text," +
                COL_2 + " Text, " +
                COL_3 + " Decimal(10,5)) " +
                 ";" ;

        Log.d("DBText","createTable: "+createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table " + TABLE_NAME + ";" );
        this.onCreate(db);
    }

    //Adding new record, takes a product as parameter
    public void addRecord(Product product){

        ContentValues values= new ContentValues();

        values.put(COL_1,product.getProductName());
        values.put(COL_2,product.getProductDescription());
        values.put(COL_3,product.getProductPrice());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    //Deleting record, takes name of product as parameter
    public void deleteRecord(String nameInput){

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete from " + TABLE_NAME + " where " + COL_1 + "='"  + nameInput +"';");
        db.close();
    }

    //Selects all products from DB and puts them into an ArrayList and returns it
    public List<Product> getAllProducts(){
        String result="";
        List<Product> products = new ArrayList<Product>();
        Product temp = new Product();
        SQLiteDatabase db = getWritableDatabase();
        String query =" Select * from " + TABLE_NAME + ";";

        Cursor c = db.rawQuery(query,null);

        c.moveToFirst();

        while(!(c.isAfterLast())){
            temp.setProductName(c.getString(c.getColumnIndex(COL_1)));
            temp.setProductDescription(c.getString(c.getColumnIndex(COL_2)));
            temp.setProductPrice(c.getDouble(c.getColumnIndex(COL_3)));
            products.add(temp);
            temp=new Product();
            c.moveToNext();
            /*
            result += c.getString(c.getColumnIndex(COL_1));
            result+=" - ";
            result += c.getString(c.getColumnIndex(COL_2));
            result+=" - ";
            result += String.valueOf(c.getDouble(c.getColumnIndex(COL_3)));
            result+= " \n ";
            c.moveToNext();*/

        }
        db.close();
        return products;
    }
}

