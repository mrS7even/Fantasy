package com.example.fantasy;

public class RecyclerViewItem {

    private String textViewNumber;
    private String textViewSortSurname;
    private String textViewClub;
    private String textViewAmplua;
    private String textViewAvarageQuantityFO;
    private String textViewIndexQuality;
    private String textViewMainIndex;

    public RecyclerViewItem(String textViewNumber, String textViewSortSurname, String textViewClub, String textViewAmplua, String textViewAvarageQuantityFO, String textViewIndexQuality, String textViewMainIndex) {
        this.textViewNumber = textViewNumber;
        this.textViewSortSurname = textViewSortSurname;
        this.textViewClub = textViewClub;
        this.textViewAmplua = textViewAmplua;
        this.textViewAvarageQuantityFO = textViewAvarageQuantityFO;
        this.textViewIndexQuality = textViewIndexQuality;
        this.textViewMainIndex = textViewMainIndex;
    }

    public String getTextViewNumber() {
        return textViewNumber;
    }

    public String getTextViewSortSurname() {
        return textViewSortSurname;
    }

    public String getTextViewClub() {
        return textViewClub;
    }

    public String getTextViewAmplua() {
        return textViewAmplua;
    }

    public String getTextViewAvarageQuantityFO() {
        return textViewAvarageQuantityFO;
    }

    public String getTextViewIndexQuality() {
        return textViewIndexQuality;
    }

    public String getTextViewMainIndex() {
        return textViewMainIndex;
    }
}
