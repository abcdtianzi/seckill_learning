package org.seckill.entity;

import java.sql.Date;


/**
 * Created by ting on 2017/8/6.
 */
public class  SuccessKilled {

    private long id;
    private long userPhone;
    private short state;

    private Date createTime;

    //    多对一
    private Seckill seckill;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }


    @Override
    public String toString() {
        return "SuccessKilled{" +
                "id=" + id +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + seckill +
                '}';
    }
}
