package app;

import java.util.ArrayList;

public class GraphBuilder {
	
	public static void findDependencies(ArrayList<Instruction> parsedCode){
		int i, j, currentConst, previousConst;
		Instruction currentLine, previousLine;
		Edge edge;
		for(i = parsedCode.size() - 1; i>=0 ;i--){//iterate through the array list holding the instructions
			currentLine = parsedCode.get(i);//get instruction at line i
			parsedCode.get(i).setindex(i+1);
			String opCode = currentLine.getopCode();//get the opcode
			switch(opCode){
			/*output (1)	constant*/
			case "output":
				currentConst = Integer.parseInt(currentLine.getvar1())-1024;
				for(j = i-1; j>=0 ;j--){//search for other output instructions
					previousLine = parsedCode.get(j);
					if(previousLine.getopCode().equals("output")){
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
					}
				}
				for(j = i-1; j>=0 ;j--){//let's find the incoming edge
					previousLine = parsedCode.get(j);
					previousConst = Integer.parseInt(previousLine.getvar3());
					if(previousConst == currentConst){
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));//.add(i+1);
						
						parsedCode.get(i).setlatency(currentLine.getcycle());//set latency for output
						if(parsedCode.get(j).getlatency() <= currentLine.getcycle()+previousLine.getcycle()){
							parsedCode.get(j).setlatency(currentLine.getcycle()+previousLine.getcycle());//set latency for output parent
						}
						break;
						
					}
				}
				break;
			/*storeAO (5)	regNum => regNum, regNum*/
	   		case "storeAO":
	   			break;
	   		/*storeAI (5)	regNum => regNum, constant*/
			case "storeAI":
				for(j = i; j >=0; j--){//find TRUE dependencies

					previousLine = parsedCode.get(j); 
					if(currentLine.getvar1().equals(previousLine.getvar2()) && 
						(previousLine.getopCode().equals("load") || previousLine.getopCode().equals("store") || previousLine.getopCode().equals("loadI"))){
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
						}
						break;
					}
					else if(currentLine.getvar1().equals(previousLine.getvar3()) && !previousLine.getopCode().equals("storeAO")){
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
						}
						break;
					}	
				}
				for(j = i; j >=0; j--){
					previousLine = parsedCode.get(j);
					if(currentLine.getvar2().equals(previousLine.getvar2()) && previousLine.getopCode().equals("loadI")){
						currentLine.sourceOfEdge.add(j+1);
						previousLine = parsedCode.get(0);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
						}
						break;
					}
				}
				break;
			/*loadAO (5)regNum, regNum => regNum*/
	   		case "loadAO":
	   			break;
	   		/*loadAI (5)	regNum, constant => regNum*/
			case "loadAI":
				currentConst = Integer.parseInt(currentLine.getvar2());
				for(j = i; j >=0; j--){//find TRUE dependence
					previousLine = parsedCode.get(j);
					if(previousLine.getopCode().equals("storeAI")){
						previousConst = Integer.parseInt(previousLine.getvar3());
						if(currentConst == previousConst){
							currentLine.sourceOfEdge.add(j+1);
							previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
							if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
								previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
							}
							break;
						}
					}
				}
				for(j = i; j >=0; j--){//find ANTI dependence
					previousLine = parsedCode.get(j);
				
					if(currentLine.getvar3().equals(previousLine.getvar1())){//find ANTI dependence
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(1,i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+1);
						}
						break;
					}
					if(currentLine.getvar3().equals(previousLine.getvar2())){
						if(previousLine.getopCode().equals("add") ||
						previousLine.getopCode().equals("sub") ||
						previousLine.getopCode().equals("mult") ||
						previousLine.getopCode().equals("div") ||
						previousLine.getopCode().equals("loadAO")){
							currentLine.sourceOfEdge.add(j+1);
							previousLine.destinationOfEdge.add(edge = new Edge(1,i+1, 0));
							if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
								previousLine.setlatency(currentLine.getlatency()+1);
							}
							break;
						}
					}
				}//end of for
				break;
			/*loadI (1)	constant => regNum*/
			case "loadI":
				//True dependence will be discovered from bottom up so we can check for ANTI dependence
				//see if var2 is used anywhere before. Most likely nowhere, since we allocate a new virtual reg for each loadI
				for(j = i; j >=0; j--){//find ANTI dependence
					previousLine = parsedCode.get(j);
				
					if(currentLine.getvar2().equals(previousLine.getvar1())){//find ANTI dependence
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(1,i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+1);
						}
						break;
					}
					if(currentLine.getvar2().equals(previousLine.getvar2())){
						if(previousLine.getopCode().equals("add") ||
						previousLine.getopCode().equals("sub") ||
						previousLine.getopCode().equals("mult") ||
						previousLine.getopCode().equals("div") ||
						previousLine.getopCode().equals("loadAO")){
							currentLine.sourceOfEdge.add(j+1);
							previousLine.destinationOfEdge.add(edge = new Edge(1,i+1, 0));
							if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
								previousLine.setlatency(currentLine.getlatency()+1);
							}
							break;
						}
					}
				}//end of for
				break;
			/*load (5)	regNum => regNum
	   		store (5)	regNum => regNum*/
			case "load": case "store":
				
				break;
			/*mult (3)	regNum, regNum => regNum
			div (3)		regNum, regNum => regNum
			add (1)	regNum, regNum => regNum
			sub (1)		regNum, regNum => regNum*/
			case "mult": case "div": case "add": case "sub":
				for(j = i; j >=0; j--){//find TRUE dependence check 1st reg
					previousLine = parsedCode.get(j);
					if(currentLine.getvar1().equals(previousLine.getvar3()) && currentLine.getindex()!=previousLine.getindex() || (currentLine.getvar1().equals(previousLine.getvar2()) && 
					(previousLine.getopCode().equals("loadI") ||
					previousLine.getopCode().equals("load") ||
					previousLine.getopCode().equals("store")))){
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
						}
						break;
					}
				}
				for(j = i; j >=0; j--){//check 2nd reg
					previousLine = parsedCode.get(j);	
					if(currentLine.getvar2().equals(previousLine.getvar3()) || (currentLine.getvar2().equals(previousLine.getvar2()) && 
						(previousLine.getopCode().equals("loadI") ||
						previousLine.getopCode().equals("load") ||
						previousLine.getopCode().equals("store")))){
							currentLine.sourceOfEdge.add(j+1);
							previousLine.destinationOfEdge.add(edge = new Edge(previousLine.getcycle(),i+1, 0));
							if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
								previousLine.setlatency(currentLine.getlatency()+previousLine.getcycle());
							}
							break;
					}
				}
				for(j = i; j >=0; j--){//find ANTI dependence
					previousLine = parsedCode.get(j);
				
					if(currentLine.getvar3().equals(previousLine.getvar1()) && currentLine.getindex()!=previousLine.getindex()){//find ANTI dependence
						currentLine.sourceOfEdge.add(j+1);
						previousLine.destinationOfEdge.add(edge = new Edge(1,i+1, 0));
						if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
							previousLine.setlatency(currentLine.getlatency()+1);
						}

					}
					if(currentLine.getvar3().equals(previousLine.getvar2()) && currentLine.getindex()!=previousLine.getindex()){
						if(previousLine.getopCode().equals("add") ||
						previousLine.getopCode().equals("sub") ||
						previousLine.getopCode().equals("mult") ||
						previousLine.getopCode().equals("div") ||
						previousLine.getopCode().equals("loadAO")){
							currentLine.sourceOfEdge.add(j+1);
							previousLine.destinationOfEdge.add(edge = new Edge(1,i+1,0));
							if(previousLine.getlatency() <= currentLine.getlatency()+previousLine.getcycle()){//check if the latency set already and not less than new
								previousLine.setlatency(currentLine.getlatency()+1);
							}
						}
					}
				}//end of for
				break;
			/*addI (1)	regNum, constant => regNum
			subI (1)	regNum, constant => regNum*/
			case "addI": case "subI": 
				break;
			//nop (1) Independent instruction
			case "nop":
				break;
			}//end of switch
		}//end of for
		setIndex(parsedCode);
	}//end of findDependencies
	public static void setIndex(ArrayList<Instruction> parsedCode){
		int x=0,y=0;
		for(x=0; x < parsedCode.size(); x++){
			for(y=0; y<parsedCode.get(x).destinationOfEdge.size(); y++){
				parsedCode.get(x).destinationOfEdge.get(y).setsource(x+1);
			}
		}
	}
}
