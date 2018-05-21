import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;
import org.jgap.util.SystemKit;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by danielwalker on 27/04/18.
 */

public class Main {

    //constants
    public static final int INPUT_SIZE = 20;
    public static final int MAX_DEPTH = 4;
    public static final int POP_SIZE = 1000;
    public static final Object[] NEW_OB = new Object[0];


    //fields
    private double x[];
    private double y[];

    private GPConfiguration config;
    private Variable vx;
    private MathsProblem mp;



    /**
     * On runtime, we pass in the regression.txt file through program arguments
     * @param file
     */
    private Main(String file){
        load(file);
    }

    public static void main(String[] args) throws Exception {
        // Check valid usage
        if (args.length != 1) { // we only need to take in one text file
            System.out.println("Incorrect number of arguments. \nArguments: Filename");
            return;
        }

        // Create application and run
        Main main;
        main = new Main(args[0]);
        main.initialConfig();
        main.run();
    }


    private void load(String f){
        //initialise variables
        x = new double[INPUT_SIZE];
        y = new double[INPUT_SIZE];

        Scanner scan = new Scanner(new InputStreamReader(ClassLoader.getSystemResourceAsStream(f)));

        //skip first two lines of file
        scan.nextLine(); scan.nextLine();

        //Read in each x and y
        for (int i = 0; scan.hasNextDouble(); i++){
            x[i] = scan.nextDouble();
            y[i] = scan.nextDouble();
        }
    }


    public void run() throws Exception{
        GPGenotype geno = mp.create();
        geno.setGPConfiguration(config);
        geno.setVerboseOutput(true);

        //Evolve the model structure
        evolve(geno);

        geno.outputSolution(geno.getAllTimeBest());
        System.out.println("Fitness Function: " + geno.getAllTimeBest().toStringNorm(0));
        //System.out.println("Breakdown " + "x: " + this.x + "y: " + this.y);

        mp.showTree(geno.getAllTimeBest(), "Best MathProb Solution.png");
    }


    public void evolve(GPGenotype type){
        int offset = type.getGPConfiguration().getGenerationNr();
        int evo;
        double fit;
        for (evo = 0; evo<1000; ++evo){
            type.evolve();
            type.calcFitness();
            fit = type.getAllTimeBest().getFitnessValue();
            String freeMB = SystemKit.niceMemory(SystemKit.getFreeMemoryMB());
            if (fit < 0.001) break;

            //if it has better fitness value than the former best solution.
            if (evo % 25 == 0) {
                System.out.println("\n Evolving program: " + (evo + offset) + " fitness: " + fit
                        + " memory free: " + freeMB + "\n");
            }
        }
        System.out.println("After " + evo + " evolutions the program had a fitness of: " + type.getAllTimeBest().getFitnessValue());
    }


    private void initialConfig() throws Exception{
        // Setup the algorithm's parameters.
        // ---------------------------------
        config = new GPConfiguration();
        // We use a delta fitness evaluator because we compute a defect rate, not
        // a point score!
        // ----------------------------------------------------------------------
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());

        config.setFitnessFunction(new MathsFitnessFunc());

        //Set properties
        config.setMaxInitDepth(MAX_DEPTH);
        config.setPopulationSize(POP_SIZE);
        config.setMaxCrossoverDepth(8);

        config.setStrictProgramCreation(true);
        config.setCrossoverProb(0.9f);
        config.setMutationProb(35.0f);
        config.setReproductionProb(0.2f);

        // Create variable (to be used for fitness checking)
        vx = Variable.create(config, "X", CommandGene.DoubleClass);

        // Create a math problem
        mp = new MathsProblem(config, vx);

    }


    /**
     * Evaluates the fitness of a program
     */
    public class MathsFitnessFunc extends GPFitnessFunction {

        @Override
        protected double evaluate(IGPProgram igpProgram) {
            double totalError = 0;

            //For every intput, evaluate it
            for (int i = 0; i < Main.INPUT_SIZE; i++) {
                vx.set(x[i]); // give variable vx an input number.

                // check performance, then add its error to the total error
                double result = igpProgram.execute_double(0, NEW_OB);
                double differnce = result - y[i];
                totalError += Math.abs(differnce);

                if (Double.isInfinite(totalError)) {
                    return Double.MAX_VALUE;
                }
            }
            //remember to account for our min threshhold
            if (totalError < 0.001) {
                return 0;
            }
            return totalError;
        }
    }
}
