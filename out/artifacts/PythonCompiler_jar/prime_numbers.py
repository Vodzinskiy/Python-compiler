def prime_numbers(m, n):
    result = 0
    for i in range(m, n + 1):
        flag = False
        for j in range(2, i):
            if i % j == 0:
                flag = True
        if flag == False and i != 1:
            _result += i
    return result


def main():
    return prime_numbers(2, 100)
