package com.example.manageyourmoney;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class TransferMoneyFragment extends Fragment {


    private TransferMoneyFragment.OnTransferMoneyFragmentInteractionListener mListener;
    private Context context;
    public static DatabaseHelper db;
    private int card_id_from=0;
    private int card_id_to=0;
    private int keep_position_from=0;
    private int keep_position_to=0;
    private Spinner spinner1;
    private Spinner spinner2;
    ArrayList<String> cardsNames= new ArrayList<String>();
    ArrayList<CardObject> CardObjectsList;
    private boolean running;

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;


    public TransferMoneyFragment() {
        // Required empty public constructor
    }


    public static TransferMoneyFragment newInstance(int param1) {
        TransferMoneyFragment fragment = new TransferMoneyFragment();
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

        View view = inflater.inflate(R.layout.fragment_transfer_money, container, false);
        context = this.getActivity().getApplicationContext();
        running=true;

        db = new DatabaseHelper(getActivity());

        EditText editText = view.findViewById(R.id.editTextNumberDecimal_transfer);
        EditText editText_date = view.findViewById(R.id.editTextDate2);
        spinner1 = (Spinner) view.findViewById(R.id.spinner_from);
        spinner2 = (Spinner) view.findViewById(R.id.spinner_to);

        loadCards();

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.gray));
                    card_id_from=0;
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black));
                    card_id_from=CardObjectsList.get(position-1).getId();
                }
                keep_position_from=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.gray));
                    card_id_to=0;
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black));
                    card_id_to=CardObjectsList.get(position-1).getId();
                }
                keep_position_to=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        // Transfer money
        Button button_transfer = view.findViewById(R.id.button_transfer);
        button_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double quantity=0;
                boolean checkTransfer=true;

                String quantity_str=editText.getText().toString();

                if (quantity_str.length()<1 || quantity_str.equals(".") || quantity_str.equals(",") || quantity_str.equals("-")){
                    Toast.makeText(context, "You need to fill the amount space!" , Toast.LENGTH_SHORT).show();
                    checkTransfer=false;
                }
                else{
                    quantity= Double.valueOf(quantity_str);
                    if (quantity==0){
                        Toast.makeText(context, "Transfer amount cannot be zero!" , Toast.LENGTH_SHORT).show();
                        checkTransfer=false;
                    }
                }

                if(card_id_from==0 || card_id_to==0)
                {
                    Toast.makeText(context, "Both from and to cards must be selected!" , Toast.LENGTH_SHORT).show();
                    checkTransfer=false;
                }
                else if(card_id_from==card_id_to)
                {
                    Toast.makeText(context, "Cards selected must be different!" , Toast.LENGTH_SHORT).show();
                    checkTransfer=false;
                }

                if (checkTransfer)
                {
                    String date_str = editText_date.getText().toString();
                    String description= "Transfer from card " + CardObjectsList.get(keep_position_from-1).getCardName() + " to card " + CardObjectsList.get(keep_position_to-1).getCardName();

                    Toast.makeText(context, "Transfer done!" , Toast.LENGTH_SHORT).show();

                    add_new_transfer(quantity, description, date_str);
                }

            }
        });



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransferMoneyFragment.OnTransferMoneyFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (TransferMoneyFragment.OnTransferMoneyFragmentInteractionListener) context;
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
            mListener.go_to_add_transaction(mParam1);

        }
        else if (id == R.id.logout) {

            running=false;
            mListener.go_to_login();

        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnTransferMoneyFragmentInteractionListener {
        void go_to_add_transaction(int user_ID);
        void go_to_login();
    }


    public void update_view()
    {
        cardsNames.add("--- Select a card ---");
        CardObjectsList = db.getListCards(mParam1);
        for (int i=0; i<CardObjectsList.size(); i++){
            cardsNames.add(CardObjectsList.get(i).getCardName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, cardsNames);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

    // Correr em threads paralelas
    private void loadCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                CardObjectsList = db.getListCards(mParam1);

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            update_view();

                        }
                    });
                }

            }
        }).start();
    }

    private void add_new_transfer(double quantity, String description, String date_str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                running=false;
                db.addTransaction(false, quantity, card_id_from, "Transfer", description, date_str); //addExpense
                db.addTransaction(true, quantity, card_id_to, "Transfer", description, date_str); //addIncome
                mListener.go_to_add_transaction(mParam1);
            }
        }).start();
    }

}