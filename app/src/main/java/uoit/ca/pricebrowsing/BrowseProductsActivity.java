package uoit.ca.pricebrowsing;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BrowseProductsActivity extends AppCompatActivity {

    ProductDBHelper productDB;
    List<Product> allProducts;
    int global_index=0;
    int global_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_products);
        productDB= new ProductDBHelper(this,null, null,1);
        allProducts = new ArrayList<Product>();
        allProducts =productDB.getAllProducts();
        global_count=allProducts.size();

        //Load UI with first product, adjust UI according to number of products ( if there are any)

        if(global_count>0)
        {
            displayProducts(allProducts.get(global_index));
            new convertToBitcoin().execute(String.valueOf(allProducts.get(global_index).getProductPrice()));
            Button previous = (Button) findViewById(R.id.previousBtn);
            previous.setEnabled(false);
        }
        if (global_count==0 || global_count == 1)
        {
            Button next = (Button) findViewById(R.id.nextBtn);
            Button previous = (Button) findViewById(R.id.previousBtn);
            next.setEnabled(false);
            previous.setEnabled(false);
        }
    }

    //Enabling the options menu for add product
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.product_menu,menu);
        return true;
    }

    //Handling add product option
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()){
            case R.id.add_Product:
                Intent addProductIntent = new Intent(this,AddProductActivity.class);
                startActivityForResult(addProductIntent, 13);
        }
        return true;
    }

    //Reloading list of products when a new product is added
    public void onActivityResult(int reqCode, int resCode, Intent result)
    {
        super.onActivityResult(reqCode,resCode,result);
        if(resCode==RESULT_OK)
        {
            allProducts = new ArrayList<Product>();
            allProducts =productDB.getAllProducts();
            global_count=allProducts.size();
            if(global_count>0)
            {
                displayProducts(allProducts.get(global_index));
                new convertToBitcoin().execute(String.valueOf(allProducts.get(global_index).getProductPrice()));
            }
            if(global_count>=2)
            {
                Button next = (Button) findViewById(R.id.nextBtn);
                next.setEnabled(true);
            }
        }
    }

    //Next button handler, always re-enables previous button when clicked, and is disabled if it's the last product
    public void getNext(View v) throws IOException
    {

        if(global_count!=0 && global_index+1<global_count)
        {
            global_index++;
            displayProducts(allProducts.get(global_index));
            new convertToBitcoin().execute(String.valueOf(allProducts.get(global_index).getProductPrice()));
        }

        Button next = (Button) findViewById(R.id.nextBtn);
        Button previous = (Button) findViewById(R.id.previousBtn);
        previous.setEnabled(true);
        if(global_index+1==global_count)
        {
            next.setEnabled(false);
        }
    }

    //Previous button handler, always re-enables next button when clicked, and is disabled if it's the last product
    public void getPrevious(View v)
    {
        if(global_count!=0 && global_index-1>-1)
        {
            global_index--;
            displayProducts(allProducts.get(global_index));
            new convertToBitcoin().execute(String.valueOf(allProducts.get(global_index).getProductPrice()));
        }
        Button next = (Button) findViewById(R.id.nextBtn);
        Button previous = (Button) findViewById(R.id.previousBtn);
        next.setEnabled(true);
        if(global_index-1==-1)
        {
            previous.setEnabled(false);
        }
    }

    //Delete button handler, adjust UI according to deletion
    public void deleteProduct(View v)
    {
        TextView name = (TextView) findViewById(R.id.nameTxt);
        TextView bitcoin = (TextView)findViewById(R.id.priceBit);
        productDB.deleteRecord(name.getText().toString());
        allProducts = new ArrayList<Product>();
        allProducts =productDB.getAllProducts();
        global_count=allProducts.size();
        TextView desc = (TextView) findViewById(R.id.descTxt);
        TextView price = (TextView) findViewById(R.id.priceTxt);
        if(global_count!=0 && global_index>-1)
        {
            if(global_index!=0)
                global_index--;
            displayProducts(allProducts.get(global_index));
            new convertToBitcoin().execute(String.valueOf(allProducts.get(global_index).getProductPrice()));
        }
        else if(global_count==0)
        {
            name.setText("");
            price.setText("");
            desc.setText("");
            bitcoin.setText("");
            Button next = (Button) findViewById(R.id.nextBtn);
            Button previous = (Button) findViewById(R.id.previousBtn);
            next.setEnabled(false);
            previous.setEnabled(false);
            global_index=0;
        }
    }

    //Async task to convert the current product's price to bitcoin via webservice
    public class convertToBitcoin extends AsyncTask<String,Void,String>
    {
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            BufferedReader reader=null;
            try {
            String urlString = "https://blockchain.info/tobtc?currency=CAD&value=" + String.valueOf(strings[0]);
            URL url = new URL(urlString);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            InputStream input = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            result=reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView bitcoin = (TextView)findViewById(R.id.priceBit);
            bitcoin.setText(result);

        }
    }

    //Wrapper function to display products in their respective fields
    public void displayProducts(Product product)
    {
        TextView name = (TextView) findViewById(R.id.nameTxt);
        TextView desc = (TextView) findViewById(R.id.descTxt);
        TextView price = (TextView) findViewById(R.id.priceTxt);
        name.setText(product.getProductName());
        price.setText(String.valueOf(product.getProductPrice()));
        desc.setText(product.getProductDescription());
    }
}
