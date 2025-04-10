package org.lg.UUID.UID;


import java.util.HashMap;

public class GenerateSymbol {
    private char[] arr={'@','#','$','%','^','&','*','_','-','+'};
    private HashMap<Character,Integer> map;

    public GenerateSymbol() {
        map=new HashMap<>();
        for(int i=0;i<arr.length;i++){
            map.put(arr[i],i);
        }
    }
    public String getSymbol(String str){
        if(str.length()==0 || str.equals("++++")) return "@@@@";
        char[] res=str.toCharArray();
        if(res[3]=='+' && res[2]!='+' && res[1]!='+'){
            res[2]=map.get(res[2])==(arr.length-1)?arr[0]:arr[map.get(res[2])+1];
            res[3]=arr[0];
        }
        else if (res[2]=='+' && res[1]!='+') {
            res[1]=map.get(res[1])==(arr.length-1)?arr[0]:arr[map.get(res[1])+1];
            res[3]=arr[0];
            res[2]=arr[0];
        }
        else if (res[1]=='+') {
            res[0]=map.get(res[0])==(arr.length-1)?arr[0]:arr[map.get(res[0])+1];
            res[3]=arr[0];
            res[2]=arr[0];
            res[1]=arr[0];
        }
        else {
            res[3]=arr[map.get(res[3])+1];
        }

        return new String(res);
    }
}
