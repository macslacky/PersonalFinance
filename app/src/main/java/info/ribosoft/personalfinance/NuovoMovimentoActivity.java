package info.ribosoft.personalfinance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import info.ribosoft.personalfinance.GestioneDBLista.ClassFunzioni;
import info.ribosoft.personalfinance.GestioneDBLista.DBDatiBanca;
import info.ribosoft.personalfinance.GestioneDBLista.DBHelper;

public class NuovoMovimentoActivity extends AppCompatActivity {
    private Intent intent;
    private int strTipoMov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovo_movimento);

        strTipoMov = 1;

        intent = getIntent();
        // read data passed as parameters
        String strIdBanca = intent.getStringExtra("id");
        String strNomeBanca = intent.getStringExtra("nomeBanca");

        TextView txtNewMovBanca = findViewById(R.id.txtNewMovBanca);
        // displays the name of the bank
        txtNewMovBanca.setText(strNomeBanca);

        ClassFunzioni cFunzioni = new ClassFunzioni();

        TextView txtNewMovData = findViewById(R.id.txtNewMovData);
        ImageButton btnNewMovData = findViewById(R.id.btnNewMovData);
        // activate listening on the button to insert the date of the movement
        btnNewMovData.setOnClickListener(v ->
            cFunzioni.modificaData(NuovoMovimentoActivity.this, txtNewMovData));

        TextView txtNewMovValuta = findViewById(R.id.txtNewMovValuta);
        ImageButton btnNewMovValuta = findViewById(R.id.btnNewMovValuta);
        // activate listening on the button to enter the value date
        btnNewMovValuta.setOnClickListener(v ->
            cFunzioni.modificaData(NuovoMovimentoActivity.this, txtNewMovValuta));

        EditText txtNewMovImporto = findViewById(R.id.txtNewMovImporto);
        // if the user passes to another element, the function to format the amount is called
        txtNewMovImporto.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) cFunzioni.formatImporto(txtNewMovImporto);
        });

        RadioGroup rdgEntrataUscita = findViewById(R.id.rdgEntrataUscita);
        // activate listening on checked radio buttons
        rdgEntrataUscita.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.btnEntrata) { //displays the amount in green
                txtNewMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorEntrate));
                strTipoMov = 1;
            } else if (checkedId == R.id.btnUscita) { //displays the amount in red
                txtNewMovImporto.setTextColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorUscite));
                strTipoMov = -1;
            }
        });

        ImageButton btnNewMovSalva = findViewById(R.id.btnNewMovSalva);
        // activate listening on the save button
        btnNewMovSalva.setOnClickListener(v -> {
            // format the amount
            cFunzioni.formatImporto(txtNewMovImporto);

            boolean flagSalva = true;
            if (txtNewMovData.length() == 0) { //check if the movement date has been entered
                flagSalva = false;
                Toast.makeText(getApplicationContext(), "Inserire la data del movimento",
                    Toast.LENGTH_LONG).show();
            }
            if (txtNewMovValuta.length() == 0) { // check if the value date has been entered
                flagSalva = false;
                Toast.makeText(getApplicationContext(), "Inserire la valuta del movimento",
                    Toast.LENGTH_LONG).show();
            }

            if (flagSalva) {
                DBHelper myDBConti = new DBHelper(this);
                double depo = Double.parseDouble(txtNewMovImporto.getText().toString());
                // read the bank's incoming and outgoing balance
                DBDatiBanca myDBDatiBanca = myDBConti.leggiSaldoBanca(strIdBanca);
                double depoIn = Double.parseDouble(myDBDatiBanca.getSaldoEntrata());
                double depoOut = Double.parseDouble(myDBDatiBanca.getSaldoUscita());
                // updates the bank balance with the new movement
                if (strTipoMov > 0) depoIn += depo; else depoOut += depo;

                // updates the bank balance
                myDBConti.scriveSaldoBanca(strIdBanca, depoIn, depoOut);

                depo = strTipoMov * Double.parseDouble(txtNewMovImporto.getText().toString());
                EditText txtNewMovNote = findViewById(R.id.txtNewMovNote);
                // writes a new movment in the table
                myDBConti.scriviNuovoMovimento(strIdBanca, cFunzioni.strImporto(depo),
                    txtNewMovData.getText().toString(), txtNewMovValuta.getText().toString(),
                    txtNewMovNote.getText().toString());
                // return to the activity listing the movements
                intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
                intent.putExtra("id", strIdBanca);
                startActivity(intent);
            }
        });

        // activate listening on the cancel button
        ImageButton btnAnnulla = findViewById(R.id.btnNewMovAnnulla);
        btnAnnulla.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MovimentiBancaActivity.class);
            intent.putExtra("id", strIdBanca);
            startActivity(intent);
        });
    }
}