package com.example.lapitchat.view.fragment.userCycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lapitchat.R;
import com.example.lapitchat.view.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class StartFragment extends BaseFragment {

    @BindView(R.id.start_fragment_btn_reg)
    Button startFragmentBtnReg;
    Unbinder unbinder;
    @BindView(R.id.start_fragment_btn_sign)
    Button startFragmentBtnSign;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        unbinder = ButterKnife.bind(this, view);

        // set StartActivity
        setUpActivity();


        // to remove toolbar
        startActivity.setToolBar(view.GONE, null, null);

        return view;
    }


    @OnClick({R.id.start_fragment_btn_sign, R.id.start_fragment_btn_reg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_fragment_btn_sign:
                // in case of pressing sign button move page to login page
                replaceFragment(getActivity().getSupportFragmentManager(), R.id.start_activity_frame, new LoginFragment());
                break;
            case R.id.start_fragment_btn_reg:
                // in case of pressing register button move page to register page
                replaceFragment(getActivity().getSupportFragmentManager(), R.id.start_activity_frame, new RegisterFragment());
                break;
        }
    }
}
