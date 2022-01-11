package com.yang.pojo.BO;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class UserTripBO {

    private String username;
    private Date starttime;
    private Date stoptime;
    private String place;

}
