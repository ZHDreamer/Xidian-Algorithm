import random
import sys

if len(sys.argv) != 2:
    print("usage: python gen_query.py {n}")
    sys.exit()

with open("res/alice29.txt", "r", encoding="utf-8") as f:
    corpus = f.read()

corpus = corpus.replace("\n", " ").split()

random.shuffle(corpus)
querys = random.sample(corpus, int(sys.argv[1]))

with open("res/alice_query.txt", "w", encoding="utf-8", newline="\n") as f:
    f.write("\n".join(querys))
