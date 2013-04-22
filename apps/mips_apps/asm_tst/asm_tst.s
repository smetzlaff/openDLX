.align	2
.globl	main
.set	nomips16
.ent	main
.type	main, @function
main:	
	li $4, -4000
	li $3, 2
	sll $4, $4, 19
	srl $5, $4, 2
	li $6, 32
	srl $6, $6, 2
	mthi $6
	mtlo $5
	j main
	addu $4, $5, $5
	ldc1 $3, 0
	syscall 555

.set	macro
.set	reorder
.end	main
