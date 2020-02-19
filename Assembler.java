import java.util.*;
import java.io.*;
public class Assembler {

public static class Info
{
	private String label;
	private String opcode;
	private String operand;
	
	public Info(String l,String oc,String or)
	{
		label=l;
		opcode=oc;
		operand=or;
	}
	public String getLabel()
	{
		return label;
	}
	public String getOpcode()
	{
		return opcode;
	}
	public String getOperand()
	{
		return operand;
	}
}

public static class SymbolTable
{
	private String symbol="";
	private String value="";
	
	public SymbolTable(String s,String v)
	{
		symbol=s;
		value=v;
	}
	public String getSymbol()
	{
		return symbol;
	}
	public String getValue()
	{
		return value;
	}
}

	public static void main(String[] args) {
		try
		{
			FileReader f=new FileReader("Figure2.1.txt");
			BufferedReader b=new BufferedReader(f);
			Scanner sc=new Scanner(b);
			String[] op={ "ADD", "ADDF", "ADDR", "AND", "CLEAR", "COMP", "COMPF", "COMPR", "DIV", "DIVF", "DIVR",
					"FIX", "FLOAT", "HIO", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS",
					"LDT", "LDX", "LPS", "MUL", "MULF", "MULR", "NORM", "OR", "RD", "RMO", "RSUB", "SHIFTL", "SHIFTR",
					"SIO", "SSK", "STA", "STB", "STCH", "STF", "STI", "STL", "STS", "STSW", "STT", "STX", "SUB", "SUBF",
					"SUBR", "SVC", "TD", "TIO", "TIX", "TIXR", "WD" };
			String[] code={ "18", "58", "90", "40", "B4", "28", "88", "A0", "24", "64", "9C", "C4", "C0", "F4", "3C",
					"30", "34", "38", "48", "00", "68", "50", "70", "08", "6C", "74", "04", "E0", "20", "60", "98", "C8",
					"44", "D8", "AC", "4C", "A4", "A8", "F0", "EC", "0C", "78", "54", "80", "D4", "14", "7C", "E8", "84",
					"10", "1C", "5C", "94", "B0", "E0", "F8", "2C", "B8", "DC" };
			String label=" ",opcode=" ",operand=" ";
			int line=0,flag=0;//line=0�O�@���٨S�����Aflag�O�P�_Ū�쪺�r��O���ӳ���
			ArrayList<Info> info=new ArrayList<>();
			ArrayList<SymbolTable> SymTab=new ArrayList<>();
			ArrayList<Integer> LEN=new ArrayList<>();
			ArrayList<String> LOC=new ArrayList<>();//��"."����m
			ArrayList<Integer> TEMP=new ArrayList<>();
			ArrayList<String> ObjectCode=new ArrayList<>();//��"."���ت��X
			ArrayList<String> LOC_2=new ArrayList<>();//�L"."����m
			ArrayList<String> ObjectCode_2=new ArrayList<>();//�L"."���ت��X
			
			//����-----------------
			while(sc.hasNext())
			{
				String s=sc.next();
				if(s.equals("."))
				{
					label=s;
					line=1;//����Ū���n�s�J���U�@��Ū
				}
				if(s.equals("BYTE")||s.equals("WORD")||s.equals("RESW")||s.equals("RESB")||s.equals("START")||s.equals("END"))
				{
					opcode=s;
					flag=1;
				}
				else 
				{
					for(int i=0;i<op.length;i++)
					{
						if(s.equals(op[i]))
						{
							opcode=s;
							flag=1;
							break;
						}
					}
				}
				if(flag==0)
				{
					label=s;
				}
				else if(!opcode.equals("RSUB"))
				{
					operand=sc.next();//�e�ⶵ��Ū��F�A�ĤT���s�U�@��Ū���r��
					line=1;
				}
				if(opcode.equals("RSUB"))
				{
					line=1;
				}
				if(line==1)//����Ū���n�s�Jinfo
				{
					info.add(new Info(label,opcode,operand));
					label=" ";
					opcode=" ";
					operand=" ";
					line=0;
					flag=0;
				}
			}

			//PASS 1-----------------
			int temp=0;
			String startADDR="0";
			int len=0;
			String loc_H="";
			for(int i=0;i<info.size();i++)
			{
				//��len-----------------
				if(info.get(i).getLabel().contains(".")||i==0)//i==0�O"START"
				{
					len=0;
				}
				else if(info.get(i).getOpcode().contains("RESW"))
				{
					len=Integer.parseInt(info.get(i).getOperand())*3;
				}
				else if(info.get(i).getOpcode().contains("RESB"))
				{
					//��16�i����
					//len=Integer.parseInt(Integer.toHexString(Integer.parseInt(info.get(i).getOperand())));
					len=Integer.parseInt(info.get(i).getOperand());
				}
				else if(info.get(i).getOpcode().contains("BYTE"))
				{
					if(info.get(i).getOperand().contains("X"))//�pX'F1'
					{
						len=1;
					}
					else//�pC'EOF'
					{
						len=3;
					}
				}
				else//�p�Goperand�O"WORD"�Ψ�L
				{
					len=3;
				}
				LEN.add(len);
				
							
				//��LOC-----------------
				
				if(i==0)//"START"
				{
					loc_H=info.get(i).getOperand();
					startADDR=info.get(i).getOperand();//�_�l��m
					temp=0;
				}
				else if(i==1)//"FIRST"
				{
					loc_H=startADDR;
					temp=len;
				}
				else if(info.get(i).getLabel().equals("."))
				{
					loc_H=" ";
				}
//				else if(i==info.size()-1)//"END"�S��LOC���٬O���s�ۥu�O�̫ᤣ�n�L
//				{
//					loc_H="";
//				}
				else
				{
					//��10�i����16�i�쪺�[�k�A�A��16�i���ܥX��
					loc_H=Integer.toHexString(Integer.parseInt(startADDR,16)+temp).toUpperCase();
					temp+=len;
				}
				TEMP.add(temp);
				LOC.add(loc_H);
				
				//��symbol table-----------------
				if(!info.get(i).getOpcode().equals("START")&&!info.get(i).getLabel().equals(" ")&&!info.get(i).getLabel().equals("."))
				{
					SymTab.add(new SymbolTable(info.get(i).getLabel(),loc_H));
				}
				
			}

			//PASS 2-----------------
			String object_code=" ";
			for(int i=0;i<info.size();i++)
			{
				if(i==0||info.get(i).getOpcode().equals("END")||info.get(i).getLabel().equals(".")||info.get(i).getOpcode().equals("RESW")||info.get(i).getOpcode().equals("RESB"))
				{
					object_code=" ";
				}
				else if(info.get(i).getOpcode().equals("WORD"))
				{
					if(Integer.toHexString(Integer.parseInt(info.get(i).getOperand())).length()==4)//�p4096
					{
						object_code="00"+Integer.toHexString(Integer.parseInt(info.get(i).getOperand()));
					}
					else//�p3
						object_code="00000"+Integer.toHexString(Integer.parseInt(info.get(i).getOperand()));
				}
				else if(info.get(i).getOpcode().equals("BYTE"))
				{
					if(info.get(i).getOperand().contains("C"))
					{   //�pC'EOF'(object_code�OE��hex ascii code�[O���[F��)
						String s=info.get(i).getOperand().substring(2,info.get(i).getOperand().length()-1);
						object_code=(Integer.toHexString(s.charAt(0))+Integer.toHexString(s.charAt(1))+Integer.toHexString(s.charAt(2))).toUpperCase();
					}
					else//X'F1'��X'05'(object_code�OF1��05)
					{
						object_code=info.get(i).getOperand().substring(2,info.get(i).getOperand().length()-1);
					}
				}
				else if(info.get(i).getOperand().contains(",X"))//�pBUFFER,X
				{
					for(int j=0;j<op.length;j++)
					{
						if(info.get(i).getOpcode().equals(op[j]))
						{
							for(int k=0;k<SymTab.size();k++)
							{
								if(info.get(i).getOperand().contains(SymTab.get(k).getSymbol()))
								{
									object_code=code[j]+(Integer.parseInt(SymTab.get(k).getValue())+8000);
									break;
								}
							}
							break;
						}
					}
				}
				else//��L
				{
					for(int j=0;j<op.length;j++)
					{
						if(info.get(i).getOpcode().equals(op[j]))
						{
							if(info.get(i).getOpcode().equals("RSUB"))
								object_code=code[j]+"0000";
							else
							{
								for(int k=0;k<SymTab.size();k++)
								{
									if(info.get(i).getOperand().equals(SymTab.get(k).getSymbol()))
									{
										object_code=code[j]+SymTab.get(k).getValue();
										break;
									}
								}
							}
							break;
						}
					}
				}
				ObjectCode.add(object_code);
			}
			
			//�L�X���G----------------------------------
			System.out.printf("%s\t%-3s%s\t%-7s%s\t\r\n", "Loc", " ", "Source statement", " ", "Object code");
			System.out.println("---------------------------------------------------");
			for(int i=0;i<info.size();i++)
			{
				if(i==info.size()-1)//END���ΦLLoc
					System.out.printf("%s\t%-6s\t%-6s\t%-10s\t%s\t\n"," ",info.get(i).getLabel(),info.get(i).getOpcode(),info.get(i).getOperand(),ObjectCode.get(i));
				else
					System.out.printf("%s\t%-6s\t%-6s\t%-10s\t%s\t\n",LOC.get(i),info.get(i).getLabel(),info.get(i).getOpcode(),info.get(i).getOperand(),ObjectCode.get(i));
			}
			
			//�L�Xobject program----------------------------------
			System.out.println();
			System.out.println("Object program");
			System.out.println("---------------------------------------------------------------------");
			
			//�LHeader record
			String s1=Integer.toHexString(Integer.parseInt(LOC.get(LOC.size()-1), 16)-Integer.parseInt(LOC.get(0), 16));
			System.out.printf("H%-6s00%s00%s\n",info.get(0).getLabel() ,LOC.get(0) ,s1.toUpperCase());
			
			//�A�s�@��LOC��ObjectCode��ArrayList��"."���n�s�J
			for(int i=0;i<LOC.size();i++)
			{
				if(!info.get(i).getLabel().equals("."))
				{
					LOC_2.add(LOC.get(i));
					ObjectCode_2.add(ObjectCode.get(i));
				}
			}
			LOC_2.add(LOC.get(LOC.size()-1));
			ObjectCode_2.add(ObjectCode.get(ObjectCode.size()-1));		
			
			//�LText record
			for(int i=0,j=1;i<=(ObjectCode_2.size()/10);i++)
			{
				String S1,S2;
				if(i==4)//�̫�@��T
				{
					S1=LOC_2.get(j+3).substring(2);
					S2=LOC_2.get(j).substring(2);
				}
				else
				{
					S1=LOC_2.get(j+10).substring(2);
					S2=LOC_2.get(j).substring(2);
				}
			
				if(i==1)//�ĤG��T(�]�����צn�������D�A���ӬO1B�A�����O15�A�ҥH�h���P�_)
					System.out.printf("T00%s%s",LOC_2.get(j),Integer.toString(Integer.parseInt(S1, 16)-Integer.parseInt(S2, 16)-12).toUpperCase() );
				else if(i==4)//�̫�@��T
					System.out.printf("T00%s0%s",LOC_2.get(j),Integer.toHexString(Integer.parseInt(S1, 16)-Integer.parseInt(S2, 16)).toUpperCase() );
				else
					System.out.printf("T00%s%s",LOC_2.get(j),Integer.toHexString(Integer.parseInt(S1, 16)-Integer.parseInt(S2, 16)).toUpperCase() );
				for(int k=0;k<10;k++,j++)//�L�XObjectCode(�q�`�O�@��10��)
				{
					if(j==LOC_2.size()-1)//�̫�@�椣��10��
						break;
					System.out.printf("%s",ObjectCode_2.get(j));
				}
				System.out.println();
			}
			
			//�LEnd record
			System.out.printf("E00%s",LOC.get(0));
			
			
			f.close();
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.print("file not found!");
			System.exit(0);
		}
		catch(IOException e)
		{
			System.out.print("IOException!");
			System.exit(0);
		}

	}

}
