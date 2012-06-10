/**
 * 
 *     This file is part of ag41-print12-challenge.
 *     
 *     ag41-print12-challenge is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     
 *     ag41-print12-challenge is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     
 *     You should have received a copy of the GNU General Public License
 *     along with ag41-print12-challenge.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

/**
 * 
 * @author Olivier Grunder
 * @version 0.02
 * @date 23 mai 2012
 *
 */

import java.util.* ;

public class Solution {

	protected Problem slpb ;
	protected double evaluation ;

	/*
	 * Array of the production dates, indice is job
	 */
	protected double productionStartingDates[] ;
	public double productionCompletionTimes[] ;

	public double transportationCompletionTimes[] ;

	protected double evaluationManufacturer;
	protected double evaluationTransporter;
	protected double evaluationCustomer;

	/*
	 * Array of the first transporter departure, indice is a transporter
	 */
	protected double dateTransporterDeparture ;
	protected double dateProductionDeparture ;

	public static final double MOINS_INFINI = -1000000.0;

	/**
	 * multi transporter loading sequence :
	 * a solution is composed of different batches, each batch is defined
	 * by the identifier of the transporter and the number of transported parts.
	 */
	protected Vector deliverySequenceMT ;

	/**
	 * production sequence :
	 */
	protected Vector productionSequenceMT ;

	
	
	

	/*
	 * ---------------------------------------- Constructors ----------------------------------------
	 * */
	public Solution(Problem pb_) {
		slpb = pb_ ;
		productionCompletionTimes = new double[slpb.getNp()] ;
		transportationCompletionTimes = new double[slpb.getNp()] ;
		productionStartingDates = new double[slpb.getNp()] ;

		deliverySequenceMT = new Vector() ;
		productionSequenceMT = new Vector() ;
	}

	/** create a copy of a solution.
	 * Note: you should evaluate the solution after. It is not do.
	 * @param sol la solution a recopier **/
	public Solution(Solution sol) {
		slpb=sol.slpb;
		productionCompletionTimes = new double[sol.slpb.getNp()] ;
		transportationCompletionTimes = new double[sol.slpb.getNp()] ;
		productionStartingDates = new double[sol.slpb.getNp()] ;

		deliverySequenceMT = (Vector) sol.deliverySequenceMT.clone();
		productionSequenceMT = (Vector) sol.productionSequenceMT.clone();
	}
	
	
	/** create and return a random solution
	 * 
	 * @param pb 
	 * @return : the random solution
	 */
	public Solution randomSolution(Problem pb) {
		Solution s = new Solution(pb);
		s.setAsRandom();
		return s;
	}
	

	
	/*
	 * ---------------------------------------- Operators to vectors Delivery & Production ----------------------------------------
	 * */
	public void reset() {
		deliverySequenceMT = new Vector() ;
		productionSequenceMT = new Vector() ;
		productionCompletionTimes = new double[slpb.getNp()] ;
		productionStartingDates = new double[slpb.getNp()] ;
		System.gc();
	}


	public void setDeliverySequenceMT(Vector newListChargt1) { deliverySequenceMT = newListChargt1 ; }

	//----------------------------------------
	public int getNumberOfDeliveredBatches(){ return deliverySequenceMT.size(); }
	public int getNumberOfProducedBatches(){ return productionSequenceMT.size(); }

	//----------------------------------------
	public int getNumberOfDeliveredParts() {
		int nc = getNumberOfDeliveredBatches() ;
		int nbrParts = 0 ;
		for (int i=0;i<nc;i++) {
			nbrParts += getDeliveryBatchSize(i) ;
		}
		return nbrParts ;
	}

	public int getNumberOfProducedParts() {
		int nc = getNumberOfProducedBatches() ;
		int nbrParts = 0 ;
		for (int i=0;i<nc;i++) {
			nbrParts += getProductionBatch(i).getQuantity() ;
		}
		return nbrParts ;
	}

	//----------------------------------------
	public Batch getDeliveryBatch(int i){
		return (Batch)deliverySequenceMT.get(i) ;
	}

	//----------------------------------------
	public Batch getProductionBatch(int i){
		return (Batch)productionSequenceMT.get(i) ;
	}

	//----------------------------------------
	private void setDeliveryBatch(int i, Batch b) {
		deliverySequenceMT.set(i,b) ;
	}

