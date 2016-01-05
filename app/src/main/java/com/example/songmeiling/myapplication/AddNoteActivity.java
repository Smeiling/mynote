package com.example.songmeiling.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class AddNoteActivity extends BaseActivity {

    private TextView tvTime;
    private EditText etTitle;
    private EditText etContent;
    private MyDatabaseHelper dbHelper;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setActionBar();
        initWidget();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        tvTime.setText(date);
        currentUser = getIntent().getStringExtra("authorName");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
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
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add:
                String tt = etTitle.getText().toString();
                String ct = etContent.getText().toString();
                if (tt.isEmpty() || ct.isEmpty()) {

                } else {
                    int insertID = saveContent(tt, ct);
                    Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ShowActivity.class);
                    intent.putExtra("selectedId", String.valueOf(insertID));
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.add_new_note);
    }

    private void initWidget() {
        tvTime = (TextView) findViewById(R.id.currentTime);
        etTitle = (EditText) findViewById(R.id.title);
        etContent = (EditText) findViewById(R.id.content);
    }

    public int saveContent(String t, String c) {
        dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into Note(title,content,author,date) values(?,?,?,?)", new String[]{t, c, currentUser, tvTime.getText().toString()});
        Cursor insCursor = db.query("Note", null, null, null, null, null, "_id desc");
        insCursor.moveToFirst();
        int insertIndex = insCursor.getInt(insCursor.getColumnIndex("_id"));
        Log.d("MainActivity", String.valueOf(insertIndex));
        return insertIndex;
    }
}
