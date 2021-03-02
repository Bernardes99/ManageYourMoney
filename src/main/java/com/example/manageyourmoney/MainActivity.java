package com.example.manageyourmoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener, AddCardFragment.OnAddCardFragmentInteractionListener,
        TransactionsFragment.OnTransactionsFragmentInteractionListener, EditCardFragment.OnEditCardFragmentInteractionListener,
        TransferMoneyFragment.OnTransferMoneyFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener,
        StatisticsFragment.OnStatisticsFragmentInteractionListener, AddTransactionFragment.OnAddTransactionFragmentInteractionListener,
        SeeCardsFragment.OnSeeCardsFragmentInteractionListener, MenuFragment.OnMenuFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        openLoginFragment();
    }


    // Create a new instance of LoginFragment
    public void openLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, loginFragment, "fragLogin");
        fragmentTransaction.commit();
    }

    // Create a new instance of RegisterFragment
    public void openRegisterFragment() {
        RegisterFragment registerFragment = RegisterFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, registerFragment, "fragRegister");
        fragmentTransaction.commit();
    }


    // Create a new instance of MainFragment
    public void openMenuFragment(int user_ID) {
        MenuFragment menuFragment = MenuFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, menuFragment, "fragMenu");
        fragmentTransaction.commit();
    }

    // Create a new instance of AddCardFragment
    public void openAddCardFragment(int user_ID) {
        AddCardFragment addCardFragment = AddCardFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, addCardFragment, "fragAddCard");
        fragmentTransaction.commit();
    }
    
    public void openTransactionsFragment (int card_ID, int user_ID, String card_name) {
        TransactionsFragment transactionsFragment = TransactionsFragment.newInstance(card_ID, user_ID, card_name);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, transactionsFragment, "fragTransactions");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openEditCardFragment(int user_ID) {
        EditCardFragment editCardFragment = EditCardFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, editCardFragment, "fragEditCard");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openTransferMoneyFragment(int user_ID) {
        TransferMoneyFragment transferMoneyFragment = TransferMoneyFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, transferMoneyFragment, "fragTransfer");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openProfileFragment(int user_ID) {
        ProfileFragment profileFragment = ProfileFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, profileFragment, "fragProfile");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openStatisticsFragment(int user_ID) {
        StatisticsFragment statisticsFragment = StatisticsFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, statisticsFragment, "fragStatistics");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openAddTransactionFragment(int user_ID) {
        AddTransactionFragment addTransactionFragment = AddTransactionFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, addTransactionFragment, "fragAddTransaction");
        fragmentTransaction.commit();
    }

    // Create a new instance
    public void openSeeCardsFragment(int user_ID) {
        SeeCardsFragment seeCardsFragment = SeeCardsFragment.newInstance(user_ID);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, seeCardsFragment, "fragSeeCards");
        fragmentTransaction.commit();
    }


    @Override
    public void go_to_register() {

        openRegisterFragment();

    }

    @Override
    public void go_to_login() {

        openLoginFragment();
    }

    @Override
    public void go_to_menu(int user_ID) {

        openMenuFragment(user_ID);
    }

    @Override
    public void go_to_add_card(int user_ID) {

        openAddCardFragment(user_ID);
    }
    
    public void open_transactions_list(int card_ID, int user_ID, String card_name) {
        openTransactionsFragment(card_ID, user_ID, card_name);
    }

    @Override
    public void go_to_edit_card(int user_ID) {

        openEditCardFragment(user_ID);
    }

    @Override
    public void go_to_transfer_money(int user_ID) {

        openTransferMoneyFragment(user_ID);
    }

    @Override
    public void go_to_profile(int user_ID) {

        openProfileFragment(user_ID);
    }

    @Override
    public void go_to_statistics(int user_ID) {

        openStatisticsFragment(user_ID);
    }


    @Override
    public void go_to_add_transaction(int user_ID) {

        openAddTransactionFragment(user_ID);
    }

    @Override
    public void go_to_see_cards(int user_ID) {

        openSeeCardsFragment(user_ID);
    }

}