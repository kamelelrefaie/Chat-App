package com.example.lapitchat.view.fragment.userCycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.helper.notification.Token;
import com.example.lapitchat.view.activity.MainActivity;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;
import static com.example.lapitchat.helper.HelperMethods.updateToken;

public class LoginFragment extends BaseFragment {

    @BindView(R.id.login_fragment_til_email)
    TextInputLayout loginFragmentTilEmail;
    @BindView(R.id.login_fragment_til_password)
    TextInputLayout loginFragmentTilPassword;
    @BindView(R.id.login_fragment_btn_login)
    Button loginFragmentBtnLogin;
    @BindView(R.id.login_fragment_ll)
    LinearLayout loginFragmentLl;
    @BindView(R.id.login_fragment_rl)
    RelativeLayout loginFragmentRl;

    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;
    Unbinder unbinder;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        //get user auth
        mAuth = FirebaseAuth.getInstance();
        // initialize loading dialog
        loadingDialog = new LoadingDialog(getActivity());
        //setting start activity
        setUpActivity();
        // set toolbar
        setLoginToolBar(view);

        return view;


    }


    private void setLoginToolBar(View view) {
        startActivity.setToolBar(view.VISIBLE, getString(R.string.create_account), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to start fragment
                replaceFragment(getActivity().getSupportFragmentManager(), R.id.start_activity_frame, new StartFragment());
            }
        });
    }


    @OnClick(R.id.login_fragment_btn_login)
    public void onViewClicked() {
        // getting email and password from edit text
        String sEmail = loginFragmentTilEmail.getEditText().getText().toString();
        String sPassword = loginFragmentTilPassword.getEditText().getText().toString();

        if (!TextUtils.isEmpty(sEmail) || !TextUtils.isEmpty(sPassword)) {
            loadingDialog.startLoadingDialog();
            loginUser(sEmail, sPassword);
        }

    }

    private void loginUser(String sEmail, String sPassword) {
        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingDialog.dismissDialog();
                            //get user token
                            updateToken();

                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            loadingDialog.dismissDialog();
                            Toast.makeText(getActivity(), "Login Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}