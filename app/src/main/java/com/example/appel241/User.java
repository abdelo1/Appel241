package com.example.appel241;

public class User {
  private String name;
  private String number;

public User(String name ,String number){
  this.name = name;
  this.number = number;
}
public  User(){

}
  public void setNumber(String number) {
    this.number = number;
  }

  public String getNumber() {
    return this.number;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}