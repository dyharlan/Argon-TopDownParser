package org.example;

public class NumericalVariable<V> implements Comparable<NumericalVariable<V>>{
    private String name;
    private V value;
    private boolean isMutable;
    private boolean hasBeenAssigned;
    public NumericalVariable(String name, V value, Boolean isMutable) {
        this.name = name;
        this.value = value;
        this.isMutable = isMutable;
    }

    public String getName() {
        return name;
    }
    public V getValue() {
        if(!hasBeenAssigned){
            System.out.println(name + " has not been initialized yet.");
            System.exit(0);
        }
        return value;
    }
    public void setValue(V value) {
        if(isMutable || !hasBeenAssigned){
            this.value = value;
            hasBeenAssigned = true;
        }else {
            System.out.println(name + " is not a mutable variable.");
            System.exit(1);
        }
    }
    public boolean isMutable() {
        return isMutable;
    }
    @Override
    public int compareTo(NumericalVariable<V> o) {
        return o.getName().compareTo(this.name);
    }
}
