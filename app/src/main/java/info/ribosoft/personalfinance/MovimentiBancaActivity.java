package info.ribosoft.personalfinance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.ribosoft.personalfinance.GestioneDBLista.ClassFunzioni;
import info.ribosoft.personalfinance.GestioneDBLista.ClassListaMovimenti;
import info.ribosoft.personalfinance.GestioneDBLista.DBDatiBanca;
import info.ribosoft.personalfinance.GestioneDBLista.DBHelper;
import info.ribosoft.personalfinance.GestioneDBLista.MyAdapterListaMovimenti;

public class MovimentiBancaActivity extends AppCompatActivity {
    MyAdapterListaMovimenti myAdapterListaMovimenti;
    TextView txtMovNome;
    String strIdBanca;
    boolean blnFiltro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimenti_banca);

        ClassFunzioni cFunzioni = new ClassFunzioni();

        // read the passed parameter
        Intent intent = getIntent();
        strIdBanca = intent.getStringExtra("id");

        DBHelper myDBBanche = new DBHelper(this);
        DBDatiBanca myDBDatiBanca = myDBBanche.leggiSaldoBanca(strIdBanca);

        txtMovNome = findViewById(R.id.txtMovNome);
        txtMovNome.setText(myDBDatiBanca.getNomeBanca());

        // calculate bank balance
        double dblSaldo = Double.parseDouble(myDBDatiBanca.getSaldoEntrata()) -
            Double.parseDouble(myDBDatiBanca.getSaldoUscita());

        TextView txtMovSaldo = findViewById(R.id.txtMovSaldo);
        Context context = getApplicationContext();
        // colors the amount green if entering, red if leaving
        if (dblSaldo < 0) {
            txtMovSaldo.setTextColor(ContextCompat.getColor(context,R.color.colorUscite));
        } else {
            txtMovSaldo.setTextColor(ContextCompat.getColor(context, R.color.colorEntrate));
        }

        // view formatted amount
        txtMovSaldo.setText(cFunzioni.strImporto(dblSaldo));

        ImageButton btnMovFiltra = findViewById(R.id.btnMovFiltra);
        // displays or hides the bar where you can select the start and end date
        btnMovFiltra.setOnClickListener(v -> visualizzaNascondiFiltro());

        TextView txtMovDataInizio = findViewById(R.id.txtMovDataInizio);
        ImageButton btnMovDataInizio = findViewById(R.id.btnMovDataInizio);
        // select start date
        btnMovDataInizio.setOnClickListener(v ->
            cFunzioni.modificaData(MovimentiBancaActivity.this, txtMovDataInizio));

        TextView txtMovDataFine = findViewById(R.id.txtMovDataFine);
        ImageButton btnMovDataFine = findViewById(R.id.btnMovDataFine);
        // select end date
        btnMovDataFine.setOnClickListener(v ->
            cFunzioni.modificaData(MovimentiBancaActivity.this, txtMovDataFine));

        // apply the filter to view transactions that fall between the two dates
        ImageButton btnMovApplicaFiltro = findViewById(R.id.btnMovApplicaFiltro);
        btnMovApplicaFiltro.setOnClickListener(v -> {
            boolean flgControllo = true;
            // check that the starting date has been selected
            if (txtMovDataInizio.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                    "Manca la data d'inizio", Toast.LENGTH_LONG).show();
                flgControllo = false;
            }
            // verify that the final date has been selected
            if (txtMovDataFine.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                    "Manca la data di fine", Toast.LENGTH_LONG).show();
                flgControllo = false;
            }
            // verify the correctness of the two dates
            if (flgControllo) {
                String strInizio = cFunzioni.filtroData(txtMovDataInizio.getText().toString());
                String strFine = cFunzioni.filtroData(txtMovDataFine.getText().toString());
                int r = strInizio.compareTo(strFine);
                // reports if the end date is less than the start date
                if (r > 0) Toast.makeText(getApplicationContext(),
                    "La data d'inizio è inferiore della data di fine",Toast.LENGTH_LONG).show();
                else {
                    // the data entered is correct so it hides the row for data input
                    visualizzaNascondiFiltro();
                    // view the movements
                    listaMovimenti(true, strInizio, strFine);
                }
            }
        });

        ListView lstListaMovimenti = findViewById(R.id.lstMovListaMovimenti);
        // read the movements related to a bank
        listaMovimenti(false, "", "");

        // activate listening on the list
        lstListaMovimenti.setOnItemClickListener((parent, v, i, l) -> {
            // starts the activity to view the movements of the selected bank
            Intent intent1 = new  Intent(getApplicationContext(), GestisciMovimentoActivity.class);
            String strIdMovimento = myAdapterListaMovimenti.leggiIdMovimento(i);
            intent1.putExtra("idMov", strIdMovimento);
            intent1.putExtra("idBanca", strIdBanca);
            startActivity(intent1);
        });
    }

    // defines the menu items
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movbanca_menu, menu);
        return true;
    }

    // reads the menu item selected by the user
    public boolean onOptionsItemSelected(MenuItem mItem) {
        Intent intent;

        int id = mItem.getItemId();
        if (id == R.id.movbanca_NewMovimento) { //insert new movement
            intent = new Intent(getApplicationContext(), NuovoMovimentoActivity.class);
            intent.putExtra("id", strIdBanca);
            intent.putExtra("nomeBanca", txtMovNome.getText());
            startActivity(intent);
        } else if (id == R.id.movbanca_ListaBanche) { // returns to the main activity
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.movbanca_about) { // view app information
            InfoApp infoApp = new InfoApp();
            infoApp.OnCreate(this);
        }
        return false;
    }

    // read the movements related to a bank
    private void listaMovimenti(boolean blnFiltro, String strInizio, String strFine) {
        ListView lstListaMovimenti = findViewById(R.id.lstMovListaMovimenti);
        DBHelper myDBMovimenti = new DBHelper(this);

        // fills the array with the data read from the table
        ArrayList<ClassListaMovimenti> arrayListMovimenti =
            myDBMovimenti.leggiMovimenti(strIdBanca, blnFiltro, strInizio, strFine);
        // initializes the adapter with the data from the array
        myAdapterListaMovimenti = new MyAdapterListaMovimenti(this, arrayListMovimenti);
        // associate the adapter with a listview
        lstListaMovimenti.setAdapter(myAdapterListaMovimenti);
    }

    // view or hide the row for entering the start and end dates
    private void visualizzaNascondiFiltro() {
        LinearLayout lnlFiltro = findViewById(R.id.lnlFiltro);
        blnFiltro = !blnFiltro;
        if (blnFiltro) {
            lnlFiltro.setVisibility(View.VISIBLE);
        } else {
            lnlFiltro.setVisibility(View.GONE);
        }
    }
}