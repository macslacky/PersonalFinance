package info.ribosoft.personalfinance;

import androidx.appcompat.app.AppCompatActivity;
import info.ribosoft.personalfinance.GestioneDBLista.DBHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class NuovaBancaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_banca);

        EditText txtNomeBanca = findViewById(R.id.txtNewBancaNome);

        ImageButton ibtnSalva = findViewById(R.id.ibtnNewBancaSalva);
        // activate listening on the save button
        ibtnSalva.setOnClickListener((View view) -> {
            DBHelper myDBConti = new DBHelper(this);
            // writes a new bank in the table
            myDBConti.scriveNuovaBanca(txtNomeBanca.getText().toString());
            // returns to the main activity
            vaiMainActivity();
        });

        ImageButton ibtnAnnulla = findViewById(R.id.ibtnNewBancaAnnulla);
        // activate listening on the cancel button
        ibtnAnnulla.setOnClickListener(v -> vaiMainActivity());
    }

    void vaiMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}