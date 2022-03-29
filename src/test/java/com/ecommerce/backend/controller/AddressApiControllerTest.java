package com.ecommerce.backend.controller;

class AddressApiControllerTest {

}

class Hero{
    private String name;
    private String title;

    public Hero(){
        this.name = "hJunsu";
        this.title = "hTitle";
        System.out.println("Super class");
    }
    void walk(){
        System.out.println("Hero walk");
    }

    String getName(){
        return name;
    }

}

class Person extends Hero{
    private String pName;
    private String pTitle;

    public Person(){
        super();
        System.out.println("Sub class");
        this.pName = "personName";
        this.pTitle = "personTitle";
    }

    String getpName(){
        return pName;
    }

    void walk(){
        System.out.println("Person walk");
    }

    public static void main(String[] args) {
        Person person = new Person();

        System.out.println(person.pName);
        System.out.println(person.pTitle);
        System.out.println(person.getName());
        person.walk();

    }
}