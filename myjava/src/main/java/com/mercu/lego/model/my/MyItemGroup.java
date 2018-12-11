package com.mercu.lego.model.my;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MyItemGroup extends MyItem {
    private List<MyItem> myItems;
    private String repImg;
    private String repImgOriginal;
    private String colorCode;
}
