package com.tamagochi.model;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String nome;
    private String userUID;

    public User(String email, String nome, String userUID) {
        this.email = email;
        this.nome = nome;
        this.userUID = userUID;
    }

    @Override
    public String toString(){
     return this.email + " " +
             this.nome + " " +
             this.userUID + " ";
    }
}
