package com.example.songmeiling.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SearchView.OnQueryTextListener {

    private Button btnAddNote;
    private Cursor cursor;
    private boolean loginState;
    private EditText etSearchTitle;
    private ListView lvNoteTitle;
    private SQLiteDatabase db;
    private String currentUser;
    private LinearLayout layoutSearch;
    private MyDatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private int headIcon = R.drawable.ic_drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBar();
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        loginState = sharedPreferences.getBoolean("loginState", false);
        currentUser = sharedPreferences.getString("currentLoginId", null);
        if (loginState) {
            initWidget();
            showListView();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void showListView() {
        dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 2);//创建数据库
        db = dbHelper.getWritableDatabase();
        cursor = db.query("Note", null, "author=?", new String[]{currentUser}, null, null, "_id desc");
        adapter = new SimpleCursorAdapter(this, R.layout.note_title_layout, cursor, new String[]{"_id", "title", "date"}, new int[]{R.id.noteId, R.id.noteTitle, R.id.noteTime});
        lvNoteTitle.setAdapter(adapter);
    }

    private void setActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeAsUpIndicator(headIcon);
    }

    private void initWidget() {
        btnAddNote = (Button) findViewById(R.id.add_button);
        lvNoteTitle = (ListView) findViewById(R.id.noteList);
        etSearchTitle = (EditText) findViewById(R.id.searchTitle);
        layoutSearch = (LinearLayout) findViewById(R.id.search_layout);
        btnAddNote.setOnClickListener(this);
        lvNoteTitle.setOnItemClickListener(this);
        lvNoteTitle.setOnItemLongClickListener(this);

        etSearchTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String searchTitle = etSearchTitle.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchTitle == null) {
                        //Toast.makeText(getApplicationContext(), "You are searching nothing", Toast.LENGTH_SHORT).show();
                        layoutSearch.setVisibility(View.GONE);
                    } else if (searchTitle.equals("")) {
                        db = dbHelper.getWritableDatabase();
                        cursor = db.query("Note", null, "author=?", new String[]{currentUser}, null, null, "_id desc");
                        adapter.changeCursor(cursor);
                        lvNoteTitle.setAdapter(adapter);
                        layoutSearch.setVisibility(View.GONE);
                    } else {
                        db = dbHelper.getWritableDatabase();
                        cursor = db.query("Note", null, "author=? and title like ?", new String[]{currentUser, "%" + searchTitle + "%"}, null, null, "_id desc");
                        adapter.changeCursor(cursor);
                        lvNoteTitle.setAdapter(adapter);
                        etSearchTitle.setText("");
                        layoutSearch.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null)
            cursor.close();
        if (db != null)
            db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (layoutSearch.getVisibility() == View.GONE) {

                    layoutSearch.setVisibility(View.VISIBLE);
                    etSearchTitle.setFocusable(true);
                    etSearchTitle.setFocusableInTouchMode(true);
                    etSearchTitle.requestFocus();
                    InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                } else {
                    layoutSearch.setVisibility(View.GONE);
                }
                break;
            case android.R.id.home:
                //Log.d("MainActivity", "you press home");
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("loginState", loginState);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("authorName", currentUser);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView clickedId = (TextView) view.findViewById(R.id.noteId);
        Intent intent = new Intent(this, ShowActivity.class);
        intent.putExtra("selectedId", clickedId.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final TextView selectedId = (TextView) view.findViewById(R.id.noteId);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.sure_to_delete);
        builder.setTitle(R.string.empty);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db = dbHelper.getWritableDatabase();
                db.delete("Note", "_id=?", new String[]{selectedId.getText().toString()});
                cursor = db.query("Note", null, null, null, null, null, "_id desc");
                adapter.changeCursor(cursor);
                lvNoteTitle.setAdapter(adapter);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.sure_to_quit);
            builder.setTitle(R.string.empty);
            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals("")) {
            db = dbHelper.getWritableDatabase();
            cursor = db.query("Note", null, "author=?", new String[]{currentUser}, null, null, "_id desc");
            adapter.changeCursor(cursor);
            lvNoteTitle.setAdapter(adapter);
            layoutSearch.setVisibility(View.GONE);
        } else {
            db = dbHelper.getWritableDatabase();
            cursor = db.query("Note", null, "author=? and title like ?", new String[]{currentUser, "%" + newText + "%"}, null, null, "_id desc");
            adapter.changeCursor(cursor);
            lvNoteTitle.setAdapter(adapter);
            etSearchTitle.setText(R.string.empty);
            layoutSearch.setVisibility(View.GONE);
        }
        return false;
    }
}
