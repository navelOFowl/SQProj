package com.example.sqproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttAdd, buttRead, buttClear;
    EditText name, email;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttAdd = (Button) findViewById(R.id.buttAdd);
        buttAdd.setOnClickListener(this);

        buttRead = (Button) findViewById(R.id.buttRead);
        buttRead.setOnClickListener(this);

        buttClear = (Button) findViewById(R.id.buttClear);
        buttClear.setOnClickListener(this);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

    }

    public void updateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int emailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
            TableLayout Output = findViewById(R.id.Output);
            Output.removeAllViews();
            do {
                TableRow outputRow = new TableRow(this);
                outputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView outputId = new TextView(this);
                params.weight = 1.0f;
                outputId.setLayoutParams(params);
                outputId.setText(cursor.getString(idIndex));
                outputRow.addView(outputId);

                TextView outputName = new TextView(this);
                params.weight = 3.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                outputRow.addView(outputName);


                TextView outputMail = new TextView(this);
                params.weight = 3.0f;
                outputMail.setLayoutParams(params);
                outputMail.setText(cursor.getString(emailIndex));
                outputRow.addView(outputMail);

                Button deleteButt = new Button(this);
                deleteButt.setOnClickListener(this);
                params.weight=1.0f;
                deleteButt.setLayoutParams(params);
                deleteButt.setText("Удалить");
                deleteButt.setId(cursor.getInt(idIndex));
                outputRow.addView(deleteButt);

                Output.addView(outputRow);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    @Override
    public void onClick(View v) {


        switch(v.getId()) {
            case R.id.buttAdd:
                String strname = name.getText().toString();
                String stremail = email.getText().toString();

                rec = new ContentValues();

                rec.put(DBHelper.KEY_NAME, strname);
                rec.put(DBHelper.KEY_MAIL, stremail);

                database.insert(DBHelper.TABLE_CONTACTS, null, rec);
                updateTable();
                break;

            case R.id.buttClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                updateTable();
                break;
            default:
                View outputDbRow = (View) v.getParent();
                ViewGroup outputDb = (ViewGroup) outputDbRow.getParent();
                outputDb.removeView(outputDbRow);
                outputDb.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});
                rec = new ContentValues();

                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int emailIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MAIL);
                    int realId = 1;
                    do{
                        if(cursorUpdater.getInt(idIndex) > realId){
                            rec.put(DBHelper.KEY_ID, realId);
                            rec.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            rec.put(DBHelper.KEY_MAIL, cursorUpdater.getString(emailIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, rec);
                        }
                        realId++;
                    } while (cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast()){
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    updateTable();
                }
                break;
        }
        dbHelper.close();
    }
}