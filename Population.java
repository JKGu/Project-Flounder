import java.util.*;
import java.util.Map.Entry;


public class Population {
    protected HashSet<Individual> population;
    protected LinkedHashMap<Individual, Integer> fitnessMap;
    protected LinkedHashMap<Individual, Integer> rouletteWheelMap;
    protected int size;
    private int mainColorNum;
    protected int i;
    protected int j;
    protected int numOfDots;
    protected int generationNumTag;
    protected int dotSizeInPixels;

    public Population(int populationSize, int mainColorNum, int i, int j, int numOfDots) {
        this(populationSize, mainColorNum, i, j, numOfDots, false);
        for (int k = 0; k < populationSize; k++) {
            this.population.add(new Individual(mainColorNum, i, j, numOfDots));
        }
    }

    //do no fill with individuals
    public Population(int populationSize, int mainColorNum, int i, int j, int numOfDots, boolean no) {
        this.population = new HashSet<Individual>();
        this.size = populationSize;
        this.mainColorNum = mainColorNum;
        this.i = i;
        this.j = j;
        this.numOfDots = numOfDots;
        this.fitnessMap = new LinkedHashMap<Individual, Integer>();
        this.rouletteWheelMap = new LinkedHashMap<Individual, Integer>();
        this.generationNumTag = -1;
    }

    public Population newPopulation() {
        return new Population(size, mainColorNum, i, j, numOfDots, false);
    }

    public void putIndividual(Individual i){
        population.add(i);
    }
    public void putIndividual(ArrayList<Individual> al){
        for(Individual i: al){
            population.add(i);
        }
    }
    public void hashAndSortFitness(Fitness fMethod, FileManager fm) {
        int totalFitness = 0;
        for (Individual i : this.population) {
            int value = fMethod.getFitness(i, fm); 
            fitnessMap.put(i, value);
            totalFitness += value;
        }
        List<Map.Entry<Individual, Integer>> list = new LinkedList<Map.Entry<Individual, Integer>>(
                fitnessMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Individual, Integer>>() {
            @Override
            public int compare(Entry<Individual, Integer> o1, Entry<Individual, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        LinkedHashMap<Individual, Integer> tmp = new LinkedHashMap<Individual, Integer>();
        for(Map.Entry<Individual, Integer> x: list){
            tmp.put(x.getKey(), x.getValue());
            rouletteWheelMap.put(x.getKey(), x.getValue()*10000/totalFitness);
        }
        this.fitnessMap=tmp;
    }

    public Individual getBestIndividual(){
        Individual output = fitnessMap.entrySet().iterator().next().getKey();
        return output;
    }
    public ArrayList<Individual> getBestIndividual(int n){
        ArrayList<Individual> al = new ArrayList<Individual>();
        Iterator<Entry<Individual, Integer>> x= fitnessMap.entrySet().iterator();
        while(x.hasNext()&&al.size()<n){
            al.add(fitnessMap.entrySet().iterator().next().getKey());
        }
        return al;
    }



}