package app;

public class Edge {
	int weight;
	int destination;
	int source;
	public Edge(int w, int dest, int s){
		weight = w;
		destination = dest;
		source = s;
	}
	public Edge(){
		weight = 0;
		destination = 0;
		source = 0;
	}
	public void setsource(int s){
		source = s;
	}
	public void setweight(int w){
		weight = w;
	}
	public void setdestination(int dest){
		destination = dest;
	}
	public int getsource(){
		return source;
	}
	public int getweight(){
		return weight;
	}
	public int getdestination(){
		return destination;
	}
}
