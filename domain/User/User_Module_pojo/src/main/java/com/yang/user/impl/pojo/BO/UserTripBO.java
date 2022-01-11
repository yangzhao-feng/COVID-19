package com.yang.user.impl.pojo.BO;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class UserTripBO {

    private String username;
    private String starttime;
    private String stoptime;
    private String place;


}
