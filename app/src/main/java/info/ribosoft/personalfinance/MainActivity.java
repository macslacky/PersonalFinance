package info.ribosoft.personalfinance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import info.ribosoft.personalfinance.GestioneDBLista.DBHelper;
import info.ribosoft.personalfinance.GestioneDBLista.ClassContiBanche;
import info.ribosoft.personalfinance.GestioneDBLista.MyAdapterListaBanche;

public class MainActivity extends AppCompatActivity {
    private MyAdapterListaBanche myAdapterListaBanche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lstListaBanche = findViewById(R.id.lstListaBanche);
        // read the banks table and associate the data to a listview
        ListaBanche();

        // activate listening on the list
        lstListaBanche.setOnItemClickListener((adapterView, v, i, l) -> {
            // starts the activity to view the movements of the selected bank
            Intent intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
            // read idbanca of the selected bank by clicking
            String strIdBanca = myAdapterListaBanche.leggiIdBanca(i);
            intent.putExtra("id", strIdBanca);
            startActivity(intent);
        });
    }

    // defines the menu items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // reads the menu item selected by the user
    public boolean onOptionsItemSelected(MenuItem mItem) {
        int id = mItem.getItemId();
        if (id == R.id.main_NewBanca) { // enter new bank
            Intent intent = new Intent(getApplicationContext(), NuovaBancaActivity.class);
            startActivity(intent);
        } else if (id == R.id.main_about) { // view app information
            InfoApp infoApp = new InfoApp();
            infoApp.OnCreate(this);
        }
        return false;
    }

    // read the banks table and associate the data to a listview
    private void ListaBanche() {
        ListView lstListaBanche = findViewById(R.id.lstListaBanche);

        DBHelper myDBBanche = new DBHelper(this);

        // fills the array with the data read from the table
        ArrayList<ClassContiBanche> arrayListBanche = myDBBanche.leggiBanche();
        // initializes the adapter with the data from the array
        myAdapterListaBanche = new MyAdapterListaBanche(this, arrayListBanche);
        // associate the adapter with a listview
        lstListaBanche.setAdapter(myAdapterListaBanche);
    }
}
