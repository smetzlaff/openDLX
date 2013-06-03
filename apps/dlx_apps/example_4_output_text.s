; ------------------------------------------------------------------
; Example program showing the use trap 5 for text output.
;
; Requires enabled forwarding
; ------------------------------------------------------------------

          .data                 
Text1:    .asciiz       "Hello World!!" ; zero terminated string, of length 14.
          .align        2       ; aligns following definitions or code to 4 bytes
TextAdr:  .word         Text1

          .text                 
          .global main          
main:
          addi  r14,r0,TextAdr  ; storing the address of the string into register R14
          trap  5               ; calling trap for text output
                                ; the trap prints a zero terminated string starting at the address provided in register R14 
          trap  0               ; trap for halting processor and exiting of simulation
