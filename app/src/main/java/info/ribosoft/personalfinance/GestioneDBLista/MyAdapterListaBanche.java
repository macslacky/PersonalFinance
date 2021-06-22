package info.ribosoft.personalfinance.GestioneDBLista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import info.ribosoft.personalfinance.R;

// define the adapter to describe the process of converting the Java object into a view
public class MyAdapterListaBanche extends ArrayAdapter<ClassContiBanche> {
    private int i = 1;

    public MyAdapterListaBanche(Context context, ArrayList<ClassContiBanche> classContiBanches) {
        super(context, 0, classContiBanches);
    }

    // returns bank id of the indicated position
    public String leggiIdBanca(int position) {
        ClassContiBanche classContiBanche = getItem(position);
        return classContiBanche.getIdBanca();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        double dblSaldo;

        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_banche,
                parent, false);
        }
        // get the data item for this position
        ClassContiBanche classContiBanche = getItem(position);
        // lookup view for data population
        LinearLayout lytListaBanche = convertView.findViewById(R.id.lytListaBanche);

        // alternates the color of the line
        if (i == 1) {
            i = 0;
            lytListaBanche.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.riga1));
        } else {
            i = 1;
            lytListaBanche.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.riga2));
        }

        TextView txtIdBanca = convertView.findViewById(R.id.txtBancheIdBanca);
        TextView txtNomeBanca = convertView.findViewById(R.id.txtBancheNomeBanca);
        TextView txtSaldoBanca = convertView.findViewById(R.id.txtBancheSaldo);
        TextView txtSaldoEntrate = convertView.findViewById(R.id.txtBancheSaldoEntrate);
        TextView txtSaldoUscite = convertView.findViewById(R.id.txtBancheSaldoUscite);

        // populate the data into the template view using the data object
        txtIdBanca.setText(classContiBanche.getIdBanca());
        txtNomeBanca.setText(classContiBanche.getNomeBanca());

        dblSaldo = Double.parseDouble(classContiBanche.getSaldoBanca());
        if (dblSaldo < 0) {
            txtSaldoBanca.setTextColor(ContextCompat.getColor(getContext(),R.color.colorUscite));
        } else {
            txtSaldoBanca.setTextColor(ContextCompat.getColor(getContext(), R.color.colorEntrate));
        }
        txtSaldoBanca.setText(String.format(Locale.US, "%.2f", dblSaldo));

        txtSaldoEntrate.setText(classContiBanche.getSaldoEntrate());
        txtSaldoUscite.setText(String.format("-%s", classContiBanche.getSaldoUscite()));

        // return the completed view to render on screen
        return convertView;
    }
}