package me.whiteship;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("holoman")
public class HolomanProperties {

    private String name;

    private int howLong;

    public int getHowLong() {
        return howLong;
    }

    public String getName() {
        return name;
    }

    public void setHowLong(int howLong) {
        this.howLong = howLong;
    }

    public void setName(String name) {
        this.name = name;
    }

}