	private void insertDeliveryBatch(int i5, Batch batch) {
		deliverySequenceMT.insertElementAt(batch, i5) ;
	}

	//----------------------------------------
	public int getDeliveryBatchSize(int i){
		Batch chargt = getDeliveryBatch(i) ;
		if (chargt!= null) return chargt.getQuantity() ;
		return -1 ;
	}

	//----------------------------------------
	public int getProductionBatchSize(int i){
		Batch chargt = getProductionBatch(i) ;
		if (chargt!= null) return chargt.getQuantity() ;
		return -1 ;
	}

	public void setProductionBatchSize(int indice, int batchsize) {
		productionSequenceMT.set(indice, new Batch(batchsize)) ;		
	}	

	/**
	 * @param lot1
	 * @param Delivery
	 */
	public void setDelivery(int lot1, Batch Delivery) {
		deliverySequenceMT.set (lot1, Delivery) ;		
	}

	/**
	 * @param i
	 * @param qteAEclater
	 * @param newtr
	 */
	public void addDelivery(int i, int qte) {
		deliverySequenceMT.insertElementAt(new Batch(qte), i) ;

	}


	//----------------------------------------
	/**
	 * @param j = quantity of delivered parts
	 * @param tr = transporteur
	 */
	public void addDeliveryFirst(int j) { deliverySequenceMT.add(0, new Batch(j)) ; }
	public void addProductionFirst(int j) { productionSequenceMT.add(0, new Batch(j)) ; }
	public void addProductionLast(int j) { productionSequenceMT.add(new Batch(j));}
	public void addDeliveryLast(int i) { deliverySequenceMT.add(new Batch(i)) ; }

	public void setDelivery(int i, int j) { deliverySequenceMT.set(i, new Batch(j)) ; }

	public void delDelivery(int lot1) { deliverySequenceMT.remove(lot1) ;	}

	//----------------------------------------
	public void delFirstDelivery() { deliverySequenceMT.remove(0) ;}
	public void delFirstProduction() { productionSequenceMT.remove(0) ;}
	public void delAllProduction() { productionSequenceMT.removeAllElements() ;}

	/**
	 * check the sequence
	 */
	protected boolean check() {
		int i=0 ;
		while (i<getNumberOfDeliveredBatches()) {
			if (getDeliveryBatchSize(i)<=0) {
				System.out.println ("WARNING GSupplyLinkSolution.check : getDelivery("+i+")="+getDeliveryBatch(i)+" <= 0 => removing it !" );
				deliverySequenceMT.remove(i) ;
			}
			else
				i++ ;
		}

		if (getNumberOfDeliveredParts()>slpb.getNp()) {
			System.out.println ("ERROR GSupplyLinkSolution.check : getNbrProduit()="+getNumberOfDeliveredParts()+" > slpb.getNp()="+slpb.getNp()+" => don't know what to do !" );
			return false ;
		}
		return true;
	}

	
	
	


	/** affect to a solution random CORRECTS sequences
	 * 
	 * @return the random solution
	 */
	public void setAsRandom() {
		Random generator = new Random();		
		/* Nombre d'articles à placer */
		int restOfProducts = slpb.getNp();
		
		/* Nombre d'article placer en un lot */
		int newBatch;
		
		// Tant que l'on a des lots à placer, on tire au hasard le nombre de paquets de lots que l'on a dans la prochaine étape
		while(restOfProducts>0) {
			newBatch = generator.nextInt(restOfProducts)+1;
			restOfProducts-=newBatch;
			this.addProductionLast(newBatch);
		}

		/* et on recommence la tirage avec la livraison:
		 On reinitialise le reste de lots à caser */
		restOfProducts = slpb.getNp();

		/* Tant que l'on a des lots à placer, on tire au hasard le nombre de paquets de lots que l'on a dans la prochaine étape */
		while(restOfProducts>0) {
			/* test used to generate corrects batchs for transporter, not more than the capacity and not more than number of articles to place */
			newBatch = generator.nextInt( (slpb.getTransporter().getCapacity()<restOfProducts) ? slpb.getTransporter().getCapacity() : restOfProducts ) +1;
			restOfProducts-=newBatch;
			this.addDeliveryLast(newBatch);
		}
		this.evaluate();
	}

	
	
		
	
	
	
