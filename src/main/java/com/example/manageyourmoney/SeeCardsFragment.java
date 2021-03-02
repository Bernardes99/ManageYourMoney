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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class SeeCardsFragment extends Fragment {


    private SeeCardsFragment.OnSeeCardsFragmentInteractionListener mListener;
    private Context context;
    private ArrayList<CardObject> cardsObjects = new ArrayList<CardObject>();
    private double totalBalance;
    public static DatabaseHelper db;
    ListView listView;
    TextView textview;
    CardListAdapter cardListAdapter;
    private boolean running;

    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private int mParam1;


    public SeeCardsFragment() {
        // Required empty public constructor
    }


    public static SeeCardsFragment newInstance(int param1) {
        SeeCardsFragment fragment = new SeeCardsFragment();
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
        View view = inflater.inflate(R.layout.fragment_see_cards, container, false);
        context = this.getActivity().getApplicationContext();
        running=true;


        listView=(ListView)view.findViewById(R.id.listView_main_);
        textview = view.findViewById(R.id.total_balanceTXT_);

        db = new DatabaseHelper(getActivity());

        loadCards();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selected_cardID=cardsObjects.get(position).getId();

                running=false;
                mListener.open_transactions_list(selected_cardID, mParam1, cardsObjects.get(position).getCardName());

            }
        });


        Button button_add_card = view.findViewById(R.id.button_cards_add_card);
        button_add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running=false;
                mListener.go_to_add_card(mParam1);
            }
        });

        Button button_edit_card = view.findViewById(R.id.button_cards_edit_card);
        button_edit_card .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running=false;
                mListener.go_to_edit_card(mParam1);
            }
        });



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SeeCardsFragment.OnSeeCardsFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (SeeCardsFragment.OnSeeCardsFragmentInteractionListener) context;
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

    public void update_view()
    {
        // Vai buscar os valores à base de dados e atualiza os balanços e a lista de cartões

        //double totalBalance= db.getUserBalance(mParam1);
        String txt_total_balance="";

        DecimalFormat df = new DecimalFormat("0.00");

        if (totalBalance>0)
        {
            txt_total_balance = "+" + df.format(totalBalance);
        }
        else {
            txt_total_balance = df.format(totalBalance);
        }
        txt_total_balance+="€";

        textview.setText("Your total balance: " + txt_total_balance);
        //cardsObjects=db.getListCards(mParam1);

        cardListAdapter= new CardListAdapter(this.getActivity(), cardsObjects);
        listView.setAdapter(cardListAdapter);
    }

    public interface OnSeeCardsFragmentInteractionListener {

        void go_to_login();
        void go_to_menu(int user_ID);
        void go_to_add_card(int user_ID);
        void go_to_edit_card(int user_ID);
        void open_transactions_list(int card_ID, int user_ID, String card_name);
    }


    // Correr em threads paralelas
    private void loadCards(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                /*try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } // para testar se está a funcionar*/

                cardsObjects=db.getListCards(mParam1);
                totalBalance= db.getUserBalance(mParam1);

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