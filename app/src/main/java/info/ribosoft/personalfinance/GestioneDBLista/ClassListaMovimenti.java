package info.ribosoft.personalfinance.GestioneDBLista;

// create the custom class for the adapter
public class ClassListaMovimenti {
    String strIdMovimento, strImporto, strDataValuta, strNote;

    // set the fields
    public ClassListaMovimenti (String idMovimento, String dataValuta,String importo, String note) {
        this.strIdMovimento = idMovimento;
        this.strDataValuta = dataValuta;
        this.strImporto = importo;
        this.strNote = note;
    }

    // reads the fields
    public String getIdMovimento() {return strIdMovimento;}
    public String getImporto() {return strImporto;}
    public String getDataValuta() {return strDataValuta;}
    public String getNote() {return strNote;}
}