import java.util.Iterator;
import java.util.Map.Entry;

public class Evolution {
    public static Population evolve(Population p, int numOfElites, int generationNum, FileManager fm){
        Population output = p.newPopulation();

        //----------cacl fitness
        Fitness fn = null;
        if(generationNum%10000==0) fn= new FitnessAuto(); else fn = new FitnessAuto();
        p.hashAndSortFitness(fn, fm);


        //----------PUT NEW WITH CROSSOVER
        for(int i = 0; i<p.size-numOfElites; i++){
            Individual i1 = select(p);
            Individual i2 = select(p);
            Individual iNew = crossover(i1, i2);
            output.putIndividual(iNew);
        }
        //-----------MUTATE
        for(Individual i: output.population){
            mutate(i, 1/(1+((double)generationNum)));
        }
        //-----------KEEP BEST ONES
        output.putIndividual(p.getBestIndividual(numOfElites));
        return output;
    }

    private static Individual select(Population p){
        int random = (int)(Math.random()*10000);
        Iterator<Entry<Individual, Integer>> x= p.rouletteWheelMap.entrySet().iterator();
        Entry<Individual, Integer> entry;
        do {
            entry = x.next();
        }while(x.hasNext()&&random>entry.getValue());
        return entry.getKey();
    }

    private static Individual crossover(Individual i1, Individual i2){
        Individual newI = i1.individualWithSameSettings();
        int colorOrPattern = (int)(Math.random()*2);
        int genesLength, mutateEndPoint, mutateStartPoint;
        int colorEncodingLength = i1.mainColorNum*24;
        if(colorOrPattern==0){
            genesLength=colorEncodingLength;
            mutateStartPoint = (int)(Math.random()*genesLength);
            mutateEndPoint = (int)(Math.random()*genesLength);
        }else{
            genesLength=i1.genes.length()-colorEncodingLength;
            mutateStartPoint = colorEncodingLength+(int)(Math.random()*genesLength);
            mutateEndPoint = colorEncodingLength+(int)(Math.random()*genesLength);
        }

        String newGenes = "";
        if(mutateStartPoint<=mutateEndPoint){
            newGenes=i1.genes.substring(0, mutateStartPoint)+i2.genes.substring(mutateStartPoint, mutateEndPoint)+i1.genes.substring(mutateEndPoint);
        }else{
            newGenes=i1.genes.substring(0, mutateEndPoint)+i2.genes.substring(mutateEndPoint, mutateStartPoint)+i1.genes.substring(mutateStartPoint);
        }
        newI.genes=newGenes;
        return newI;
    }

    private static void mutate(Individual i, double mutationRate){
        int length = i.genes.length();
        for(int k=0; k<length; k++){
            if(Math.random()<mutationRate){
                if(i.genes.substring(k,k+1).equals("0")) i.genes=i.genes.substring(0,k)+"1"+i.genes.substring(k+1);
                else i.genes=i.genes.substring(0,k)+"0"+i.genes.substring(k+1);
            }
        }
    }
}