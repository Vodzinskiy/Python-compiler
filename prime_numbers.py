def sum_prime_numbers(m, n):
    result = 0
    for i in range(m, n + 1):
        prime_number = True
        for j in range(2, i):
            if i % j == 0:
                prime_number = False
        if prime_number:
            if i != 1:
                result += i
    return result
def main():
    return sum_prime_numbers(40, 100)
print(main())