package com.example.manageyourmoney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditCardFragment extends Fragment {


    private EditCardFragment.OnEditCardFragmentInteractionListener mListener;
    private Context context;
    public static DatabaseHelper db;
    private Spinner spinner;
    private int card_id=0;
    ArrayList<String> cardsNames= new ArrayList<String>();
    ArrayList<CardObject> CardObjectsList;
    private boolean running;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    public EditCardFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditCardFragment newInstance(int param1) {
        EditCardFragment fragment = new EditCardFragment();
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

        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);
        context = this.getActivity().getApplicationContext();
        running=true;
        spinner= (Spinner) view.findViewById(R.id.select_card);

        EditText editText = view.findViewById(R.id.editCardName);
        TextView textView = view.findViewById(R.id.textView_balance);

        db = new DatabaseHelper(getActivity());

        loadCards(); //para aparecerem no dropdown

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Setting the card type
                String balance_str="Card balance: ";

                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.gray));
                    card_id=0;
                    textView.setText(balance_str);
                    editText.setText(null);
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black));
                    double balance= CardObjectsList.get(position-1).getBalance();

                    DecimalFormat df = new DecimalFormat("0.00");

                    if (balance>0)
                    {
                        balance_str+= "+" + df.format(balance);
                    }
                    else {
                        balance_str+= df.format(balance);
                    }
                    balance_str+="€";

                    card_id=CardObjectsList.get(position-1).getId();
                    textView.setText(balance_str);
                    editText.setText(CardObjectsList.get(position-1).getCardName());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });


        // Delete card
        Button button_del = view.findViewById(R.id.del_button);
        button_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(card_id!=0)
                {
                    warning_dialog(card_id);
                }
                else{
                    Toast.makeText(context, "No card selected!" , Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Save changes
        Button button_save = view.findViewById(R.id.buttonSave);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(card_id==0)
                {
                    Toast.makeText(context, "No card selected!" , Toast.LENGTH_SHORT).show();

                }
                else if(editText.getText().toString().length()<1){
                    Toast.makeText(context, "You need to fill the Card name space!" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String new_name=editText.getText().toString();
                    Toast.makeText(context, "Card name updated!" , Toast.LENGTH_SHORT).show();
                    save_changes(new_name);
                }

            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditCardFragment.OnEditCardFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (EditCardFragment.OnEditCardFragmentInteractionListener) context;
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
            running=false;

        }
        else if (id == R.id.logout) {

            mListener.go_to_login();
            running=false;

        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnEditCardFragmentInteractionListener {
        void go_to_login();
        void go_to_see_cards(int user_ID);
    }


    public void warning_dialog(int selected_cardID)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("If you delete this card you will also delete all his transactions!");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Card deleted!" , Toast.LENGTH_SHORT).show();
                delete_card(selected_cardID);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.create();
        builder.show();
    }

    public void update_view()
    {
        cardsNames.add("--- Select a card ---");
        for (int i=0; i<CardObjectsList.size(); i++){
            cardsNames.add(CardObjectsList.get(i).getCardName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, cardsNames);
        spinner.setAdapter(adapter);

    }

    // Correr em threads paralelas
    private void loadCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                CardObjectsList=db.getListCards(mParam1);

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

    private void save_changes(String new_name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.change_card_name(card_id, new_name);
                running=false;
                mListener.go_to_see_cards(mParam1);
            }
        }).start();
    }

    private void delete_card(int selected_cardID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete_card(selected_cardID);
                running=false;
                mListener.go_to_see_cards(mParam1);
            }
        }).start();
    }

}