.386
.model flat, stdcall
option casemap:none

include     C:\\masm32\include\windows.inc
include     C:\\masm32\include\kernel32.inc
include     C:\\masm32\include\user32.inc
include     C:\\masm32\include\masm32rt.inc
includelib  C:\\masm32\lib\user32.lib
includelib  C:\\masm32\lib\kernel32.lib


.code
sum_prime_numbers proc m:dword, n:dword
	local result: dword
	local i: dword
	local prime_number: dword
	local j: dword
	mov result, 0
	mov eax, n 
	add eax, 1 
	mov n, eax 
	mov eax, m
	mov i, eax
	loop1:
	mov ecx, n
	cmp i, ecx
	je loop1_end
	mov prime_number, 1
	mov j, 2
	loop2:
	mov ecx, i
	cmp j, ecx
	je loop2_end
	mov eax, i 
	mov ebx, j 
	xor edx, edx
	div ebx
	mov ecx, edx 
	.if ecx == 0
	mov prime_number, 0
	.endif
	inc j
	jmp loop2
	loop2_end:
	.if prime_number == 1
	.if i != 1
	mov eax, result 
	add eax, i 
	mov result, eax 
	.endif
	.endif
	inc i
	jmp loop1
	loop1_end:
	mov eax, result
	ret
sum_prime_numbers endp

main proc 
	push 20
	push 2
	call sum_prime_numbers
	ret
main endp

start:
	call main
	fn MessageBox, 0, str$(eax),"КР-02-Java-IO-04-Vodzinskiy",MB_OK
end start
