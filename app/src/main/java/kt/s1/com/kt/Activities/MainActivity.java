package kt.s1.com.kt.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import kt.s1.com.kt.R;

public class MainActivity extends Activity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = "FL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        init_FireBaseAuthStateListener();
        init_elements();
    }
    @Override
     protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void init_elements(){
    loginBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            login();
        }
    });

    }

    void init_FireBaseAuthStateListener(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    //user is already signed in
                    Log.e("FL", "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else {
                    //User not signed in
                    Log.d("FL", "onAuthStateChanged:signed_out");

                }
            }
        };

    }

    void login_ToFireBase(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());

                            Utilities.Helper.showDialog(MainActivity.this,"Sign In","Signing in failed,try Again!!");
                            return;
                        }

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Sign In")
                                .setMessage("Signing in successfull!!")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MainActivity.this,StorageActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                        // ...
                    }
                });

    }

    void login()
    {

        String[] pass  = new String[]{username.getText().toString(),password.getText().toString()};
        for (String s:pass) {
            if(s.isEmpty()){
                Utilities.Helper.showDialog(MainActivity.this,"Sign In","Fields cant be empty");
                return;
            }
        }

        login_ToFireBase(pass[0],pass[1]);

    }
}


