.align 2
.globl main
.set nomips16
.set noat
.ent main
.type main, @function
main:
prolog:
	 ADDI    $4, $0, 5  # $4 = n = 5
	 ADDI    $1, $0, 1  # $1 = 1
	 ADD	 $25, $0, $0 # $25 = 0
	 ADDI	 $24, $0, 1 # $25 = 1
begin:
Outer:  SLE  $10, $4 , $0 #  $4 <= $0 ? Status in $10
        BNEZ $10, EndO    #  wenn ja, dann gehe zu EndO
        ADD  $3 , $4 , $0 #	 Setzen von $4 in $3
Inner:  SLE  $11, $3 , $0 #  $3 <= $0 ? Status in $11
        BNEZ $11, EndI    #  wenn ja, dann gehe zu EndI
       	              #  Schleifeninhalt
		ADD  $25, $25, $1 #
		ADD  $24, $24, $24 #

       	              #  Schleifeninhalt
        SUB  $3 , $3 , $1 #  $3 = $3 - $1
        J    Inner        #  Goto Inner
EndI:   SUB  $4 , $4 , $1 #  $4 = $4 - $1
        J    Outer        #  Goto Outer
EndO:                     #  Ende der Schleife

     break
.set	macro
.set	reorder
.end	main
