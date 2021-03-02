package com.example.manageyourmoney;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTransactionFragment extends Fragment {


    private AddTransactionFragment.OnAddTransactionFragmentInteractionListener mListener;
    private Context context;
    private ArrayList<CardObject> cardObjectsList = new ArrayList<CardObject>();
    private Spinner spinner;
    private  int card_id=0;
    ArrayList<String> cardsNames= new ArrayList<String>();
    public static DatabaseHelper db;
    EditText editTextQt;
    EditText editTextDate;
    EditText editTextDescription;
    private boolean running;
    //DatePickerDialog.OnDateSetListener setListener;

    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    public static AddTransactionFragment newInstance(int param1) {
        AddTransactionFragment fragment = new AddTransactionFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        context = this.getActivity().getApplicationContext();

        running=true;
        db = new DatabaseHelper(getActivity());

        editTextQt = view.findViewById(R.id.editTextNumberDecimal_);
        editTextDate = view.findViewById(R.id.editTextDate_);
        editTextDescription = view.findViewById(R.id.editTextTextMultiLine_description);
        spinner = (Spinner) view.findViewById(R.id.spinner_card_addT);

        loadCards();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.gray));
                    card_id=0;
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black));
                    card_id=cardObjectsList.get(position-1).getId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        /*Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);*/

/*       editTextDate.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String date = year + "-" + month + "-" + day;
                        editTextDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });*/


        // Adding values to income or expense
        Button button_income = view.findViewById(R.id.income_button_);
        button_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validate_transaction=true;

                String quantity_str=editTextQt.getText().toString();
                String date_str = editTextDate.getText().toString();
                String description= editTextDescription.getText().toString();

                if (description.length()<1){
                    description="No description available";
                }

                //Meter aqui condição para quando a transação é 0€ ??
                if (quantity_str.length()<1 || quantity_str.equals(".") || quantity_str.equals(",") || quantity_str.equals("-")){
                    Toast.makeText(context, "You need to fill the quantity space!" , Toast.LENGTH_SHORT).show();
                    validate_transaction=false;
                }

                if (card_id==0){
                    Toast.makeText(context, "You need to select a card!" , Toast.LENGTH_SHORT).show();
                    validate_transaction=false;
                }

                if(validate_transaction){
                    double quantity= Double.valueOf(quantity_str);
                    choose_category_dialog(true, quantity, description, date_str);
                }

            }
        });

        Button button_expense = view.findViewById(R.id.expense_button_);
        button_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validate_transaction=true;

                String quantity_str=editTextQt.getText().toString();
                String date_str = editTextDate.getText().toString();
                String description= editTextDescription.getText().toString();

                if (description.length()<1){
                    description="No description available";
                }

                //Meter aqui condição para quando a transação é 0€ ??
                if (quantity_str.length()<1 || quantity_str.equals(".") || quantity_str.equals(",") || quantity_str.equals("-")){
                    Toast.makeText(context, "You need to fill the quantity space!" , Toast.LENGTH_SHORT).show();
                    validate_transaction=false;
                }

                if (card_id==0){
                    Toast.makeText(context, "You need to select a card!" , Toast.LENGTH_SHORT).show();
                    validate_transaction=false;
                }

                if(validate_transaction){
                    double quantity= Double.valueOf(quantity_str);
                    choose_category_dialog(false, quantity, description, date_str);
                }

            }
        });

        Button button_goTo_transfer = view.findViewById(R.id.button_go_to_transfer);
        button_goTo_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.go_to_transfer_money(mParam1);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddTransactionFragment.OnAddTransactionFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (AddTransactionFragment.OnAddTransactionFragmentInteractionListener) context;
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
        else if (id == R.id.logout) {

            running=false;
            mListener.go_to_login();

        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnAddTransactionFragmentInteractionListener {

        void go_to_login();
        void go_to_menu(int user_ID);;
        void go_to_transfer_money(int user_ID);

    }


    int selectedItem;
    public void choose_category_dialog(boolean positive_quantity, double quantity, String description, String date){

        int size=0;
        String title="";
        if(positive_quantity){
            title="Choose the card where you want to add this income:";
            size=5;
        }
        else{
            title="Choose the card where you want to add this expense:";
            size=9;
        }

        selectedItem=0; //To save selected item index (0 default)

        String[] options = new String [size];
        if(positive_quantity){
            options[0]="Salary";
            options[1]="Subsidies";
            options[2]="Prizes";
            options[3]="Interest";
            options[4]="Others";
        }
        else{
            options[0]="Home Bills";
            options[1]="Food";
            options[2]="Transportation Bills";
            options[3]="Technologies";
            options[4]="Health";
            options[5]="Insurances";
            options[6]="Education";
            options[7]="Leisure";
            options[8]="Others";
        }

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);


        builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem=which;
            }
        });
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                add_new_transaction(positive_quantity, quantity, card_id, options[selectedItem], description, date);
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

    public void update_view()
    {
        cardObjectsList=db.getListCards(mParam1);
        cardsNames.add("--- Select a card ---");
        for (int i=0; i<cardObjectsList.size(); i++){
            cardsNames.add(cardObjectsList.get(i).getCardName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, cardsNames);
        spinner.setAdapter(adapter);
    }


    // Correr em threads paralelas
    private void loadCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                cardObjectsList=db.getListCards(mParam1);

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

    private void add_new_transaction(boolean positive_quantity, double quantity, int card_id, String selected_category, String description, String date ){
        new Thread(new Runnable() {
            @Override
            public void run() {
                running=false;
                db.addTransaction(positive_quantity, quantity, card_id, selected_category, description, date);
                mListener.go_to_menu(mParam1);

            }
        }).start();
    }

}