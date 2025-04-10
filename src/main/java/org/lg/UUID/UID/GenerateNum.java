package org.lg.UUID.UID;



public class GenerateNum {

    public String getNum(int num){
        if(num==9999) return "0000";
        String res=String.valueOf(++num);
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<(4-res.length());i++){
            sb.append('0');
        }
        sb.append(res);
        return sb.toString();
    }
}
