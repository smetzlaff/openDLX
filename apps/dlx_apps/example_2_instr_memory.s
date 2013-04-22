; ------------------------------------------------------------------
; Example program with memory instructions.
;
; Requires enabled forwarding
; ------------------------------------------------------------------


.global main
main:

; Memory acesses
      ; direct addressing
          lw       r2,0x200    ; loads the value from memory address 0x200 into register R2
      ; indirect addressing
          addi     r1,r0,0x200 ; writes 0x200 into register R1
          lw       r2,(r1)     ; loads the value from memory address 0x200 (address obtained from register R1) into register R2
      ; indirect addressing with displacement
          lw       r3,0x10(r1) ; loads the value from memory address 0x210 (0x10 + content of register R1) into register R3

          sw       (r1),r2     ; writes the content of register R2 to memory address 0x200 (obtained from register R1)
               
		  ; end of program
		  trap 0
