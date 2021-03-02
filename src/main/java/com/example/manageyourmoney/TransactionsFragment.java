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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TransactionsFragment extends Fragment {

    private TransactionsFragment.OnTransactionsFragmentInteractionListener mListener;
    private Context context;
    private TextView card_name;
    private ArrayList<TransactionObject> incomes_list = new ArrayList<TransactionObject>();
    private ArrayList<TransactionObject> expenses_list = new ArrayList<TransactionObject>();
    public static DatabaseHelper db;
    ListView listView_incomes;
    ListView listView_expenses;
    TransactionListAdapter transactionListAdapter_incomes;
    TransactionListAdapter transactionListAdapter_expenses;

    private boolean running;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private int mParam1;
    private int mParam2;
    private String mParam3; //Card_name

    public TransactionsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(int param1, int param2, String param3) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        context = this.getActivity().getApplicationContext();

        running=true;

        card_name= view.findViewById(R.id.textView6);
        card_name.setText(mParam3);

        listView_incomes = (ListView)view.findViewById(R.id.listView_allincomes);
        listView_expenses = (ListView)view.findViewById(R.id.listView_allexpenses);
        db = new DatabaseHelper(getActivity());

        loadLists();

        listView_incomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String IncomeCategory = incomes_list.get(position).getCategory();
                String IncomeDescription = incomes_list.get(position).getDescription();
                double IncomeQuantity = incomes_list.get(position).getQuantity();
                Date IncomeDate = incomes_list.get(position).getDate();

                details_dialog(IncomeCategory, IncomeDescription, IncomeQuantity, IncomeDate);

            }
        });

        listView_expenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ExpenseCategory = expenses_list.get(position).getCategory();
                String ExpenseDescription = expenses_list.get(position).getDescription();
                double ExpenseQuantity = expenses_list.get(position).getQuantity();
                Date ExpenseDate = expenses_list.get(position).getDate();

                details_dialog(ExpenseCategory, ExpenseDescription, ExpenseQuantity, ExpenseDate);

            }
        });



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionsFragment.OnTransactionsFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (TransactionsFragment.OnTransactionsFragmentInteractionListener) context;
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

    public void update_view()
    {

        transactionListAdapter_incomes = new TransactionListAdapter(this.getActivity(), incomes_list);
        transactionListAdapter_expenses = new TransactionListAdapter(this.getActivity(), expenses_list);
        listView_incomes.setAdapter(transactionListAdapter_incomes);
        listView_expenses.setAdapter(transactionListAdapter_expenses);

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
            mListener.go_to_see_cards(mParam2);

        }
        else if (id == R.id.logout) {

            running=false;
            mListener.go_to_login();

        }

        return super.onOptionsItemSelected(item);
    }


    public void details_dialog(String category, String description, double quantity, Date date){
        String title = "TRANSACTION INFORMATION";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date_s = simpleDateFormat.format(date);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        DecimalFormat df = new DecimalFormat("0.00");
        String quantity_s = df.format(quantity);
        quantity_s+="€";
        builder.setMessage("Amount: " + quantity_s + "\nDate: " + date_s + "\nCategory: " + category + "\nDescription: " + description);

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        builder.create();
        builder.show();
    }

    public interface OnTransactionsFragmentInteractionListener {

        void go_to_login();
        void go_to_see_cards(int user_ID);

    }


    // Correr em threads paralelas
    private void loadLists(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    incomes_list = db.getListIncomes(mParam1);
                    expenses_list = db.getListExpenses(mParam1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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


}