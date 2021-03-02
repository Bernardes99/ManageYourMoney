package com.example.manageyourmoney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;


public class ProfileFragment extends Fragment {

    private ProfileFragment.OnProfileFragmentInteractionListener mListener;
    private Context context;
    public static DatabaseHelper db;
    private UserObject user;

    private TextView profile_name;
    private TextView profile_username;
    private EditText edit_username;
    private EditText edit_name;
    private EditText edit_surname;
    private EditText edit_email;
    private boolean running;


    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(int param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = this.getActivity().getApplicationContext();
        running=true;

        db = new DatabaseHelper(getActivity());

        profile_name= view.findViewById(R.id.profile_name_);
        profile_username= view.findViewById(R.id.profile_username_);;
        edit_username= view.findViewById(R.id.edit_username_profile);;
        edit_name= view.findViewById(R.id.edit_name_profile);;
        edit_surname= view.findViewById(R.id.edit_surname_profile);;
        edit_email= view.findViewById(R.id.edit_email_profile);;

        loadData();

        // Save changes
        Button button_save_information = view.findViewById(R.id.button_save_info);
        button_save_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validate_changes=true;

                String username = edit_username.getText().toString();
                String name = edit_name.getText().toString();
                String surname = edit_surname.getText().toString();
                String email = edit_email.getText().toString();

                if(username.length()<3 || username.length()>20){
                    validate_changes=false;
                    Toast.makeText(context, "Username should have between 3 and 20 chars!" , Toast.LENGTH_SHORT).show();
                }

                if(name.length()<2 || name.length()>20){
                    validate_changes=false;
                    Toast.makeText(context, "Name should have between 2 and 20 chars!" , Toast.LENGTH_SHORT).show();
                }

                if(surname.length()<2 || surname.length()>20){
                    validate_changes=false;
                    Toast.makeText(context, "Surname should have between 2 and 20 chars!" , Toast.LENGTH_SHORT).show();
                }

                if(email.length()<1){
                    validate_changes=false;
                    Toast.makeText(context, "Email should be filled!" , Toast.LENGTH_SHORT).show();
                }

                if (validate_changes){
                    change_profile_thread(username, name, surname, email);
                }
            }
        });

        // Delete card
        Button button_change_password = view.findViewById(R.id.button_change_password);
        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_dialog();

            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnProfileFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (ProfileFragment.OnProfileFragmentInteractionListener) context;
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


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.back) {

            running=false;
            mListener.go_to_menu(mParam1);

        }
        else if (id == R.id.logout)
        {
            running=false;
            mListener.go_to_login();
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnProfileFragmentInteractionListener {
        void go_to_menu(int user_ID);
        void go_to_login();
    }


    public void password_dialog(){

        // Pop new dialog to the user can choose add a category and a description to the transaction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setTitle("Change password:");
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        builder.setView(view);

        EditText edit_old_pass = view.findViewById(R.id.old_pass);
        EditText edit_new_pass = view.findViewById(R.id.new_pass);
        EditText edit_confirm_new_pass = view.findViewById(R.id.new_pass_confirm);

        // Saving the new title for the note
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                boolean validate_change_password=true;

                String old_pass_str= edit_old_pass.getText().toString();
                String new_pass_str= edit_new_pass.getText().toString();
                String new_pass_confirm_str= edit_confirm_new_pass.getText().toString();

                if (!old_pass_str.equals(user.getPassword())){
                    Toast.makeText(context, "Wrong old password!" , Toast.LENGTH_SHORT).show();
                    validate_change_password=false;
                }

                if (!new_pass_confirm_str.equals(new_pass_str)){
                    Toast.makeText(context, "New passwords don't match!" , Toast.LENGTH_SHORT).show();
                    validate_change_password=false;
                }

                if(new_pass_str.length()<4 || new_pass_str.length()>20){
                    validate_change_password=false;
                    Toast.makeText(context, "Password should have between 4 and 20 chars!" , Toast.LENGTH_SHORT).show();
                }

                if(validate_change_password){
                    Toast.makeText(context, "Password changed with success!" , Toast.LENGTH_SHORT).show();

                    delete_password_thread(new_pass_str);
                }

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        builder.create();
        builder.show();
    }

    // Correr em threads paralelas
    private void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                user=db.get_user(mParam1);

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String full_name= user.getName() + " " + user.getSurname();
                            profile_name.setText(full_name);
                            String username_view= "@" + user.getUsername();
                            profile_username.setText(username_view);
                            edit_username.setText(user.getUsername());
                            edit_name.setText(user.getName());
                            edit_surname.setText(user.getSurname());
                            edit_email.setText(user.getEmail());
                        }
                    });
                }

            }
        }).start();
    }

    // Correr em threads paralelas
    boolean validate_changes_2;
    private void change_profile_thread(String username, String name, String surname, String email){

        new Thread(new Runnable() {
            @Override
            public void run() {

                validate_changes_2=true;

                //diferente do registo, aqui compara com todos os users menos o próprio
                boolean exists_username=db.check_username_change(username, mParam1);
                boolean exists_email=db.check_email_change(email, mParam1);

                if(exists_username){
                    validate_changes_2=false;
                }
                if(exists_email){
                    validate_changes_2=false;
                }

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    // main thread-> avisos
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(exists_username){
                                Toast.makeText(context, "Username already taken!" , Toast.LENGTH_SHORT).show();
                            }
                            if(exists_email){
                                Toast.makeText(context, "Email already used!" , Toast.LENGTH_SHORT).show();
                            }
                            if(validate_changes_2){
                                Toast.makeText(context, "Changes Saved!" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // DB operation-> parallel thread
                if(validate_changes_2)
                {
                    running=false;
                    db.update_user_info(mParam1, username, name, surname, email);
                    mListener.go_to_menu(mParam1);
                }

            }
        }).start();

    }

    private void delete_password_thread(String new_pass_str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                running=false;
                db.change_password(mParam1, new_pass_str);
                mListener.go_to_menu(mParam1);
            }
        }).start();
    }

}