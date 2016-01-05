package com.example.songmeiling.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.BaseKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class ShowActivity extends BaseActivity {

    private TextView tvTime;
    private EditText etTitle;
    private EditText etContent;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String noteId;
    private ScrollView sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setActionBar();
        initWidget();
        showContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (item.getTitle().equals("EDIT")) {
                    onEdit(item);
                } else {
                    onSave(item);
                }
                break;
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEdit(MenuItem item) {
        etTitle.setEnabled(true);
        etContent.setEnabled(true);
        item.setTitle(R.string.save);
    }

    private void onSave(MenuItem item) {
        final MenuItem saveItem = item;

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        builder.setMessage(R.string.sure_to_savechange);
        builder.setTitle(R.string.empty);
        builder.setPositiveButton(R.string.empty, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                tvTime.setText(date);
                etTitle.setEnabled(false);
                etContent.setEnabled(false);
                ContentValues values = new ContentValues();
                values.put("title", etTitle.getText().toString());
                values.put("content", etContent.getText().toString());
                values.put("date", tvTime.getText().toString());
                //db.update("Note", values, "_id=?", new String[]{noteId});
                db.execSQL("insert into Note(title,content,author,date) values(?,?,?,?)", new String[]{etTitle.getText().toString(), etContent.getText().toString(), getSharedPreferences("loginData", MODE_PRIVATE).getString("currentLoginId", null), tvTime.getText().toString()});
                db.execSQL("delete from Note where _id=?", new String[]{noteId});
                saveItem.setTitle(R.string.edit);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etTitle.setEnabled(false);
                etContent.setEnabled(false);
                saveItem.setTitle(R.string.edit);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showContent() {
        noteId = getIntent().getStringExtra("selectedId");
        dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 2);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Note", null, "_id=?", new String[]{noteId}, null, null, null, null);
        cursor.moveToFirst();
        etTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        etContent.setText(cursor.getString(cursor.getColumnIndex("content")));
        tvTime.setText(cursor.getString(cursor.getColumnIndex("date")));
    }

    private void setActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initWidget() {
        etTitle = (EditText) findViewById(R.id.title);
        etContent = (EditText) findViewById(R.id.content);
        tvTime = (TextView) findViewById(R.id.currentTime);
        etTitle.setEnabled(false);
        etContent.setEnabled(false);
        sc = (ScrollView) findViewById(R.id.contentScroll);
    }

}
