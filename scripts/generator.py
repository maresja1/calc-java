# This is just the very beginning of a script that can be used to process
# arithmetic expressions.  At the moment it just defines a few classes
# and prints a couple example expressions.

# Possible additions include methods to evaluate expressions and generate
# some random expressions.

from __future__ import division
from random import random, randint, uniform, choice, seed

class Expression:
    pass

class Number(Expression):
    def __init__(self, num):
        self.num = num

    def eval(self):
        return self.num

    def __str__(self):
        return str(self.num)

class BinaryExpression(Expression):
    def __init__(self, op, priority,parent):
        self.op = op
        self.priority = priority
        self.parent = parent
        self.left = None
        self.right = None

    def eval(self):
        if self.op == "+":
            return self.left.eval() + self.right.eval()
        elif self.op == "-":
            return self.left.eval() - self.right.eval()
        elif self.op == "/":
            return self.left.eval() / self.right.eval()
        elif self.op == "*":
            return self.left.eval() * self.right.eval()


    def __str__(self):
        if self.parent is None or self.parent.priority < self.priority:
            return  str(self.left) + " " + self.op + " "  + str(self.right)
        else:
            return  "(" + str(self.left) + " " + self.op + " "  + str(self.right) + ")"


def randomExpression(prob,parent):
    p = random()
    if p > prob:
        if randint(0, 1) == 2:
            return Number(round(uniform(-100, 100),5))
        else:
            return Number(randint(-100, 100))
    else:
        choice = randint(0,3)
        op = ["+", "-", "*", "/"][choice]
        priority = [0,0,1,1][choice]
        expression = BinaryExpression(op, priority, parent)
        expression.left = randomExpression(prob * 0.8, expression)
        expression.right = randomExpression(prob * 0.8, expression)
        return expression

fi = open('test/INPUT_GEN_1', 'w')
fo = open('test/OUTPUT_GEN_1', 'w')
for i in range(10000):
    expr = randomExpression(0.8,None)
    fi.write(str(expr) + "\n")
    try:
        fo.write("{0:.5f}".format(round(expr.eval(),5)) + "\n")
    except ZeroDivisionError:
        fo.write("ERROR\n")
