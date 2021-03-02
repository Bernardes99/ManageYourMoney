package com.example.manageyourmoney;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private OnLoginFragmentInteractionListener  mListener;
    private Context context;
    private EditText editText1;
    private EditText editText2;
    public static DatabaseHelper db;
    private boolean running;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = this.getActivity().getApplicationContext();

        running=true;

        db = new DatabaseHelper(getActivity());

        editText1 = view.findViewById(R.id.login_username);
        editText2 = view.findViewById(R.id.login_password);

        // Login
        Button buttonLogin = view.findViewById(R.id.login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editText1.getText().toString();
                String password = editText2.getText().toString();

                login_thread(username, password);
            }
        });

        // Register
        Button buttonRegister = view.findViewById(R.id.register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running=false;
                mListener.go_to_register();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener ) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.back).setVisible(false);
        menu.findItem(R.id.logout).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public interface OnLoginFragmentInteractionListener {
        void go_to_register();
        void go_to_menu(int user_ID);
    }


    // Correr em threads paralelas
    private void login_thread(String username, String password){
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean login=db.login_user(username, password);

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(login){
                                Toast.makeText(context, "User logged!" , Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(context, "Wrong username or password!" , Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(login){
                    int user_ID=db.get_userID(username);
                    running=false;
                    mListener.go_to_menu(user_ID);
                }

            }
        }).start();
    }


}