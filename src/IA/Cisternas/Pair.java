package IA.Cisternas;

public class Pair implements Comparable<Pair> {
    private int key;
    private int value;

    public Pair(){
    	this.key = -1;
    	this.value = -1;
    }
    
    public Pair(int key, int value){
        this.key = key;
        this.value = value;
    }
    
    public int getKey() {
        return key;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setKey(int key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(Pair p) {
        return(p.value - value); 
    }
    
    public String toString() {
        return "("+key+","+value+")";

    }

}