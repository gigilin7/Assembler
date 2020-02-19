import java.util.*;
import java.io.*;
public class Assembler {

/*提供PASS1與PASS2程式碼給大家參考，其餘省略*/
		//PASS 1-----------------
			int temp=0;
			String startADDR="0";
			int len=0;
			String loc_H="";
			for(int i=0;i<info.size();i++)
			{
				//算len-----------------
				if(info.get(i).getLabel().contains(".")||i==0)//i==0是"START"
				{
					len=0;
				}
				else if(info.get(i).getOpcode().contains("RESW"))
				{
					len=Integer.parseInt(info.get(i).getOperand())*3;
				}
				else if(info.get(i).getOpcode().contains("RESB"))
				{
					//用16進位表示
					//len=Integer.parseInt(Integer.toHexString(Integer.parseInt(info.get(i).getOperand())));
					len=Integer.parseInt(info.get(i).getOperand());
				}
				else if(info.get(i).getOpcode().contains("BYTE"))
				{
					if(info.get(i).getOperand().contains("X"))//如X'F1'
					{
						len=1;
					}
					else//如C'EOF'
					{
						len=3;
					}
				}
				else//如果operand是"WORD"或其他
				{
					len=3;
				}
				LEN.add(len);
				
							
				//算LOC-----------------
				
				if(i==0)//"START"
				{
					loc_H=info.get(i).getOperand();
					startADDR=info.get(i).getOperand();//起始位置
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
				else
				{
					//先10進位表示16進位的加法，再用16進位表示出來
					loc_H=Integer.toHexString(Integer.parseInt(startADDR,16)+temp).toUpperCase();
					temp+=len;
				}
				TEMP.add(temp);
				LOC.add(loc_H);
				
				//建symbol table-----------------
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
					if(Integer.toHexString(Integer.parseInt(info.get(i).getOperand())).length()==4)//如4096
					{
						object_code="00"+Integer.toHexString(Integer.parseInt(info.get(i).getOperand()));
					}
					else//如3
						object_code="00000"+Integer.toHexString(Integer.parseInt(info.get(i).getOperand()));
				}
				else if(info.get(i).getOpcode().equals("BYTE"))
				{
					if(info.get(i).getOperand().contains("C"))
					{   //如C'EOF'(object_code是E的hex ascii code加O的加F的)
						String s=info.get(i).getOperand().substring(2,info.get(i).getOperand().length()-1);
						object_code=(Integer.toHexString(s.charAt(0))+Integer.toHexString(s.charAt(1))+Integer.toHexString(s.charAt(2))).toUpperCase();
					}
					else//X'F1'或X'05'(object_code是F1或05)
					{
						object_code=info.get(i).getOperand().substring(2,info.get(i).getOperand().length()-1);
					}
				}
				else if(info.get(i).getOperand().contains(",X"))//如BUFFER,X
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
				else//其他
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
			
			
