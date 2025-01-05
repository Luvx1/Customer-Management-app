package com.example.customermanagement.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private String _ID;
    private String _Name;
    private Boolean Gender;
    private String _Age;
    private String _Address;
    private String _Salary;
    private String _DelteDate;

    // Full constructor
    public Customer(String _ID, String _Name, Boolean gender, String _Age, String _Address, String _Salary) {
        this._ID = _ID;
        this._Name = _Name;
        this.Gender = gender;
        this._Age = _Age;
        this._Address = _Address;
        this._Salary = _Salary;
    }

    public Customer(String _Name, Boolean gender, String _Age, String _Address, String _Salary) {
        this._Name = _Name;
        Gender = gender;
        this._Age = _Age;
        this._Address = _Address;
        this._Salary = _Salary;
    }

    public Customer(String _Name, Boolean gender, String _Address, String _Age, String _Salary, String _DelteDate) {
        this._Name = _Name;
        Gender = gender;
        this._Address = _Address;
        this._Age = _Age;
        this._Salary = _Salary;
        this._DelteDate = _DelteDate;
    }

    public Customer(String _ID, String _Name, Boolean gender, String _Age, String _Address, String _Salary, String _DelteDate) {
        this._ID = _ID;
        this._Name = _Name;
        Gender = gender;
        this._Age = _Age;
        this._Address = _Address;
        this._Salary = _Salary;
        this._DelteDate = _DelteDate;
    }

    // Getter and Setter methods
    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String get_Name() {
        return _Name;
    }

    public void set_Name(String _Name) {
        this._Name = _Name;
    }

    public Boolean getGender() {
        return Gender;
    }

    public void setGender(Boolean gender) {
        Gender = gender;
    }

    public String get_Age() {
        return _Age;
    }

    public void set_Age(String _Age) {
        this._Age = _Age;
    }

    public String get_Address() {
        return _Address;
    }

    public void set_Address(String _Address) {
        this._Address = _Address;
    }

    public String get_Salary() {
        return _Salary;
    }

    public void set_Salary(String _Salary) {
        this._Salary = _Salary;
    }

    public String get_DelteDate() {
        return _DelteDate;
    }

    public void set_DelteDate(String _DelteDate) {
        this._DelteDate = _DelteDate;
    }
}
