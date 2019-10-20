package com.engineerskasa.rxj.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.engineerskasa.rxj.Activity.InstantSearch.InstantSearchActivity;
import com.engineerskasa.rxj.Database.DataSource.UserRepository;
import com.engineerskasa.rxj.Database.Local.UserDataSource;
import com.engineerskasa.rxj.Database.Local.UserDatabase;
import com.engineerskasa.rxj.Model.Preferences;
import com.engineerskasa.rxj.Model.User;
import com.engineerskasa.rxj.R;
import com.engineerskasa.rxj.Retrofit.IRxJAPI;
import com.engineerskasa.rxj.Util.Common;
import com.engineerskasa.rxj.Util.Management;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dmax.dialog.SpotsDialog;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static String TAG = HomeActivity.class.getSimpleName();

    private TextView token_textview;
    Management management;
    private Preferences userData;

    private RelativeLayout mainLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IRxJAPI rxJAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        token_textview = (TextView) findViewById(R.id.user_token);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        // initialize database
        initDB();

        rxJAPI = Common.getAPI();

        management = new Management(this);

        userData = management.getPreferences(new Preferences().setRetrieveUserCredential(true));

        if (userData.getUserName()!= null) {
            getSupportActionBar().setTitle("Akwaaba: "+userData.getUserName());
        } else {
            getSupportActionBar().setTitle("RxJava");
        }
        
        // display count of users
        try {
            countUsers();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void countUsers() throws InterruptedException {
        final AtomicInteger uCount = new AtomicInteger();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                token_textview.setText("Total users:  "+String.valueOf(Common.userRepository.countTotalUsers()));
            }
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
    }


    private void initDB() {
        Common.userDatabase = UserDatabase.getInstance(this);
        Common.userRepository = UserRepository.getInstance(UserDataSource.getInstance(Common.userDatabase.userDAO()));
    }

    public void registerUser(View view) {
        showRegisterDialog();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Register");

        LayoutInflater inflater = this.getLayoutInflater();
        View register_layout = inflater.inflate(R.layout.register_layout, null);

        final MaterialEditText edt_name = (MaterialEditText) register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = (MaterialEditText) register_layout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = (MaterialEditText) register_layout.findViewById(R.id.edt_birthdate);
        final MaterialEditText edt_phone = (MaterialEditText) register_layout.findViewById(R.id.edt_phone);

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();
        Button btn_reg = (Button) register_layout.findViewById(R.id.btnReg);

        //make sure birthdate matches required format
        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (TextUtils.isEmpty(edt_address.getText().toString())) {
                   /* edt_address.setError("Please Enter Address");
                    edt_address.requestFocus();*/
                    Toast.makeText(HomeActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_name.getText().toString())) {
                    /*edt_name.setError("Please Enter Name");
                    edt_name.requestFocus();*/
                    Toast.makeText(HomeActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_birthdate.getText().toString())) {
                    /*edt_birthdate.setError("Please Enter Birthdate");
                    edt_birthdate.requestFocus();*/
                    Toast.makeText(HomeActivity.this, "Please Enter Birthdate", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_phone.getText().toString())) {
                   /* edt_phone.setError("Phone number is required");
                    edt_phone.requestFocus();*/
                    Toast.makeText(HomeActivity.this, "Phone number is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please Wait");

                // register new User
                compositeDisposable.add(
                        rxJAPI.registerNewUser(edt_phone.getText().toString(),
                                edt_name.getText().toString(),
                                edt_birthdate.getText().toString(),
                                edt_address.getText().toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Response<User>>() {
                                    @Override
                                    public void accept(Response<User> userResponse) throws Exception {
                                        waitingDialog.dismiss();
                                        User user = userResponse.body();
                                        Common.currentUser = userResponse.body();
                                        Toast.makeText(HomeActivity.this, "User is "+ Common.currentUser.getName(), Toast.LENGTH_SHORT).show();

                                        //save user into stored preferences after login
                                        management.savePreferences(new Preferences()
                                        .setSaveUserCredential(true)
                                        .setUserName(Common.currentUser.getName())
                                        .setUserPhone(Common.currentUser.getPhone())
                                        .setUserBirthdate(Common.currentUser.getBirthdate()));

                                        // save user into room database after login
                                        Completable.fromAction(()-> Common.userRepository.addUser(user))
                                        .subscribeOn(Schedulers.io())
                                        .subscribe();
                                        Log.d("RMDB", new Gson().toJson(user));


                                    }

                                }, throwable -> {
                                    waitingDialog.dismiss();
                                    Snackbar.make(mainLayout, "Network error", Snackbar.LENGTH_SHORT)
                                            .setAction("OK", null).show();
                                    //Toast.makeText(HomeActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                })
                );

            }
        });
        dialog.show();
    }
    
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public void launchSearchActivity(View view) {
        startActivity(new Intent(HomeActivity.this, InstantSearchActivity.class));
    }
}
