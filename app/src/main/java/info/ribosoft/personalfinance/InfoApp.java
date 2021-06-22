package info.ribosoft.personalfinance;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

// takes care of displaying information about the app and its author in a popup
public class InfoApp extends AppCompatActivity {
    public void OnCreate(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_main);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Info");
        dialog.show();
    }
}
