package com.unwo.FingenNotify;

public class Notify {
    private long id;
    private String name;
    private String message;
    private String datetime;
    private String sender;

    public void setName(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return name;
    }

    public void setMessage(String message)
    {
        this.message=message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setId(long id)
    {
        this.id=id;
    }

    public long getId()
    {
        return id;
    }

    public void setDatetime(String datetime)
    {
        this.datetime=datetime;
    }

    public String getDateTime()
    {
        return datetime;
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
