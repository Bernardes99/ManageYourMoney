package com.example.manageyourmoney;

public class CardObject {

    int id;
    boolean type;
    int user_id;
    String cardName;
    double balance;

    public CardObject(int id, boolean type, int user_id, String cardName, double balance){

        this.id=id;
        this.type=type;
        this.user_id=user_id;
        this.cardName=cardName;
        this.balance=balance;
    }

    public CardObject() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
