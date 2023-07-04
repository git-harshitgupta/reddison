package org.harshit.test.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Resturant {

    private String id;
    private String city;
    private double latitude;
    private double longitude;
    private String name;
    private String zip;

}
