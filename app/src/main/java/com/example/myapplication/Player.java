package com.example.myapplication;

public class Player {


//            "id":1887865,
//            "amplua":5,
//            "tag_id":161004301,
//            "club_id":4181,
//            "club":"Денвер",
//            "sport_id":210,
//            "start_value":3000,
//            "lock":"0",
//            "points":1083,
//            "name":"Йокич",
//            "sport_name":"basketball",
//            "price":"4083",
//            "delta":"1083",
//            "img":"https:\/\/s5o.ru\/fantasy\/images\/shirts\/basketball\/nba\/Denver-Nuggets.gif",
//            "sort_surname":"Йокич"

    int id;
    int amplua;
    String club;
    int start_value;
    int points;
    String name;
    String sort_surname;

    int shortPrice;
    int quantityOfGame;
    double indexQuality;
    double mainIndex;

    double averagePointsPerMatch;



    public Player(int id, int amplua, String club, int start_value, int points, String name, String sort_surname) {
        this.id = id;
        this.amplua = amplua;
        this.club = club;
        this.start_value = start_value;
        this.points = points;
        this.name = name;
        this.sort_surname = sort_surname;
    }
}
