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
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    private MenuFragment.OnMenuFragmentInteractionListener mListener;
    private Context context;

    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance(int param1) {
        MenuFragment fragment = new MenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        context = this.getActivity().getApplicationContext();


        ImageButton ibutton_cards = (ImageButton)view.findViewById(R.id.imageButton_cards);
        ibutton_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.go_to_see_cards(mParam1);

            }
        });

        ImageButton ibutton_transactions = (ImageButton)view.findViewById(R.id.imageButton_transactions);
        ibutton_transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.go_to_add_transaction(mParam1);

            }
        });

        ImageButton ibutton_statistics = (ImageButton)view.findViewById(R.id.imageButton_statistics);
        ibutton_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.go_to_statistics(mParam1);

            }
        });

        ImageButton ibutton_profile = (ImageButton)view.findViewById(R.id.imageButton_profile);
        ibutton_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.go_to_profile(mParam1);

            }
        });

        Button button_logout = view.findViewById(R.id.button_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.go_to_login();
                Toast.makeText(context, "Logout!" , Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuFragment.OnMenuFragmentInteractionListener) {

            // This will initialize the variable. It will return an exception if it is not
            //  implemented in the java code of the variable context (in our case the
            //  context is the MainActivity.
            mListener = (MenuFragment.OnMenuFragmentInteractionListener) context;
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

    public interface OnMenuFragmentInteractionListener {

        void go_to_profile(int user_ID);;
        void go_to_login();
        void go_to_statistics(int user_ID);
        void go_to_add_transaction(int user_ID);
        void go_to_see_cards(int user_ID);
    }
}