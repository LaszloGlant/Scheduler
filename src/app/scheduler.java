package app;

import java.io.IOException;
import java.util.ArrayList;

public class scheduler {
	public static ArrayList<Instruction> parsedCode = new ArrayList<Instruction>();
	public static ArrayList<Instruction> reorderedCode = new ArrayList<Instruction>();
	public static ArrayList<Instruction> nop = new ArrayList<Instruction>();
	public static void main (String args[]) throws IOException{
		
		//invalid number of command line arguments
		if(args.length != 1){
			System.out.println("Usage: Scheduler <heuristics> < test.file");
			printUse();
			System.exit(1);
		}
//		String fileContent = getStringFromInputStream();	
		
		
		//FileParser.fileParser(args[1].replaceAll(",", ""), parsedCode);
		
		FileParser.fileParser(parsedCode);
		//arrayListPrinter(parsedCode);
		GraphBuilder.findDependencies(parsedCode);
		//graphPrint();
		if(args[0].equals("-a")){
			Heuristics.longestLatencyWeighted(parsedCode);
		}
		else if(args[0].equals("-b")){
			Heuristics.highestInstructionLatency(parsedCode);
		}
		else if(args[0].equals("-c")){
			Heuristics.fifo(parsedCode);
		}
		else{
			System.out.println("No such argument!");
			printUse();
		}
	}
	public static void printUse(){
		System.out.println("Enter -a for longest latency-weighted path to root scheduler!");
		System.out.println("Enter -b for highest latency single instruction scheduler!");
		System.out.println("Enter -c for first in first out scheduler!");
	}
	public static void arrayListPrinter(ArrayList<Instruction> inst){
		for(int i=0; i<inst.size(); i++){
			System.out.println(inst.get(i));
		}
	}
	public static void graphPrint(){//for testing only
		
		System.out.println("Dependencies: ");
		System.out.println("Array list size: "+parsedCode.size());
		
		for(int x=0; x < parsedCode.size(); x++){
			System.out.println("Line index "+parsedCode.get(x).getindex() +" "+ parsedCode.get(x).getlineOfCode()+"     "+parsedCode.get(x).getcycle());
			for(int y=0; y<parsedCode.get(x).sourceOfEdge.size(); y++){
				System.out.println("				source: "+ parsedCode.get(x).sourceOfEdge.get(y));
			}
			for(int y=0; y<parsedCode.get(x).destinationOfEdge.size(); y++){
				System.out.println("				Source: "+parsedCode.get(x).destinationOfEdge.get(y).getsource()+" destination: "+ parsedCode.get(x).destinationOfEdge.get(y).getdestination()+" and weight: "+
						parsedCode.get(x).destinationOfEdge.get(y).getweight());
				System.out.println("	latency: "+parsedCode.get(x).getlatency());
				//System.out.println("\nedge weight: "+parsedCode.get(x).getedgeWeight()+"\n");
			}
		}
		System.out.println("	latency: "+parsedCode.get(parsedCode.size()-1).getlatency());
		//System.out.println("\nedge weight: "+parsedCode.get(parsedCode.size()-1).getedgeWeight()+"\n");
	}
}