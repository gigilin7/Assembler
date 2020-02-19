# Assembler實作介紹與過程
## assembler directives
**( 組譯程式指引命令，只在assembler接收指令，不會在機器執行 )**
+ `START`：指定程式名稱和起始位址
+ `END`：指示原始程式的結束處，並指定程式中第一個可執行的指令
+ `BYTE`：定義字元或十六進位的常數，並且指出其可佔用之位元組的數量
+ `WORD`：定義一個字組的整數常數
+ `RESB`：保留所示數量的位元組，供資料區使用
+ `RESW`：保留所示數量的字組，供資料區使用

## 組合語言轉換成object code
+ pass1：計算指令位址及定義標籤
+ pass2：組譯指令產生object code

## object program格式
+ `Header record`：
  - Col.1：H
  - Col.2－7：程式名稱
  - Col.8－13：(3bytes)object program的起始位址（16進位值）
  - Col.14－19：(3bytes)的程式的長度，以位元組為單位
+ `Text record`：
  - Col.1：T
  - Col.2－7：此記錄之object code的起始位址
  - Col.8－9：此記錄之object code的長度(位元組)
  - Col.10－69：object code，以16進位表示
+ `End record`：
  - Col.1：E
  - Col.2－7：object program中第一個可執行指令的位址

## 步驟
+ 將opcode轉換成對應的機器語言，例：STL轉換成14
+ 把operand轉換成對應的機器位址，例：RETADR轉換成1033
+ 建立SYMTAB存放label名稱與位址
+ 將原始程式內的常數資料，轉換成機器內部的表示方式，例：EOF轉換成454F46
+ 產生object code和object program

## 使用語言
+ Java

# pass1 & pass2 演算法
<img src="" width=200><img src="" width=200>

# 成果
<img src="" width=200><img src="" width=200>
