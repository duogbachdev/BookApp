package com.example.book.Model.report;

public class  ModelReport {
    private String bookId;
    private String categoryId;
    private String id;
    private String reason;
    private long timestamp;
    private String uid;
    private String bookTitle;
    private String bookAuthor;
    private String bookYear;
    private String bookMajor;
    private String nameUser;
    private String emailUser;

    public ModelReport() {
    }

    public ModelReport(String bookId, String categoryId, String id, String reason, long timestamp, String uid, String bookTitle, String bookAuthor, String bookYear, String bookMajor, String nameUser, String emailUser) {
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.id = id;
        this.reason = reason;
        this.timestamp = timestamp;
        this.uid = uid;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookYear = bookYear;
        this.bookMajor = bookMajor;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
    }

    //get and set


    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookYear() {
        return bookYear;
    }

    public void setBookYear(String bookYear) {
        this.bookYear = bookYear;
    }

    public String getBookMajor() {
        return bookMajor;
    }

    public void setBookMajor(String bookMajor) {
        this.bookMajor = bookMajor;
    }
}
