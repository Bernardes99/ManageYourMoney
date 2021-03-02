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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCardFragment extends Fragment {

    private AddCardFragment.OnAddCardFragmentInteractionListener mListener;
    private Context context;
    public static DatabaseHelper db;
    private Spinner spinner;
    private boolean type;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    public AddCardFragment() {
        // Required empty public constructor
    }

    public static AddCardFragment newInstance(int param1) {
        AddCardFragment fragment = new AddCardFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_card, container, false);
        context = this.getActivity().getApplicationContext();



        EditText editText = view.findViewById(R.id.new_card_name);
        EditText editText2 = view.findViewById(R.id.current_balance);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = year + "-" + month + "-" + day;

        db = new DatabaseHelper(getActivity());

        spinner= (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.options, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Setting the card type
                if (parent.getItemAtPosition(position).equals("Debit/Credit Card")) {
                   type=true;
                }
                else {
                    type=false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });


        // Adding a new card
        Button button_addCard = view.findViewById(R.id.button_add_card);
        button_addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkpoint=true;
                String new_cardName=editText.getText().toString();
                String balance_str=editText2.getText().toString();

                double balance =0; //default
                if (balance_str.length()<1 || balance_str.equals(".") || balance_str.equals(",") || balance_str.equals("-")){
                    Toast.makeText(context, "You need to fill the current balance space!" , Toast.LENGTH_SHORT).show();
                    checkpoint=false;
                }
                else{
                    balance= Double.valueOf(balance_str);
                }

                if (new_cardName.length()<1) {
                    Toast.makeText(context, "You need to fill the Card name space!" , Toast.LENGTH_SHORT).show();
                    checkpoint=false;
                }

                if (checkpoint){
                    Toast.makeText(context, "New card added", Toast.LENGTH_SHORT).show();

                    add_new_card(new_cardName, balance, date);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddCardFragment.OnAddCardFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (AddCardFragment.OnAddCardFragmentInteractionListener) context;
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

            mListener.go_to_see_cards(mParam1);

        }
        else if (id == R.id.logout) {

            mListener.go_to_login();

        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnAddCardFragmentInteractionListener {
        void go_to_login();
        void go_to_see_cards(int user_ID);
    }


    // Threads paralelas
    private void add_new_card(String new_cardName, double balance, String date){
        new Thread(new Runnable() {
            @Override
            public void run() {

                db.new_card(mParam1, type, new_cardName);
                ArrayList<CardObject> CardObjectsList = db.getListCards(mParam1); // vai buscar a nova lista de cartões do user já com o cartão novo
                int new_cardID=CardObjectsList.get((CardObjectsList.size() - 1)).getId(); //vai buscar o id do último cartão que vai ser o agora adicionado

                if(balance>0){
                    db.addTransaction(true, balance, new_cardID, "Initial Balance", "Original positive balance of the card", date);

                }
                else if(balance<0){
                    double balance_=-balance;
                    db.addTransaction(false, balance_, new_cardID, "Initial Balance", "Original negative balance of the card", date);
                }

                mListener.go_to_see_cards(mParam1);

            }
        }).start();
    }
}