package info.ribosoft.personalfinance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import info.ribosoft.personalfinance.GestioneDBLista.ClassFunzioni;
import info.ribosoft.personalfinance.GestioneDBLista.DBDatiBanca;
import info.ribosoft.personalfinance.GestioneDBLista.DBDatiMovimento;
import info.ribosoft.personalfinance.GestioneDBLista.DBHelper;

public class GestisciMovimentoActivity extends AppCompatActivity {
    private Intent intent;
    private String strIdMov, strIdBanca;
    private int tipoMov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_movimento);

        tipoMov = 0;

        intent = getIntent();
        // read data passed as parameters
        strIdMov = intent.getStringExtra("idMov");
        strIdBanca = intent.getStringExtra("idBanca");

        DBHelper myDBBanche = new DBHelper(this);
        // read the bank's incoming and outgoing balance
        DBDatiBanca myDBDatiBanca = myDBBanche.leggiSaldoBanca(strIdBanca);

        TextView txtEditMovBanca = findViewById(R.id.txtEditMovBanca);
        txtEditMovBanca.setText(myDBDatiBanca.getNomeBanca());
        String saldoIn = myDBDatiBanca.getSaldoEntrata();
        String saldoOut = myDBDatiBanca.getSaldoUscita();

        // read single movement
        DBDatiMovimento myDBMovimento = myDBBanche.leggiSingoloMov(strIdMov);

        ClassFunzioni cFunzioni = new ClassFunzioni();
        TextView txtEditMovData = findViewById(R.id.txtEditMovData);
        txtEditMovData.setText(myDBMovimento.getDataMovimento());
        ImageButton btnNewMovData = findViewById(R.id.btnEditMovData);
        // activate listening on the button to insert the date of the movement
        btnNewMovData.setOnClickListener(v -> cFunzioni.modificaData(
            GestisciMovimentoActivity.this, txtEditMovData));

        TextView txtEditMovValuta = findViewById(R.id.txtEditMovValuta);
        txtEditMovValuta.setText(myDBMovimento.getMovValuta());
        ImageButton btnNewMovValuta = findViewById(R.id.btnEditMovValuta);
        // activate listening on the button to insert the date of the movement
        btnNewMovValuta.setOnClickListener(v -> cFunzioni.modificaData(
            GestisciMovimentoActivity.this, txtEditMovValuta));

        EditText txtEditMovImporto = findViewById(R.id.txtEditMovImporto);

        // read the amount before any modification
        double oldImporto = Double.parseDouble(myDBMovimento.getMovImporto());
        // set the various parameters if it is an outgoing or entering movement
        if (oldImporto < 0) {
            tipoMov = -1;
            txtEditMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorUscite));
            RadioButton btnOut = findViewById(R.id.btnEditOut);
            btnOut.setChecked(true);
        } else {
            tipoMov = 1;
            txtEditMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorEntrate));
            RadioButton btnIn = findViewById(R.id.btnEditIn);
            btnIn.setChecked(true);
        }

        // format the amount to be displayed
        txtEditMovImporto.setText(cFunzioni.strImporto(oldImporto * tipoMov));

        RadioGroup rdgEntrataUscita = findViewById(R.id.rdgEditInOut);
        // activate listening on checked radio buttons
        rdgEntrataUscita.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.btnEditIn) { //displays the amount in green
                txtEditMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorEntrate));
                tipoMov = 1;
            } else if (checkedId == R.id.btnEditOut) { //displays the amount in red
                txtEditMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorUscite));
                tipoMov = -1;
            }
        });

        EditText txtEditMovNote = findViewById(R.id.txtEditMovNote);
        txtEditMovNote.setText(myDBMovimento.getMovNote());

        ImageButton btnSalva = findViewById(R.id.btnEditMovSalva);
        // activate listening on the save button
        btnSalva.setOnClickListener(v -> {
            // updates the bank balance
            aggiornaSaldo(oldImporto, Double.parseDouble(saldoIn), Double.parseDouble(saldoOut),
                Double.parseDouble(txtEditMovImporto.getText().toString()), tipoMov);

            DBHelper myDBConti = new DBHelper(this);
            double dblDepo1 = Double.parseDouble(txtEditMovImporto.getText().toString()) * tipoMov;
            // updates the movement
            myDBConti.aggiornaMovimento(strIdMov, cFunzioni.strImporto(dblDepo1),
                cFunzioni.filtroData(txtEditMovData.getText().toString()),
                cFunzioni.filtroData(txtEditMovValuta.getText().toString()),
                txtEditMovNote.getText().toString());
            // return to the activity listing the movements
            intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
            intent.putExtra("id", strIdBanca);
            startActivity(intent);
        });

        ImageButton btnCancella = findViewById(R.id.btnEditMovCancella);
        // activate listening on the delete button
        btnCancella.setOnClickListener(v -> {
            // updates the bank balance
            aggiornaSaldo(oldImporto, Double.parseDouble(saldoIn), Double.parseDouble(saldoOut),
                0.0, 1);
            DBHelper myDBConti = new DBHelper(this);
            // cancels the indicated movement
            myDBConti.CancellaMovimento(strIdMov);
            // return to the activity listing the movements
            intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
            intent.putExtra("id", strIdBanca);
            startActivity(intent);
        });

        ImageButton btnAnnulla = findViewById(R.id.btnEditMovAnnulla);
        // activate listening on the cancel button
        btnAnnulla.setOnClickListener(v -> {
            // return to the activity listing the movements
            intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
            intent.putExtra("id", strIdBanca);
            startActivity(intent);
        });
    }

    // updates the movement
    private void aggiornaSaldo(double oldImporto, double saldoIn, double saldoOut,
        double importo, int tipoMov) {

        if (oldImporto<0) {saldoOut = saldoOut + oldImporto;} // Uscita
        else {saldoIn = saldoIn - oldImporto;} // Entrata

        if (tipoMov == 1) {saldoIn = saldoIn + importo;}
        else {saldoOut = saldoOut + importo;}
        DBHelper myDBConti = new DBHelper(this);
        myDBConti.scriveSaldoBanca(strIdBanca, saldoIn, saldoOut);
    }
}