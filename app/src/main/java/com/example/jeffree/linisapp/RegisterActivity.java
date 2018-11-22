package com.example.jeffree.linisapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email_field = (EditText) findViewById(R.id.reg_email);
        reg_pass_field = (EditText) findViewById(R.id.reg_pass);
        reg_confirm_pass_field = (EditText) findViewById(R.id.reg_confirm_pass);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_progress = (ProgressBar) findViewById(R.id.reg_progress);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = reg_email_field.getText().toString(); //gets text from email textfield
                String pass = reg_pass_field.getText().toString(); //gets text from password textfield
                String confirm_pass = reg_confirm_pass_field.getText().toString(); //gets text from confirm password textfield

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)){ //checks if textfields are empty

                    if(pass.equals(confirm_pass)){ //password must be the same with confirm password

                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //add user on firebase using text from username and password textfield
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();
                                }else{
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error :" + errorMessage, Toast.LENGTH_LONG).show();
                                }

                                reg_progress.setVisibility(View.INVISIBLE);
                            }

                        });

                    }else {
                        Toast.makeText(RegisterActivity.this, "Confirm Password and Password field does not match", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }

    @Override
    protected void onStart() { //checks if current user is logged in already
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }

    private void sendToMain() { //send to mainActivity

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
