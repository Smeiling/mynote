package com.example.songmeiling.myapplication;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button btnLogin;
    private TextView tvRegister;
    private EditText etUsername;
    private EditText etPassword;
    private MyDatabaseHelper dbHelper;
    private CheckBox cbSaveId;
    private boolean loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setActionBar();
        initWidget();
        initView();
    }

    private void initView() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        String uid = sharedPreferences.getString("currentLoginId", "");
        String pwd = sharedPreferences.getString("Password", "");
        if (uid.isEmpty() || pwd.isEmpty()) {
            etUsername.setText(uid);
            cbSaveId.setChecked(false);
        } else {
            cbSaveId.setChecked(true);        //有记住的密码，就显示
            etUsername.setText(uid);
            etPassword.setText(pwd);
        }
        loginState = getIntent().getBooleanExtra("loginState", false);
        if (loginState) {
            etUsername.setEnabled(false);
            etPassword.setVisibility(View.GONE);
            cbSaveId.setVisibility(View.GONE);
            tvRegister.setVisibility(View.GONE);
            btnLogin.setText(R.string.logout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (loginState == true)
                    finish();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (btnLogin.getText().equals("登录")) {
                    onLogin();
                } else if (btnLogin.getText().equals("退出登录")) {
                    onLogout();
                }

                break;
            case R.id.register:
                //Log.d("MainActivity", "go register");
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK)//注册成功
                {
                    etUsername.setText(data.getStringExtra("rUsername"));
                    etPassword.setText(data.getStringExtra("rPassword"));
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (loginState == true)
                finish();
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

        }
        return super.onKeyDown(keyCode, event);
    }


    private void setActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.login);
    }

    private void initWidget() {
        cbSaveId = (CheckBox) findViewById(R.id.rememberId);
        btnLogin = (Button) findViewById(R.id.loginBtn);
        tvRegister = (TextView) findViewById(R.id.register);
        etUsername = (EditText) findViewById(R.id.lUsername);
        etPassword = (EditText) findViewById(R.id.lPassword);
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    private boolean loginCheck(String username, String password) {
        dbHelper = new MyDatabaseHelper(this, "MyNote.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, "username=? and password=?", new String[]{username, password}, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }

    private void onLogin() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (loginCheck(username, password)) {
            SharedPreferences.Editor editor = getSharedPreferences("loginData", MODE_PRIVATE).edit();
            if (cbSaveId.isChecked()) {
                editor.putString("Username", username);
                editor.putString("Password", password);
                editor.putString("currentLoginId", username);
            } else {
                editor.clear();
                editor.putString("currentLoginId", username);
            }
            editor.putBoolean("loginState", true);
            editor.commit();
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        } else {
            Toast.makeText(this, R.string.wrong_login_data, Toast.LENGTH_SHORT).show();
            etPassword.setText(R.string.empty);
        }
    }

    private void onLogout() {
        etUsername.setEnabled(true);
        etPassword.setVisibility(View.VISIBLE);
        cbSaveId.setVisibility(View.VISIBLE);
        tvRegister.setVisibility(View.VISIBLE);
        SharedPreferences.Editor editor = getSharedPreferences("loginData", MODE_PRIVATE).edit();
        editor.putBoolean("loginState", false);
        editor.commit();
        loginState = false;
        btnLogin.setText(R.string.login);
    }


}
