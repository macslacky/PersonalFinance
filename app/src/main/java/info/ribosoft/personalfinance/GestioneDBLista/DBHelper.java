package info.ribosoft.personalfinance.GestioneDBLista;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NOME = "dbGestioneConti.db";
    // Tabella Lista Banche
    public static final String TABELLA_BANCHE = "tblListaBanche";
    public static final String BANCHE_ID = "idBanca";
    public static final String BANCHE_NOME = "NomeBanca";
    public static final String BANCHE_SALDOENTRATE = "SaldoEntrate";
    public static final String BANCHE_SALDOUSCITE = "SaldoUscite";
    // Tabella Lista movimenti
    public static final String TABELLA_MOVIMENTI = "tblListaMovimenti";
    public static final String MOVIMENTI_ID = "idMovimento";
    public static final String MOVIMENTI_IDBANCA = "idBanca";
    public static final String MOVIMENTI_IMPORTO = "Importo";
    public static final String MOVIMENTI_DATA = "Data";
    public static final String MOVIMENTI_VALUTA = "Valuta";
    public static final String MOVIMENTI_NOTE = "Note";

    public DBHelper(Context context) {
        super(context, DATABASE_NOME, null, 1);
    }

    // create the tables
    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        sqlDB.execSQL("CREATE TABLE " + TABELLA_BANCHE + "(" + BANCHE_ID + " INTEGER PRIMARY KEY, "
            + BANCHE_NOME + " TEXT, " + BANCHE_SALDOENTRATE + " TEXT, " +
            BANCHE_SALDOUSCITE + " TEXT)");

        sqlDB.execSQL("CREATE TABLE " + TABELLA_MOVIMENTI + "(" + MOVIMENTI_ID +
            " INTEGER PRIMARY KEY, " + MOVIMENTI_IDBANCA + " TEXT, " + MOVIMENTI_IMPORTO +
            " TEXT, " + MOVIMENTI_DATA + " TEXT, " + MOVIMENTI_VALUTA + " TEXT, " +
            MOVIMENTI_NOTE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {}

    // opens the table and returns the cursor to the first element
    private Cursor openReadTable(String stringSql) {
        // opens the database for reading
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        // performs a read sql query
        Cursor cursor = sqLiteDatabase.rawQuery(stringSql, null);
        // move the cursor to the first row
        cursor.moveToFirst();
        return cursor;
    }

    // writes a new bank in the table
    public void scriveNuovaBanca(String nomeBanca) {
        // prepares the data to write it into the table
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        contentValues.put(BANCHE_NOME, nomeBanca);
        contentValues.put(BANCHE_SALDOENTRATE, "0.00");
        contentValues.put(BANCHE_SALDOUSCITE, "0.00");
        sqlDB.insert(TABELLA_BANCHE, null, contentValues);
    }

    // reads the banks table
    public ArrayList<ClassContiBanche> leggiBanche() {
        ArrayList<ClassContiBanche> arrayListaBanche = new ArrayList<>();

        // opens the database for reading and move the cursor to the first row
        String stringSql = "SELECT * FROM " + TABELLA_BANCHE + " ORDER BY " + BANCHE_NOME + " ASC";
        Cursor cursor = openReadTable(stringSql);

        while (!cursor.isAfterLast()) {
            // returns the value of the requested column as a string
            String idBanca = cursor.getString(cursor.getColumnIndex(BANCHE_ID));
            String nomeBanca = cursor.getString(cursor.getColumnIndex(BANCHE_NOME));
            String saldoEntrate = cursor.getString(cursor.getColumnIndex(BANCHE_SALDOENTRATE));
            String saldoUscite = cursor.getString(cursor.getColumnIndex(BANCHE_SALDOUSCITE));

            double saldoBanca = Double.parseDouble(saldoEntrate) - Double.parseDouble(saldoUscite);

            // add the string to the list
            arrayListaBanche.add(new ClassContiBanche(idBanca, nomeBanca,
               Double.toString(saldoBanca), saldoEntrate, saldoUscite));

            // move the cursor to the next row
            cursor.moveToNext();
        }
        return arrayListaBanche;
    }

    // read single bank
    public DBDatiBanca leggiSaldoBanca(String strIdBanca) {
        // opens the database for reading the indicated movement
        String strSql = "SELECT * FROM " + TABELLA_BANCHE + " WHERE " + BANCHE_ID + " = '" +
                strIdBanca + "'";
        Cursor cursor = openReadTable(strSql);

        // returns the value of the requested column as a string
        DBDatiBanca dbDatiBanca = new DBDatiBanca();
        dbDatiBanca.strNomeBanca = cursor.getString(cursor.getColumnIndex(BANCHE_NOME));
        dbDatiBanca.strSaldoEntrata = cursor.getString(cursor.getColumnIndex(BANCHE_SALDOENTRATE));
        dbDatiBanca.strSaldoUscita = cursor.getString(cursor.getColumnIndex(BANCHE_SALDOUSCITE));
        return dbDatiBanca;
    }

    // updates the bank balance
    public void scriveSaldoBanca(String idBanca, double saldoEntrata, double saldoUscita) {
        // open the database to write the balance for the indicated bank
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        // prepares the data to write it into the table
        ContentValues contentValues = new ContentValues();
        contentValues.put(BANCHE_ID, idBanca);

        ClassFunzioni cFunzioni = new ClassFunzioni();
        contentValues.put(BANCHE_SALDOENTRATE, cFunzioni.strImporto(saldoEntrata));
        contentValues.put(BANCHE_SALDOUSCITE, cFunzioni.strImporto(saldoUscita));
        sqlDB.update(TABELLA_BANCHE, contentValues, BANCHE_ID + "=" +
                idBanca, null);
    }

    // writes a new movment in the table
    public void scriviNuovoMovimento(String idBanca, String importo, String data, String valuta,
        String note) {
        // prepares the data to write it into the table
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIMENTI_IDBANCA, idBanca);
        contentValues.put(MOVIMENTI_IMPORTO, importo);
        ClassFunzioni cFunzioni = new ClassFunzioni();
        contentValues.put(MOVIMENTI_DATA, cFunzioni.filtroData(data));
        contentValues.put(MOVIMENTI_VALUTA, cFunzioni.filtroData(valuta));
        contentValues.put(MOVIMENTI_NOTE, note);
        sqlDB.insert(TABELLA_MOVIMENTI, null, contentValues);
    }

    // updates the movement
    public void aggiornaMovimento(String idMovimento, String importo, String data, String valuta,
        String note) {
        // open the database to write the indicated movement
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIMENTI_IMPORTO, importo);
        contentValues.put(MOVIMENTI_DATA, data);
        contentValues.put(MOVIMENTI_VALUTA, valuta);
        contentValues.put(MOVIMENTI_NOTE, note);
        sqlDB.update(TABELLA_MOVIMENTI, contentValues, MOVIMENTI_ID + "=" +
                idMovimento, null);
    }

    // cancels the indicated movement
    public void CancellaMovimento(String idMovimento) {
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        sqlDB.delete(TABELLA_MOVIMENTI, "idmovimento = ? ", new String[] {idMovimento});
    }

    // read the movements related to a bank
    public ArrayList<ClassListaMovimenti> leggiMovimenti(String idBanca, boolean blnFiltro,
        String strInizio, String strFine) {
        ArrayList<ClassListaMovimenti> arrayListMovimenti = new ArrayList<>();

        // opens the database for reading and move the cursor to the first row
        String stringSql = "SELECT * FROM " + TABELLA_MOVIMENTI + " WHERE IDBANCA = '" +
            idBanca + "'";
        if (blnFiltro) stringSql += " AND " +MOVIMENTI_VALUTA + " >= '" + strInizio + "' AND " +
            MOVIMENTI_VALUTA + " <= '" + strFine + "' ";
        stringSql += " ORDER BY " + MOVIMENTI_VALUTA + " ASC";
        Cursor cursor = openReadTable(stringSql);

        while (!cursor.isAfterLast()) {
            // returns the value of the requested column as a string
            String idMovimento = cursor.getString(cursor.getColumnIndex(MOVIMENTI_ID));
            ClassFunzioni cFunzioni = new ClassFunzioni();
            String dataValuta = cFunzioni.formatData(cursor.getString(
                cursor.getColumnIndex(MOVIMENTI_VALUTA)));
            String importo = cursor.getString(cursor.getColumnIndex(MOVIMENTI_IMPORTO));
            String note = cursor.getString(cursor.getColumnIndex(MOVIMENTI_NOTE));

            // add the string to the list
            arrayListMovimenti.add(new ClassListaMovimenti(idMovimento, dataValuta, importo,
                note));

            // move the cursor to the next row
            cursor.moveToNext();
        }
        return arrayListMovimenti;
    }

    // read single movement
    public DBDatiMovimento leggiSingoloMov(String idMovimento) {
        // opens the database for reading the indicated movement
        String strSql = "SELECT * FROM " + TABELLA_MOVIMENTI + " WHERE " + MOVIMENTI_ID +
            " = '" + idMovimento + "'";
        Cursor cursor = openReadTable(strSql);

        // returns the value of the requested column as a string
        DBDatiMovimento dbDatiMovimento =new DBDatiMovimento();
        ClassFunzioni cFunzioni = new ClassFunzioni();
        dbDatiMovimento.strDataMovimento = cFunzioni.formatData(
            cursor.getString(cursor.getColumnIndex(MOVIMENTI_DATA)));
        dbDatiMovimento.strDataValuta = cFunzioni.formatData(
            cursor.getString(cursor.getColumnIndex(MOVIMENTI_VALUTA)));
        dbDatiMovimento.strImporto = cursor.getString(cursor.getColumnIndex(MOVIMENTI_IMPORTO));
        dbDatiMovimento.strNote = cursor.getString(cursor.getColumnIndex(MOVIMENTI_NOTE));
        return dbDatiMovimento;
    }
}
