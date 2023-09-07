package com.example.shop.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*add category classifaction*/
public class AddCategoryRequest {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AddCategoryRequest{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /*unsafe to reuse the pojo class*/
    @Size(min = 2, max = 5)
    @NotNull(message = "name ids not awalled null")
    private String name;
    @NotNull(message = "type ids not awalled null")
    @Max(3)
    private Integer type;
    @NotNull(message = "parentId ids not awalled null")
    private Integer parentId;
    @NotNull(message = "orderNum ids not awalled null")
    private Integer orderNum;

}
