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
import android.widget.Spinner;
import android.widget.TextView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StatisticsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private int mParam1;

    private StatisticsFragment.OnStatisticsFragmentInteractionListener mListener;
    private Context context;
    public static DatabaseHelper db;
    private Spinner spinner;
    ArrayList<String> cardsNames= new ArrayList<String>();
    ArrayList<CardObject> CardObjectsList;
    private int card_ID=0;
    private int keep_position_from=0;
    private boolean running;

    private ArrayList<TransactionObject> incomes_stats = new ArrayList<TransactionObject>();
    private ArrayList<TransactionObject> expenses_stats = new ArrayList<TransactionObject>();

    AnyChartView anyChartView_incomes;
    AnyChartView anyChartView_expenses;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    public static StatisticsFragment newInstance(int param1) {
        StatisticsFragment fragment = new StatisticsFragment();
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
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        context = this.getActivity().getApplicationContext();
        running=true;

        spinner = (Spinner) view.findViewById(R.id.spinner_cards);

        anyChartView_incomes = view.findViewById(R.id.pie1);
        APIlib.getInstance().setActiveAnyChartView(anyChartView_incomes);
        Pie pie_incomes = AnyChart.pie();
        anyChartView_incomes.setChart(pie_incomes);

        anyChartView_expenses = view.findViewById(R.id.pie2);
        APIlib.getInstance().setActiveAnyChartView(anyChartView_expenses);
        Pie pie_expenses = AnyChart.pie();
        anyChartView_expenses.setChart(pie_expenses);

        db = new DatabaseHelper(getActivity());

        loadCardsSpinner();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.gray));
                    card_ID=0;
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black));
                    card_ID=CardObjectsList.get(position-1).getId();

                    APIlib.getInstance().setActiveAnyChartView(anyChartView_incomes);
                    update_view_incomes(card_ID, pie_incomes); //Threads separadas

                    APIlib.getInstance().setActiveAnyChartView(anyChartView_expenses);
                    update_view_expenses(card_ID, pie_expenses); //Threads separadas
                }
                keep_position_from=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });



        return view;
    }

    public void update_spinner(){
        cardsNames.add("--- Select a card ---");
        for (int i=0; i<CardObjectsList.size(); i++){
            cardsNames.add(CardObjectsList.get(i).getCardName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, cardsNames);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StatisticsFragment.OnStatisticsFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (StatisticsFragment.OnStatisticsFragmentInteractionListener) context;
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

    public interface OnStatisticsFragmentInteractionListener {

        void go_to_menu(int user_ID);
        void go_to_login();

    }

    private void loadCardsSpinner(){
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

                            update_spinner();

                        }
                    });
                }

            }
        }).start();
    }

    private void update_view_incomes(int card_ID, Pie pie_incomes){
        new Thread(new Runnable() {
            @Override
            public void run() {

                incomes_stats = db.getIncomes_Statistics(card_ID);
                List<DataEntry> dataEntries_incomes = new ArrayList<>();

                for (int i = 0; i < incomes_stats.size(); i++)
                {
                    dataEntries_incomes.add(new ValueDataEntry(incomes_stats.get(i).getCategory(), incomes_stats.get(i).getQuantity()));
                }

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            APIlib.getInstance().setActiveAnyChartView(anyChartView_incomes);
                            pie_incomes.data(dataEntries_incomes);

                        }
                    });
                }

            }
        }).start();
    }

    private void update_view_expenses(int card_ID, Pie pie_expenses){
        new Thread(new Runnable() {
            @Override
            public void run() {

                expenses_stats = db.getExpenses_Statistics(card_ID);
                List<DataEntry> dataEntries_expenses = new ArrayList<>();

                for (int i = 0; i < expenses_stats.size(); i++)
                {
                    dataEntries_expenses.add(new ValueDataEntry(expenses_stats.get(i).getCategory(), expenses_stats.get(i).getQuantity()));
                }

                //para não crashar caso saia do fragmento (running fica falso se o user sair do fragmento)
                if(running)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            APIlib.getInstance().setActiveAnyChartView(anyChartView_expenses);
                            pie_expenses.data(dataEntries_expenses);

                        }
                    });
                }

            }
        }).start();
    }
}