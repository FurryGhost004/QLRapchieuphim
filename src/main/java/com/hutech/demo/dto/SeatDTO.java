package com.hutech.demo.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private String rowName;
    private int seatNumber;
    private String type;
}