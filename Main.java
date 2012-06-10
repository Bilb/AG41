import org.w3c.dom.events.MutationEvent;
//TESZT
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
public class Main {

	// ----------------------------------------
	public static void main(String[] args) {
		Problem pb = new Problem("C:/Users/Bilb/Documents/UTBM/GI2/AG41/Projet/ag41-2012P-challenge/data/problem003-050.txt") ;
	//	System.out.println("problem="+pb.toString()+"\n") ;


		Population p = new Population(5,pb);
		System.out.println("Population :" +p);
		System.out.println("\n\nSolution tirée : "+p.selection_ranking() + "capacity transporter = "+pb.getTransporter().getCapacity());


   }
}
