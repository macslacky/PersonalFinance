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

import info.ribosoft.personalfinance.R;

// define the adapter to describe the process of converting the Java object into a view
public class MyAdapterListaMovimenti extends ArrayAdapter<ClassListaMovimenti> {
    private int i = 1;

    public MyAdapterListaMovimenti(Context context,
        ArrayList<ClassListaMovimenti> classListaMovimentis) {
        super(context, 0, classListaMovimentis);
    }

    // returns the movement ID of the indicated location
    public String leggiIdMovimento(int position) {
        ClassListaMovimenti classListaMovimenti = getItem(position);
        return classListaMovimenti.getIdMovimento();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_lista_movimenti,
                parent, false);
        }
        // get the data item for this position
        ClassListaMovimenti classListaMovimenti = getItem(position);
        // lookup view for data population
        LinearLayout lytListaMov = convertView.findViewById(R.id.lytListaMovimenti);

        // alternates the color of the line
        if (i == 1) {
            i = 0;
            lytListaMov.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.riga1));
        } else {
            i = 1;
            lytListaMov.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.riga2));
        }

        TextView txtMovId = convertView.findViewById(R.id.txtMovId);
        TextView txtMovDataValuta = convertView.findViewById(R.id.txtMovDataValuta);
        TextView txtMovImportoEntrata = convertView.findViewById(R.id.txtMovImportoEntrata);
        TextView txtMovImportoUscita = convertView.findViewById(R.id.txtMovImportoUscita);
        TextView txtMovNote = convertView.findViewById(R.id.txtMovNote);

        // populate the data into the template view using the data object
        txtMovId.setText(classListaMovimenti.getIdMovimento());
        txtMovDataValuta.setText(classListaMovimenti.getDataValuta());

        // displays amount in income or expense column
        if (Double.parseDouble(classListaMovimenti.getImporto()) > 0) {
            txtMovImportoEntrata.setText(classListaMovimenti.getImporto());
            txtMovImportoUscita.setEms(0);
        } else {
            txtMovImportoEntrata.setEms(0);
            txtMovImportoUscita.setText(classListaMovimenti.getImporto());
        }

        txtMovNote.setText(classListaMovimenti.getNote());

        // return the completed view to render on screen
        return convertView;
    }
}