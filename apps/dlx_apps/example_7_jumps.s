; ------------------------------------------------------------------
; Example program with jumps.
;
; Requires enabled forwarding
; ------------------------------------------------------------------

.global main          
main:
          addi r1,r0,5      ; R1=5
start:                     ; start defines a jump target
          beqz r1,finished    ; branches if R1 equals zero
	  nop               ; required for branch delay slots
	  nop               ; required for branch delay slots
          subi r1,r1,1      ; R1--
          J start          ; jump to start
	  nop               ; required for branch delay slots
	  nop               ; required for branch delay slots
finished:                     ; end of loop

j2:       addi r2,r0,15
          addi r3,r0,10
		  ; calculates the maximum of R2 and R3 and writes it to R4
          SGT  R10,R2,R3    ; comparison R2>R3
                            ; GT = greater than, LT = lower than, LE = lower or equal, GE = greater or equal
                            ; EQ = equal, NE = not equal
                            ; the result is a boolean value, i.e. 1 as true, 0 as false
          BNEZ R10,sprung   ; jumps, if the condition is true, i.e. R2>R3
	  nop               ; required for branch delay slots
	  nop               ; required for branch delay slots
          add  r4,r0,r3
          j Ende
	  nop               ; required for branch delay slots
	  nop               ; required for branch delay slots
sprung:   add  r4,r0,r2          
Ende:
		  trap 0
