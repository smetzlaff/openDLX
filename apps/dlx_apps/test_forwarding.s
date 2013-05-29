

          .data              
Value:	  .space 4   
Text1:    .asciiz       "Test Trap" 
          .align        2       ; aligns following definitions or code to 4 bytes
TextAdr:  .word         Text1


.global main
main:

          ; data dependencies:
	  addi     r1,r0,0x42     
	  add     r2,r1,r0    
	  add     r3,r1,r0    	
	  add     r4,r1,r0    
	  ; with forwarding r2,r3,r4 contain 0x42
	  ; wihtout forwarding only r4 contain 0x42

	  sw      Value(r0), r1
	  nop
	  nop
	  nop

          ; data dependencies with load:
	  lw     r10, Value(r0)
          add    r11, r10, r0
          add    r12, r10, r0
          add    r13, r10, r0
	  ; with forwarding r12 and r13 contain 0x42
	  ; with forwarding and MIPS compatibility mode r11, r12, and r13 contain 0x42
	  ; without forwarding only r13 contain 0x42

	  nop
	  nop
          nop

	  ; unconditional branch
	  j target
          addi r15, r0, 1
          addi r15, r0, 2
          addi r15, r0, 3
          addi r15, r0, 4
          addi r15, r0, 5
	  ; with forwarding r15 contains 2 (2 branch delay slots)
	  ; with forwarding and MIPS compatibility mode r15 contains 1 (1 branch delay slots)
	  ; without forwarding r15 should contain 3 (3 branch delay slots)

target:
	  addi r20, r0, 1
	  nop
	  nop
	  nop
	  ; conditional branch
	  bnez r20, target2
          addi r16, r0, 1
          addi r16, r0, 2
          addi r16, r0, 3
          addi r16, r0, 4
          addi r16, r0, 5
	  ; with forwarding r15 contains 2 (2 branch delay slots)
	  ; with forwarding and MIPS compatibility mode r15 contains 1 (1 branch delay slots)
	  ; without forwarding r15 should contain 3 (3 branch delay slots)

target2:
	  addi r21, r0, 1
	  nop
	  nop
	  nop
	  ; traps
          addi  r14,r0,TextAdr  ; storing the address of the string into register R14
	  nop ; required if no forwarding used!
	  nop ; required if no forwarding used!
	  ; without forwarding 2 nops are needed between assignment of r14 and call of trap
	  ; with forwarding no nops are needed between assignment of r14 and call of trap
          trap  5    
          addi r25, r0, 1
          addi r26, r0, 1
          addi r27, r0, 1
          addi r28, r0, 1
          addi r29, r0, 1
	  ; in any case r25 - r29 has to contain 1



	; end of program
	trap 0
