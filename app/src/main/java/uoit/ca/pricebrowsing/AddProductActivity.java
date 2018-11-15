package uoit.ca.pricebrowsing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddProductActivity extends AppCompatActivity {

    ProductDBHelper productDB;
    Intent browseProductsIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        productDB = new ProductDBHelper(this,null, null, 1);
    }

    public void save(View view)
    {
        EditText productName = (EditText) findViewById(R.id.productNameTxt);
        EditText productDesc = (EditText) findViewById(R.id.productDescriptionTxt);
        EditText productPrice = (EditText) findViewById(R.id.productPriceTxt);

        Product product = new Product(productName.getText().toString(),productDesc.getText().toString(),Double.parseDouble(productPrice.getText().toString()));
        productDB.addRecord(product);
        setResult(RESULT_OK,browseProductsIntent);
        finish();
    }

    public void cancel(View v)
    {
        setResult(RESULT_CANCELED,browseProductsIntent);
        finish();
    }
}
