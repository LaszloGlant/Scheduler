//Laszlo Glant 02/19/2016
package app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileParser {
		
	public static void fileParser(ArrayList<Instruction> parsedList) throws IOException{
		String inLine;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try{
			while ((inLine = br.readLine()) != null && inLine.length() != 0) {
				inLine = inLine.replaceAll(",", "");
				StringTokenizer tokenizer = new StringTokenizer(inLine);
				while(tokenizer.hasMoreTokens()){

					String token = tokenizer.nextToken();
					Instruction line=null;
					
					switch(token){
					//nop (1)
					case "nop":
						line = new Instruction(token);
						line.lineOfCode = token;
						line.setcycle(1);
						break;
					/*addI (1)	regNum, constant => regNum
					subI (1)	regNum, constant => regNum
					add (1)		regNum, regNum => regNum
					sub (1)		regNum, regNum => regNum*/
					case "addI": case "subI": case "add": case "sub":
						line = new Instruction(token);//opCode
						token = tokenizer.nextToken();//1st reg
						line.setvar1(token);
						token = tokenizer.nextToken();//2nd reg | constant
						line.setvar2(token);
						token = tokenizer.nextToken();//=>
						token = tokenizer.nextToken();//3rd reg
						line.setvar3(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+", "+line.getvar2()+" => "+line.getvar3();
						line.setcycle(1);

						break;
					/*mult (3)	regNum, regNum => regNum
					div (3)		regNum, regNum => regNum*/
					case "mult": case "div":
						line = new Instruction(token);//opCode
						token = tokenizer.nextToken();//1st reg
						line.setvar1(token);
						token = tokenizer.nextToken();//2nd reg
						line.setvar2(token);
						token = tokenizer.nextToken();//=>
						token = tokenizer.nextToken();//3rd reg
						line.setvar3(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+", "+line.getvar2()+" => "+line.getvar3();
						line.setcycle(3);

						break;

					/*load (5)	regNum => regNum
   					store (5)	regNum => regNum*/
					case "load": case "store":
						line = new Instruction(token);//opCode
						token = tokenizer.nextToken();//1st reg
						line.setvar1(token);
						token = tokenizer.nextToken();//=>
						token = tokenizer.nextToken();//2nd reg
						line.setvar2(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+" => "+line.getvar2();
						line.setcycle(5);

						break;

					/*loadI (1)	constant => regNum*/
					case "loadI":
						line = new Instruction(token);//opcode
						token = tokenizer.nextToken();
						line.setvar1(token); //constant
						token = tokenizer.nextToken(); //=>
						token = tokenizer.nextToken(); //regNum
						line.setvar2(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+" => "+line.getvar2();
						line.setcycle(1);

						break;

					/*loadAO (5)regNum, regNum => regNum
   					loadAI (5)	regNum, constant => regNum*/
					case "loadAO": case "loadAI":
						line = new Instruction(token);//opcode
						token = tokenizer.nextToken();//1st reg
						line.setvar1(token);
						token = tokenizer.nextToken();//2nd reg | constant
						line.setvar2(token);
						token = tokenizer.nextToken();//=>
						token = tokenizer.nextToken();//3rd reg
						line.setvar3(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+", "+line.getvar2()+" => "+line.getvar3();
						line.setcycle(5);

						break;

					/*storeAO (5)	regNum => regNum, regNum
   					storeAI (5)	regNum => regNum, constant*/	
					case "storeAO": case "storeAI":
						line = new Instruction(token);//opCode
						token = tokenizer.nextToken();//1st reg
						line.setvar1(token);
						token = tokenizer.nextToken();//=>
						token = tokenizer.nextToken();//2nd reg
						line.setvar2(token);
						token = tokenizer.nextToken();//3rd reg
						line.setvar3(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1()+" => "+line.getvar2()+", "+line.getvar3();
						line.setcycle(5);

						break;

						/*output (1)	constant*/
					case "output":
						line = new Instruction(token);//opCode
						token = tokenizer.nextToken();//constant
						line.setvar1(token);
						line.lineOfCode = line.getopCode()+" "+line.getvar1();
						line.setcycle(1);
						break;
					default:
						System.out.println("Illegal instruction found! Check your code!");
					}//end of switch
					parsedList.add(line);
				}//end of while
			}//end of outside while
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		Scheduler.graphPrint();
	}//end of fileParser
}
