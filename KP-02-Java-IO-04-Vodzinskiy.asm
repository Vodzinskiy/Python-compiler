.386
.model flat, stdcall
option casemap:none

include \masm32\include\masm32rt.inc

.data
	i dword ?
	prime_number dword ?
	j dword ?
	result dword ?
	m dword ?
	n dword ?

.code
start:
	mov result, 0
	mov m, 50
	mov n, 100
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
	fn MessageBox, 0, str$(result),"КР-02-Java-IO-04-Vodzinskiy",MB_OK
	invoke ExitProcess, 0
end start
