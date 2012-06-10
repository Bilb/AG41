import java.util.Collections;
import java.util.Random;

	

//TODO
	//Croisement: faudrait que ça change de nombre de lot aussi: sinon on chge pas assez les solutions...
	//Selection: rang + elitisme! => on garde meilleure reponse dans population

	//Attention operateur de mutation de Delivery : on doit faire gaffe a tiré des batchs < capacity du transporter

	//L’épuisement de la diversité peut constituer un critère d’arrêt de l’algorithme.
	
	







/** A population is a population of solutions */
public class Population {
	protected Solution[] pop;
	protected int nbSolution;
	protected Problem pb;
	
	/** Initialize a population with random solutions.
	  */
	public Population(int nPopulation,Problem problem) {
		nbSolution = nPopulation;
		pop = new Solution[nbSolution];
		pb = problem;
		/* Fill the population with solutions */
		fillFromRandom();
	}
	

	
	
	/** Fill a population with random solutions**/
	public void fillFromRandom() {
		for (int i=0; i<nbSolution;i++) {	
			/* get a random solution and affect it to pop[i] */
			Solution s = new Solution(pb);
			s.setAsRandom();
			pop[i] = s;
		}
	}
	
	
	
	
	/** Fill a population with the same solution : the one passed in s
	 * @param s The string used to generate the solution**/
	public void fillFromString(String s) {
		Solution sol = new Solution(pb);
		sol.setFromString(s);
		sol.evaluate();
		for (int i=0; i<nbSolution;i++)	
			pop[i] = sol;
	}
		
	
	
	
	
	
	
	
	
	
	
	// methode pour reordonner notre population après un petit chgt ?!
	/** Put in order our Population.
	 */
	public void ordonner()
	{
		ordonner(0,nbSolution-1);
	}

	private int partition(int deb,int fin)
	{
		int compt=deb;
		Solution pivot=pop[deb];

		for(int i=deb+1;i<=fin;i++)
		{
			if (pop[i].evaluation<pivot.evaluation)
			{
				compt++;
				swap(pop,compt,i);
			}
		}
		swap(pop,deb,compt);
		return(compt);
	}

	private void ordonner(int deb,int fin)
	{
		if(deb<fin)
		{
			int positionPivot=partition(deb,fin);
			ordonner(deb,positionPivot-1);
			ordonner(positionPivot+1,fin);
		}
	}
	
	/** Swap two solutions in a tab of solution
	 * 
	 * @param S solution[] in which we will swap elements
	 * @param indice1 : first indice 
	 * @param indice2 : second indice
	 */
	public void swap (Solution S[], int indice1, int indice2) {
		Solution temp = S[indice1];
		S[indice1] = S[indice2];
		S[indice2] = temp;
	}
	
	
	
	
	

	public Solution selection_ranking() {
		//reordonner plutot que ordonner?
		ordonner();
		Random generator = new Random();
		double variable_alea = generator.nextDouble();

		int i=0;
		/* somme des rang 1+2+3+4+...+n = n*(n+1)/2 */
		double somme_rang = (double) nbSolution*(nbSolution+1)/2.0;
		double portion = ( ((double)(nbSolution-i))/somme_rang);



		while(variable_alea>portion && i<=nbSolution)
		{
			//System.out.println("portion = "+portion+" i = "+i+" alea = "+variable_alea);
			i++;
			portion +=  ( ((double)(nbSolution-i))/somme_rang)/(nbSolution);
		}

		if(i>=nbSolution) {
			i = generator.nextInt(nbSolution);
		}
			
		return pop[i];
	}

	
	
	
	
	
	
	
	
	
	
	@Override
	public String toString() {
		String ret = "\n\n\n[Population] de : " + nbSolution + " solutions";
		for(int i=0;i<nbSolution;i++) {
			ret += "Solution n°" +i+ " = " + pop[i] + "\n";
		}
		
		return ret;
		
	}
	
	
}
