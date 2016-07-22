package app;

import java.util.ArrayList;

public class Instruction {	
	private String opCode;
	private String var1;
	private String var2;
	private String var3;
	String lineOfCode;
	private int cycle;
	private int latency = 0;
	private int index;
	private boolean removed=false;
	
	public ArrayList<Integer> edgeWeight = new ArrayList<Integer>(); 
	public ArrayList<Integer> sourceOfEdge = new ArrayList<Integer>();//edges leaving the node
	public ArrayList<Edge> destinationOfEdge = new ArrayList<Edge>();//edges pointing to the node
	
	public Instruction(String opCode){
		this.opCode = opCode;
		//lineOfCode = "";
	}
	public boolean getremoved(){
		return removed;
	}
	public int getindex(){
		return index;
	}
	public String getopCode(){
		return opCode;
	}
	public String getvar1(){
		return var1;
	}
	public String getvar2(){
		return var2;
	}
	public String getvar3(){
		return var3;
	}
	public String getlineOfCode(){
		return lineOfCode;
	}
	public int getcycle(){
		return cycle;
	}
	public int getlatency(){
		return latency;
	}
	public void setremoved(boolean remove){
		removed = remove;
	}
	public void setindex(int Index){
		index = Index;
	}
	public void setopCode(String opcode){
		opCode = opcode;
	}
	public void setvar1(String var){
		var1 = var;
	}
	public void setvar2(String var){
		var2 = var;
	}
	public void setvar3(String var){
		var3 = var;
	}
	public void setcycle(int cycle){
		this.cycle = cycle;
	}
	public void setlatency(int latency){
		this.latency = latency;
	}
	@Override
	public String toString(){
		return lineOfCode;
	}
}
