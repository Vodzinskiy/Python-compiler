# функція пошуку суми всіх простих чисел на проміжку від m до n
def sum_prime_numbers(m, n):
    # сума простих чисел
    result = 0
    # цикл який проходить по всіх числах проміжку
    for i in range(m, n + 1):
        # змінна, яка показує чи є число простим
        # True якщо воно просте
        prime_number = True
        # цикл який проходить по всіх числах на проміжку
        # від 2 до числа, яке перевіряється чи є воно простим
        for j in range(2, i):
            # якщо число ділиться без остачі на інше число (окрім одиниці
            # і його самого) то воно не є простим
            if i % j == 0:
                # число не є простим
                prime_number = False
        # якщо це просте число і не є 1, тому що 1 не просте число,
        # це число додається до загальної суми
        if prime_number:
            if i != 1:
                result += i
    # повернення суми
    return result

# головна функція
def main():
    # виклик функції пошуку суми простих чисел для
    # проміжку від 2 до 20, та повернення цього значення
    return sum_prime_numbers(2, 20)
# друк значення, яке повертає головна функція



