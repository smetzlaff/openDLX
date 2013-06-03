; ------------------------------------------------------------------
; Example program showing the use of data and text definitions.
;
; Requires enabled forwarding
; ------------------------------------------------------------------


          .data                 ; begin of data section
number1:  .space         4      ; reserve 4 bytes accessable via label number1
number2:  .space         4      ; 4 bytes for number2 (can be used as variable)
number3:  .word          0x456  ; similar to declaration with .space, the memory is initialised with a value
pointer:  .word          number3; pointer, points to address of number3

          .text                 ; begin of code section
          .global main          ; declares the main module and makes it visible
main:							; entry point of program
          lw       r1,number3   ; loads content of number3 to register R1
          nop                   ; nop, because of data dependency regarding R1
          sw       number2,r1   ; writes content of register R1 to number2
          lw       r2,pointer   ; loads the value of the pointer (addresse of number3) to register R2
          nop                   ; nop, because of data dependency regarding R2
          lw       r3,(r2)      ; loads the value of number3 to register R3

		  ; end of program
		  trap 0 
