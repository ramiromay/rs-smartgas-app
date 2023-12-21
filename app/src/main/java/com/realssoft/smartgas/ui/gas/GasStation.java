package com.realssoft.smartgas.ui.gas;

public class GasStation {

    private String name ;
    private String direction;
    private String cost;
    private int img = 0;
    private int serviceOxxo = 0;
    private int service7Eleven = 0;
    private int serviceGoMart = 0;

    public GasStation (){ }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getServiceOxxo() {
        return serviceOxxo;
    }

    public void setServiceOxxo(int serviceOxxo) {
        this.serviceOxxo = serviceOxxo;
    }

    public int getService7Eleven() {
        return service7Eleven;
    }

    public void setService7Eleven(int service7Eleven) {
        this.service7Eleven = service7Eleven;
    }

    public int getServiceGoMart() {
        return serviceGoMart;
    }

    public void setServiceGoMart(int serviceGoMart) {
        this.serviceGoMart = serviceGoMart;
    }
}
