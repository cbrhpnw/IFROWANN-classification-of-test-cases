package keel.Algorithms.ImbalancedClassification.FuzzyImb;

public class ToSort  implements Comparable<ToSort>{
    private boolean isPositive ;
    private double sim;
    
    public ToSort (boolean isPositive , double simil){
        this.isPositive = isPositive;
        this.sim = simil;
    }
    
    public boolean isPositiveInstance(){
        return isPositive;
    }
    
    public double getsim (){
        return sim;
    }

    public int compareTo(ToSort o) {
        if (sim < o.getsim()){  // our element should be considered later
            return 1;
        } else if (sim > o.getsim()){
            return -1;
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString(){
        String text = "( ";
        if(isPositive){
            text += "positive";
        } else {
            text += "negative";
        }
        return text + " , " + sim + " )" ;
    }

	
}
