; ------------------------------------------------------------------
; Example program showing the use trap 5 for integer output.
;
; Requires enabled forwarding
; ------------------------------------------------------------------

             .data                 
Text1:       .asciiz       "Number1 is %d, Number2 is %d." ; the format string is similar to printf in C
             .align        2
TextAdr:     .word         Text1
OutNumber1: .space        4        ; reserving space for output of 1st number
OutNumber2: .space        4        ; reserving space for output of 2nd number
             .text                 
             .global main          
main:
          addi  r1,r0,15        
          sw    OutNumber1,r1 
          addi  r1,r0,32        
          sw    OutNumber2,r1 
          addi  r14,r0,TextAdr 
          trap  5					; the trap requires that the parameters are directly following the format string
          trap  0
