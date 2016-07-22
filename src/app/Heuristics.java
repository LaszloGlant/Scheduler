package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Heuristics {
	/*
	TO DO
	set up scheduling heuristics
	longest latency-weighted path to root (scheduler -a < test.iloc)
	highest latency instructions (scheduler -b < test.iloc)
	a heuristic of my choice (scheduler -c < test.iloc)
	
	
	array list for ready to be scheduled instructions
	array list for remaining instructions
	
	to ready list: nodes with no "source"
	when these nodes executed delete their dependencies and delete the source from their children too
	so their children become nodes without source.
	
	fired node list: those nodes that have been executed have their cycles counted so their children can be released to ready list
	
	*/
	static ArrayList<Instruction> readyList = new ArrayList<Instruction>();
	static ArrayList<Instruction> firedList = new ArrayList<Instruction>();
	static ArrayList<Instruction> tempList = new ArrayList<Instruction>();
	static int [] highestLatency = new int[2];//index 0 is the highest latency, index 1 the instruction it belongs to
	Instruction current;
	
	//static ArrayList<Instruction> remainingList = new ArrayList<Instruction>();
	/* Search for the instruction with the highest latency that has no parent, if both the same use the first in */
	public static void longestLatencyWeighted(ArrayList<Instruction> parsedCode){
		//Instruction lastLine = parsedCode.get(parsedCode.size()-1);
//		Instruction lastLine = parsedCode.get(0);
//		System.out.println("lastLine "+ lastLine.getremoved());
		
		
		//while(!lastLine.getremoved()){
		while(parsedCode.size() != firedList.size()){
			findLeafs(parsedCode);
//			System.out.println("										In option A in while");
			int j, k, tempLatency, edgeWeight=0;
			//fire instruction and start counter of cycle length
			if(!readyList.isEmpty()){//find the highest latency node
				highestLatency [0]= 0;
				highestLatency [1]= 0;
				for(int i=0; i < readyList.size(); i++){
					tempLatency = readyList.get(i).getlatency();
					if(tempLatency>highestLatency[0]){
						highestLatency [0]= readyList.get(i).getlatency();
						highestLatency [1]= i;
					}
					if(tempLatency==highestLatency[0]){//if latency the same see which one has the higher weight
						
						for(j = 0; j < readyList.get(i).destinationOfEdge.size(); j++){//find highest edge weight for node1
							
							if(readyList.get(i).destinationOfEdge.get(j).getweight()>edgeWeight){
								edgeWeight = readyList.get(i).destinationOfEdge.get(j).getweight();
							}
						}
						tempLatency=tempLatency+edgeWeight;
						for(k = 0; k < readyList.get(highestLatency [1]).destinationOfEdge.size(); k++){//find highest edge weight for node2
							if(readyList.get(i).destinationOfEdge.get(k).getweight()>edgeWeight){
								edgeWeight = readyList.get(i).destinationOfEdge.get(k).getweight();
							}
						}
						highestLatency[0] = highestLatency[0]+edgeWeight; 
						if(tempLatency == highestLatency[0]){
							//send the one with lower INDEX 
							if(readyList.get(i).getindex()>readyList.get(highestLatency[1]).getindex()){
								firedList.add(readyList.get(highestLatency [1]));
								readyList.remove(highestLatency [1]);
							}
							else{
								firedList.add(readyList.get(i));
								readyList.remove(i);
							}
						}
						else if(tempLatency > highestLatency[0]){
							//send temp
							firedList.add(readyList.get(i));
							readyList.remove(i);
						}
						else{
							//send highest
							firedList.add(readyList.get(highestLatency [1]));
							readyList.remove(highestLatency [1]);
						}
						
					}
				}//end of for
				decrementEdgeWeight(parsedCode);
			}//end find highest latency
			//decrementEdgeWeight(parsedCode);
			else{
				if(!scheduler.nop.isEmpty()){
					firedList.add(scheduler.nop.get(0));
					scheduler.nop.remove(0);
				}
				decrementEdgeWeight(parsedCode);
			}
		}//end of while
		writeToFile(firedList);
		printFiredlist(parsedCode);
		
	}
	/* Search for the instruction with highest weight and no parents. If all of these are the same weight then the one with lowest index
	 takes priority. */
	public static void highestInstructionLatency(ArrayList<Instruction> parsedCode){
		while(parsedCode.size()!=firedList.size()){
			findLeafs(parsedCode);
//			System.out.println("										In option B in while");
//			System.out.println("ready list size "+readyList.size());
			int i, edgeWeight=0;
			//fire instruction and start counter of cycle length
			if(!readyList.isEmpty()){//find the highest weight node
//				System.out.println("in IF");
//				highestLatency [0]= 0;
//				highestLatency [1]= 0;
				for(i=0; i < readyList.size(); i++){//get highest cycle in the list
					if(readyList.get(i).getcycle()>edgeWeight){
						edgeWeight = readyList.get(i).getcycle();
//						System.out.println("highest edge weight"+edgeWeight);
					}
				}
				//search for nodes with highest cycle and add them to a temp list
				for(i=0; i < readyList.size(); i++){
					if(readyList.get(i).getcycle() == edgeWeight){
						tempList.add(readyList.get(i));
						readyList.remove(i);
//						System.out.println("ready content  "+readyList.get(i));
//						System.out.println("templist content  "+tempList.get(i));
						
					}
				}
				//search temp list for lowest index
				for(int k=0; k < tempList.size(); k++){
					firedList.add(tempList.get(k));
					//System.out.println("temp list content "+tempList.get(k)+" and their index "+tempList.get(k).getindex());
				}
				
				
				//firedList.add(tempList.get(lowestIndex[1]));
				
				tempList.clear();
//				System.out.println("templist size after clear"+tempList.size());
				decrementEdgeWeight(parsedCode);
			}//end of find the highest weight node
			//decrementEdgeWeight(parsedCode);
			else{
				if(!scheduler.nop.isEmpty()){
					firedList.add(scheduler.nop.get(0));
					scheduler.nop.remove(0);
				}
				decrementEdgeWeight(parsedCode);
			}
		}//end of while
		writeToFile(firedList);
		printFiredlist(parsedCode);
	}
	public static void fifo(ArrayList<Instruction> parsedCode){
		while(parsedCode.size()!=firedList.size()){
			findLeafs(parsedCode);
			if(!readyList.isEmpty()){
				for(int i=0; i < readyList.size(); i++){
					firedList.add(readyList.get(i));
//					System.out.println("items added"+readyList.get(i));
				}
				readyList.clear();
				decrementEdgeWeight(parsedCode);
			}
			else{
				if(!scheduler.nop.isEmpty()){
					firedList.add(scheduler.nop.get(0));
					scheduler.nop.remove(0);
				}
				decrementEdgeWeight(parsedCode);
			}
		}//end of while
		writeToFile(firedList);
		printFiredlist(parsedCode);
	}
	/* Find leafs and add them to the ready list */
	public static void findLeafs(ArrayList<Instruction> parsedCode){
		//System.out.println("In findLeafs");
		Instruction currentLine;
		for(int i = 0; i < parsedCode.size(); i++){
			//System.out.println("parsedCode size"+parsedCode.size());
			currentLine = parsedCode.get(i);
			if(currentLine.sourceOfEdge.isEmpty() && !currentLine.getremoved()){//if leaf add to the ready list
				if(currentLine.getopCode().equals("nop")){
					scheduler.nop.add(currentLine);
				}
				//System.out.println("In if");
				else{
					readyList.add(currentLine);
				}
				//System.out.println("Current line: "+ currentLine.getlineOfCode()+" index: "+currentLine.getindex());
				currentLine.setremoved(true);
				//return true;
			}
		}
		//return false;
	}
	/*
	 Take nodes from ready list and add them to fired list and start counting the cycle of the node down
	 */
	public static void decrementEdgeWeight(ArrayList<Instruction> parsedCode){
//		if(!firedList.isEmpty()){
			for(int i = 0; i < firedList.size(); i++){
//				System.out.println("fired list size"+firedList.size());
				for(int l = 0; l < firedList.get(i).destinationOfEdge.size(); l++){
//					System.out.println("Edge size at node no:"+firedList.get(i)+" "+firedList.get(i).destinationOfEdge.size());
					firedList.get(i).destinationOfEdge.get(l).setweight(firedList.get(i).destinationOfEdge.get(l).getweight()-1);//decrement each edge weight
					if(firedList.get(i).destinationOfEdge.get(l).getweight() == 0){
						//System.out.println("firedList.get(i).sourceOfEdge.get(l) "+firedList.get(i).sourceOfEdge.get(l));
						int index = firedList.get(i).destinationOfEdge.get(l).getdestination();//destination index where source should be deleted
						int source = firedList.get(i).destinationOfEdge.get(l).getsource();
						//delete edge
						
						for(int m = 0; m < parsedCode.get(index-1).sourceOfEdge.size(); m++){//go through the sourceOf Edge list and find where the dest matches the source and delete
//							System.out.println("		m "+m);
//							System.out.println("                                          INDEX "+index);
//							System.out.println("                                          SOURCE "+source);
//							System.out.println("code at index "+parsedCode.get(index-1)+" "+(index-1));
//							System.out.println("parsedCode.get(index-1).sourceOfEdge.get(m) "+parsedCode.get(index-1).sourceOfEdge.get(m));
							
							if(parsedCode.get(index-1).sourceOfEdge.get(m) == source){
//								System.out.println("m in if "+m);
//								System.out.println("source to be removed "+parsedCode.remove(index-1).sourceOfEdge.get(m));
								parsedCode.get(index-1).sourceOfEdge.remove(Integer.valueOf(source));
							//Scheduler.graphPrint();	
								
							}
//							for(int n = 0; n < parsedCode.get(index-1).sourceOfEdge.size(); n++){
//								//System.out.print("after delete source is"+ parsedCode.get(index-1).sourceOfEdge.get(n));
//								System.out.println("after source delete "+ parsedCode.get(index-1).sourceOfEdge.get(n));
//							}
						}
					}	
				}
			}//End of for
//		}
		//Scheduler.graphPrint();	
	}
	
	public static void printFiredlist(ArrayList<Instruction> parsedCode){

//		System.out.println("Dependencies: ");
//		System.out.println("Input size: "+parsedCode.size());
//		System.out.println("Output size: "+firedList.size());

		for(int x=0; x < firedList.size(); x++){
		
//			System.out.println(x+1 +" "+ firedList.get(x).getlineOfCode()+"		original index "+firedList.get(x).getindex()+"	latency		"+
//			firedList.get(x).getlatency()+"	cycle	"+firedList.get(x).getcycle());
			System.out.println(firedList.get(x).getlineOfCode());

		}
		//System.out.println("latency: "+firedList.get(firedList.size()-1).getlatency());
		//System.out.println("\nedge weight: "+parsedCode.get(parsedCode.size()-1).getedgeWeight()+"\n");
	}
	public static boolean writeToFile(ArrayList<Instruction> firedList) {
		try {
			File o = new File("schedule.out");
			o.createNewFile();

			PrintWriter out = new PrintWriter(new FileWriter(o, false), true);
			for (int i = 0; i < firedList.size(); i++) {
				out.println(firedList.get(i));
			}
			out.close();
			return true;
		} catch (IOException e) {
			System.out.println("Error reading file in Control.output");
			return false;
		} catch (Exception e) {
			System.out.println("Exception in Control.output");
			return false;
		}
	}
}
