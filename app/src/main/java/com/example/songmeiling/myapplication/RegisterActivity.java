package com.example.songmeiling.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final int REG_SUCCESS = 0;
    private static final int UN_REPEAT = 1;
    private TextView tvLogin;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setActionBar();
        initWidget();
        dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.registerBtn:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (password.equals(etConfirmPassword.getText().toString())) {
                    if (checkAndReg(username, password) == REG_SUCCESS) {
                        Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("rUsername", username);
                        intent.putExtra("rPassword", password);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.exist_username, Toast.LENGTH_SHORT).show();
                        etUsername.setText(R.string.empty);
                    }
                } else {
                    Toast.makeText(this, R.string.diff_password, Toast.LENGTH_SHORT).show();
                    etPassword.setText(R.string.empty);
                    etConfirmPassword.setText(R.string.empty);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d("MainActivity", uri.toString());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.register);
    }

    private void initWidget() {
        tvLogin = (TextView) findViewById(R.id.login);
        btnRegister = (Button) findViewById(R.id.registerBtn);
        etUsername = (EditText) findViewById(R.id.rUsername);
        etPassword = (EditText) findViewById(R.id.rPassword);
        etConfirmPassword = (EditText) findViewById(R.id.cPassword);
        tvLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    public int checkAndReg(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, "username=?", new String[]{username}, null, null, null, null);
        if (cursor.getCount() <= 0) {
            db.execSQL("insert into User(username,password) values(?,?)", new String[]{username, password});
            return REG_SUCCESS;
        } else {
            return UN_REPEAT;
        }
    }

}
