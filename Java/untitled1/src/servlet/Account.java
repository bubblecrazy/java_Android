package servlet;

public class Account {
    String username,pwd;
    public Account(String username,String pwd){
        this.username=username;
        this.pwd=pwd;
    }
    public String getUsername(){
        return this.username;
    }
    public String getPwd(){
        return this.pwd;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public void setPwd(String pwd){
        this.username=pwd;
    }
}
