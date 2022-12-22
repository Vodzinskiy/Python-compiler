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
sum_prime_numbers proc 
	local r: dword
	local i: dword
	mov r, 3
	mov i, 2
	loop1:
	mov ecx, r
	cmp i, ecx
	je loop1_end
	fn MessageBox, 0, str$(i),"КР-02-Java-IO-04-Vodzinskiy",MB_OK
	inc i
	jmp loop1
	loop1_end:
	ret
sum_prime_numbers endp

start:
	call sum_prime_numbers
end start