	/*
	 * ---------------------------------------- Mutation operators ----------------------------------------
	 * */	
	/** Retourne une solution mutée à partir de parent. Elle sépare un batch en deux parties
	 *	@return solMutee :  la solution mutée 
	 **/
	public Solution mutationProductionFission() {
		Solution solMutee = new Solution(slpb); // La solution que l'on va retourner
		/* Si le nombre de batchs est égal au nombre d'articles, pas besoin de
		 * chercher à faire une fission, chaque batchs contient 1 article uniquement. */
		if(slpb.getNp()!=productionSequenceMT.size()) {
			/* Les deux indices dont on va échanger les valeurs*/
			int indice1;
			/* sizeOfBatch : la taille du batch à séparer. separation:  la taille de la première partie séparée */
			int sizeOfBatch,separation; 
			
			/* On tire au hasard l'indice du batch qu'on va séparer en deux
			 * Il faut qu'il ait au moins 2 articles */
			Random generator = new Random();	
			do {
				indice1 = generator.nextInt(productionSequenceMT.size());
			} while(getProductionBatchSize(indice1)<2);
			
			sizeOfBatch = getProductionBatchSize(indice1);
			separation = generator.nextInt(sizeOfBatch-1) + 1;
			
			/* Ici, on recopie le tableau de production de la Solution parente,
			 * mais en séparant le batch en question */
			for(int indice=0;indice<productionSequenceMT.size();indice++) {
				if(indice==indice1) {
					solMutee.addProductionLast(sizeOfBatch - separation);
					solMutee.addProductionLast(separation);
				}
				else
					solMutee.addProductionLast(getProductionBatchSize(indice));			
			}
			
			/* Par contre, le tableau de livraison de change pas, on le recopie simplement.*/
			for(int i=0;i<this.deliverySequenceMT.size();i++) {
				solMutee.addDeliveryLast(getDeliveryBatchSize(i));
			}			
		}
		/* Si on a que des batchs n'ayant pas plus de 1 articles, on retourne une copie de la solution courante. */
		else {
			solMutee = this.copySeq();
		}
		solMutee.evaluate();
		return solMutee;
	}
	
	
	/** Retourne une solution mutée à partir de parent. Elle sépare un batch en deux parties
	 *	@return solMutee :  la solution mutée 
	 **/
	public Solution mutationProductionFusion() {
		Solution solMutee = new Solution(slpb); // La solution que l'on va retourner
		
		/*Il faut tester si il n'y a qu'un élément dans la sequence de production 
		 * car il faut dans ce cas ne rien faire (boucle random sur indice2 tournera à l'infinie.*/	
		if(productionSequenceMT.size()!=1) {
			/* Les deux indices dont on va fusionner les valeurs*/
			int indice1,indice2;
			/* sizeOfBatch : la taille des deux batchs fusionnés. */
			int sizeOfBatch; 
			boolean dejaTraite = false;
			
			/* On tire au hasard l'indice du batch qu'on va séparer en deux
			 * Il faut qu'il ait au moins 2 articles */
			Random generator = new Random();	
	
			indice1 = generator.nextInt(productionSequenceMT.size());
	
			do {
				indice2 = generator.nextInt(productionSequenceMT.size());
			} while(indice2==indice1);
				
			sizeOfBatch = getProductionBatchSize(indice1)+ getProductionBatchSize(indice2);
			
			/* Ici, on recopie le tableau de production de la Solution parente,
			 * mais en fusionnant les batchs en question */
			for(int indice=0;indice<productionSequenceMT.size();indice++) {
				if( (indice==indice1 || indice==indice2) && !dejaTraite) {
					solMutee.addProductionLast(sizeOfBatch);
					dejaTraite=true;
				}
				else if(indice!=indice1 && indice!=indice2)
					solMutee.addProductionLast(getProductionBatchSize(indice));			
			}
			
			/* Par contre, le tableau de livraison de change pas, on le recopie simplement.*/
			for(int i=0;i<this.deliverySequenceMT.size();i++) {
				solMutee.addDeliveryLast(getDeliveryBatchSize(i));
			}

		
		}
		else {
			/* Si il n'y a qu'un élément, on recopie les tableaux de la solution mere dans la nouvelle solution */
			solMutee = this.copySeq();
		}
		
		solMutee.evaluate();
		return solMutee;
	}
	
	
	/**
	 * Opérateur de croisement : L'opérateur prend au hasard un lot de la première productionSequenceMT et l'ajoute à la nouvelle productionSequenceMT, elle fait de même sur la deuxième et ainsi de suite.
	 * @param : s solution
	 * @return : newS solution 
	 */
	
