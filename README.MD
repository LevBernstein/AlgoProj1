# Basic Algorithms Programming Assignment 1
## Lev Bernstein


Given two planet names return the list of planet names and entrance fees of all planets between the two queried names, in lexicographic order.

Planets and their entrance fees are stored in a 2-3 tree.

Input will be formatted thusly:

* The first line will be n, the size of the database.

* The next n lines are of the form "a k", where a is the planet name and k is the entrance fee of the planet.

* The next line contains a number m, the number of queries to the databse.

* The next m lines are of the form "a b", where a,b are the names of the planets to be queried.

Constraints on input:

* n, m <= 1000

* |a|, |b| <= 10

* 0 <= k <= 10^9

Output will be formatted thusly:

For each query, write down the names of the planets and the entrance fees, separated by a single space, in between (inclusive) a, b, in separate lines.