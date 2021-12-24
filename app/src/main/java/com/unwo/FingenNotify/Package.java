package com.unwo.FingenNotify;

public class Package {
    private long id;
    private String name;
    private String sender;

    public void setName(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return name;
    }

    public void setId(long id)
    {
        this.id=id;
    }

    public long getId()
    {
        return id;
    }

    public void setSender(String sender)
    {
        this.sender=sender;
    }

    public String getSender()
    {
        return sender;
    }
}