	public Solution crossover(Solution sol2) {
		int restOfproduct= slpb.getNp(), indice1, indice2;
		Solution newSol= new Solution();
		Random generator = new Random();
		Vector prod1= new Vector(this.productionSequenceMT);
		Vector prod2= new Vector(sol2.productionSequenceMT);
		Vector newProd= newSol.productionSequenceMT;
		while(!prod1.isEmpty()||!prod2.isEmpty()) {
			if(!prod1.isEmpty()) {
				indice1 = generator.nextInt(prod1.size());
				if(prod1(indice1)<=restOfProduct) {
					newProd.add(prod1[indice1]);
					restOfproduct-=prod1[indice1];
				}
				prod1.remove(indice1);
			}
			
			if(!prod2.isEmpty()) {
				indice2 = generator.nextInt(prod2.size());
				if(prod2(indice2)<=restOfProduct) {
					newProd.add(prod2[indice2]);
					restOfproduct-=prod2[indice2];
				}
				prod2.remove(indice2);
			}
		}
		if(restOfproduct!=0)
			newProd.add(restOfProduct);
		}
	
	
	/**	Copy DeliverySequence and ProductionSequence to a new Solution.
	 * 	Note: no call to evaluate. You should call it to evaluate the solution
	 * @return : a copy of the input solution
	 */
	public Solution copySeq() {
		Solution copie = new Solution(this);	
		return copie;
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * @param solstring : "production sequence / delivery sequence", e.g. "24 26/5 3 6 5 5 5 4 4 6 7"
	 */
	public void setFromString(String solstring) {
		StringTokenizer st = new StringTokenizer(solstring, new String("/")) ;
		
		//Ici on sépare la chaîne en deux (au niveau du '/'
		String prodst = st.nextToken().trim() ;
		String delivst = st.nextToken().trim() ;
		
		//Ici on parse la première partie en cherchant des entiers que l'on ajoute à notre liste de Production
		st = new StringTokenizer(prodst, new String(" ")) ;
		while (st.hasMoreTokens()) {
			String nxtst = st.nextToken().trim();
			addProductionLast(Integer.parseInt(nxtst)) ;
		}
		
		// De même avec avec la liste de livraison
		st = new StringTokenizer(delivst, new String(" ")) ;
		while (st.hasMoreTokens()) {
			String nxtst = st.nextToken().trim();
			addDeliveryLast(Integer.parseInt(nxtst)) ;
		}
	}



	@Override
	public String toString() {
		/*return "Solution [evaluation=" + evaluation
				+ ", productionStartingDates="
				+ Arrays.toString(productionStartingDates)
				+ ", productionCompletionTimes="
				+ Arrays.toString(productionCompletionTimes)
				+ ", \ntransportationCompletionTimes="
				+ Arrays.toString(transportationCompletionTimes)
				+ ", \nevaluationManufacturer=" + evaluationManufacturer
				+ ", evaluationTransporter=" + evaluationTransporter
				+ ", evaluationCustomer=" + evaluationCustomer
				+ ", dateTransporterDeparture=" + dateTransporterDeparture
				+ ", dateProductionDeparture=" + dateProductionDeparture
				+ ", \ndeliverySequenceMT=" + deliverySequenceMT
				+ ", productionSequenceMT=" + productionSequenceMT + "]\n\n";*/
		
		return "Solution [evaluation=" + evaluation
				+ ", \ndeliverySequenceMT=" + deliverySequenceMT
				+ ", productionSequenceMT=" + productionSequenceMT + "]\n";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ---------------------------------------- class Evaluator ----------------------------------------
	 * */
	private class Evaluator {
		// Supplier/Transporter/Customer cost
		int job ;
		double completiontime ;
		double[] transportationStartingTimes ;

		public Evaluator() {
			transportationStartingTimes = new double[slpb.getNp()] ;
		}

		public double evaluate() {
			evaluation = 0 ;
			evaluationManufacturer = 0 ;
			evaluationTransporter = 0 ;
			evaluationCustomer = 0 ;

			job=slpb.getNp()-1 ;
			completiontime = slpb.getDueDate(job) ;

			evaluateTransporterCustomerCosts() ;
			evaluateSupplierCosts() ;

			return evaluation ;
		}

		/**
		 *  ================================
		 *  Customer & Transporter cost
		 *  ================================ 
		 */
		private void evaluateTransporterCustomerCosts() {
			int indiceDelivBatch = getNumberOfDeliveredBatches()-1 ;
			while (job>=0 && indiceDelivBatch>=0) {
				int delivbatch = getDeliveryBatchSize(indiceDelivBatch) ;
				Transporter transp = slpb.getTransporter() ;

				// Transporter cost
				evaluationTransporter += transp.getBatchDeliveryCost() ;

				// Computation of the arrival date of the transporter
				for (int i=job;i>=job-delivbatch+1;i--) {
					try  {
						if (slpb.getDueDate(i)<completiontime)
							completiontime = slpb.getDueDate(i) ;
					}
					catch (Exception e) {
						System.out.println ("Exception GSupplyLinkSolution.evaluate:"+e.getMessage()) ;
						System.out.println ("i="+i) ;
						System.out.println ("slpb.getNp()="+slpb.getNp()) ;
						System.out.println ("job="+job) ;
						System.out.println ("np_chargt="+(delivbatch)) ;
						System.out.println ("job-np_chargt+1="+(job-delivbatch+1)) ;
						System.out.println ("solution="+toString());
						System.out.println ("numberOfDeliveredParts="+getNumberOfDeliveredParts()) ;
						check() ;

						System.exit(0) ;
					}
				}

				for (int i=job;i>=job-delivbatch+1;i--) {
					// Customer cost
					transportationCompletionTimes[i] = completiontime ;
					transportationStartingTimes[i] = transportationCompletionTimes[i] - transp.getBatchDeliveryTimes() ;
					evaluationCustomer += slpb.getCustomerHoldingCost(i, completiontime) ;
				}
				job -= delivbatch ;

				// Transportation of the parts
				completiontime -= transp.getBatchDeliveryTimes() ;

				//		    	dateTransportationStart[indiceDelivBatch] = completiontime ;
				dateTransporterDeparture = completiontime ;

				// Empty transporter which come back from the customer to the supplier
				completiontime -= transp.getBatchDeliveryTimes() ;

				indiceDelivBatch-- ;
			}

			evaluation += evaluationTransporter + evaluationCustomer ;
		}

		/**
		 *  ================================
		 *  Supplier Cost
		 *  ================================ 
		 */
		private void evaluateSupplierCosts() {
			int indiceProdBatch = getNumberOfProducedBatches()-1 ;
			int sumProd = 0 ; 

			job = slpb.getNp()-1 ;
			double completionTimeBatch = transportationStartingTimes[job] ;
			dateProductionDeparture = completionTimeBatch ;

			while (job>=0 && indiceProdBatch>=0) {
				int prodbatch = getProductionBatchSize(indiceProdBatch) ;

				// Production cost and WIP holding cost (1st holding cost) for supplier	    	
				evaluationManufacturer += slpb.getBatchProductionHoldingCost(prodbatch);

				double prodCompletionTime = dateProductionDeparture ;
				int jobstart = job ;
				for (int job2=0;job2<prodbatch;job2++) {
					if (transportationStartingTimes[job]<prodCompletionTime)
						prodCompletionTime = transportationStartingTimes[job] ;
					job -- ;
				}

				job = jobstart ;
				for (int job2=0;job2<prodbatch;job2++) {
					productionCompletionTimes[job] = prodCompletionTime ;
					dateProductionDeparture = productionCompletionTimes[job]- slpb.getSetupTime() - slpb.getProductionTime()*prodbatch ;

					// Second holding cost for supplier
					evaluationManufacturer += (transportationStartingTimes[job]-productionCompletionTimes[job])*slpb.getSupplierUnitHoldingCost()  ;

					job -- ;
				} // end for
				indiceProdBatch-- ;
			} // end while


			evaluation += evaluationManufacturer ;

		}

	}

	
	
	/**
	 * Evaluation of the diff batch solution
	 */

	public double evaluate()  {
		if (slpb==null) return -1 ;

		// The number of produced parts has to be greater or equal to the 
		// number of delivered parts
		if (getNumberOfDeliveredBatches()<=0) {
			return -1 ;
		}

		Evaluator ev = new Evaluator() ;
		return ev.evaluate() ;
	}


} 
