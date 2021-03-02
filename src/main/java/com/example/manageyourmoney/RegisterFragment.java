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
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private RegisterFragment.OnRegisterFragmentInteractionListener mListener;
    private Context context;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
    public static DatabaseHelper db;
    private boolean running;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        context = this.getActivity().getApplicationContext();

        running=true;

        db = new DatabaseHelper(getActivity());

        editText1 = view.findViewById(R.id.register_username);
        editText2 = view.findViewById(R.id.register_name);
        editText3 = view.findViewById(R.id.register_surname);
        editText4 = view.findViewById(R.id.register_email);
        editText5 = view.findViewById(R.id.register_password1);
        editText6 = view.findViewById(R.id.register_password2);



        // Sign in
        Button buttonSignIn = view.findViewById(R.id.sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editText1.getText().toString();
                String name = editText2.getText().toString();
                String surname = editText3.getText().toString();
                String email = editText4.getText().toString();
                String password1 = editText5.getText().toString();
                String password2 = editText6.getText().toString();

                register_thread(username, name, surname, email, password1, password2);

            }
        });

        // Go to login
        Button buttonLogin = view.findViewById(R.id.register_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.go_to_login();
                running=false;
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnRegisterFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (RegisterFragment.OnRegisterFragmentInteractionListener) context;
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

    public interface OnRegisterFragmentInteractionListener {
        void go_to_login();
        void go_to_menu(int user_ID);
    }

    // Correr em threads paralelas
    boolean validate_register;
    private void register_thread(String username, String name, String surname, String email, String password1, String password2 ){

        new Thread(new Runnable() {
            @Override
            public void run() {

                validate_register=true;

                boolean exists_username=db.check_username(username); // run in parallel
                boolean exists_email=db.check_email(email); // run in parallel

                if(username.length()<3 || username.length()>20){
                    validate_register=false;
                }

                if(name.length()<2 || name.length()>20){
                    validate_register=false;
                }

                if(surname.length()<2 || surname.length()>20){
                    validate_register=false;
                }

                if(email.length()<1){
                    validate_register=false;
                }

                if(password1.length()<4 || password1.length()>20){
                    validate_register=false;
                }

                if(exists_username){
                    validate_register=false;
                }
                if(exists_email){
                    validate_register=false;
                }

                if (!password1.equals(password2)){
                    validate_register=false;
                }

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    // main thread-> avisos
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(username.length()<3 || username.length()>20){
                                Toast.makeText(context, "Username should have between 3 and 20 chars!" , Toast.LENGTH_SHORT).show();
                            }

                            if(name.length()<2 || name.length()>20){
                                Toast.makeText(context, "Name should have between 2 and 20 chars!" , Toast.LENGTH_SHORT).show();
                            }

                            if(surname.length()<2 || surname.length()>20){
                                Toast.makeText(context, "Surname should have between 2 and 20 chars!" , Toast.LENGTH_SHORT).show();
                            }

                            if(email.length()<1){
                                Toast.makeText(context, "Email should be filled!" , Toast.LENGTH_SHORT).show();
                            }

                            if(password1.length()<4 || password1.length()>20){
                                Toast.makeText(context, "Password should have between 4 and 20 chars!" , Toast.LENGTH_SHORT).show();
                            }

                            if(exists_username){
                                Toast.makeText(context, "Username already taken!" , Toast.LENGTH_SHORT).show();
                            }
                            if(exists_email){
                                Toast.makeText(context, "Email already used!" , Toast.LENGTH_SHORT).show();
                            }

                            if (!password1.equals(password2)){
                                Toast.makeText(context, "Passwords don't match!" , Toast.LENGTH_SHORT).show();
                            }

                            if(validate_register){
                                Toast.makeText(context, "New account created!" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // DB operation-> parallel thread
                if(validate_register)
                {
                    db.add_user(username, name, surname, email, password1);

                    // Automatically create a "main debit card" and a "money in hand" for this new user
                    int user_ID=db.get_userID(username);
                    db.new_card(user_ID, false, "Money in hand");
                    db.new_card(user_ID, true, "Main debit card");

                    running=false;
                    mListener.go_to_menu(user_ID); //Register sucess
                }

            }
        }).start();

    }


}