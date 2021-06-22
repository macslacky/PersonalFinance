package info.ribosoft.personalfinance.GestioneDBLista;

// create the custom class for the adapter
public class ClassContiBanche {
    String strIdBanca, strNomeBanca, strSaldoBanca, strSaldoEntrate, strSaldoUscite;

    // set the fields
    public ClassContiBanche(String idBanca, String nomeBanca, String saldoBanca,
        String saldoEntrate, String saldoUscite) {
        this.strIdBanca = idBanca;
        this.strNomeBanca = nomeBanca;
        this.strSaldoBanca = saldoBanca;
        this.strSaldoEntrate = saldoEntrate;
        this.strSaldoUscite = saldoUscite;
    }

    // reads the fiels
    public String getIdBanca() {return strIdBanca;}
    public String getNomeBanca() {
        return strNomeBanca;
    }
    public String getSaldoBanca() {return strSaldoBanca;}
    public String getSaldoEntrate() {
        return strSaldoEntrate;
    }
    public String getSaldoUscite() {return strSaldoUscite;}
}
