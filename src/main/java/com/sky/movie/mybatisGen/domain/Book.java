package com.sky.movie.mybatisGen.domain;

import java.util.Date;
import javax.persistence.*;

public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String author;

    private String bookman;

    private Double price;

    @Column(name = "categoryId")
    private Integer categoryid;

    private String introduction;

    @Column(name = "onSaleDate")
    private Date onsaledate;

    @Column(name = "onSaleNum")
    private Integer onsalenum;

    @Column(name = "remainNum")
    private Integer remainnum;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return bookman
     */
    public String getBookman() {
        return bookman;
    }

    /**
     * @param bookman
     */
    public void setBookman(String bookman) {
        this.bookman = bookman;
    }

    /**
     * @return price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return categoryId
     */
    public Integer getCategoryid() {
        return categoryid;
    }

    /**
     * @param categoryid
     */
    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    /**
     * @return introduction
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * @param introduction
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * @return onSaleDate
     */
    public Date getOnsaledate() {
        return onsaledate;
    }

    /**
     * @param onsaledate
     */
    public void setOnsaledate(Date onsaledate) {
        this.onsaledate = onsaledate;
    }

    /**
     * @return onSaleNum
     */
    public Integer getOnsalenum() {
        return onsalenum;
    }

    /**
     * @param onsalenum
     */
    public void setOnsalenum(Integer onsalenum) {
        this.onsalenum = onsalenum;
    }

    /**
     * @return remainNum
     */
    public Integer getRemainnum() {
        return remainnum;
    }

    /**
     * @param remainnum
     */
    public void setRemainnum(Integer remainnum) {
        this.remainnum = remainnum;
    }
}