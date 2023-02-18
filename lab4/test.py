from typing import List


def find_pattern(corpus: str, query: str) -> List[int]:
    query_idx, corpus_idx = 1, 0
    res = []
    while corpus_idx != -1:
        if corpus.startswith(query, corpus_idx):
            res.append(query_idx)
        corpus_idx = corpus.find(' ', corpus_idx) + 1
        if corpus_idx == 0:
            break
        query_idx += 1

    return res


with open('corpus.txt', 'r', encoding='utf-8') as f:
    corpus = f.read()

corpus.replace('\n', ' ')

with open('query.txt', 'r', encoding='utf-8') as f:
    querys = f.read()

for query in querys.split('\n'):
    indices = find_pattern(corpus, query)
    if indices:
        print(*indices, sep=', ', end=' ')
    print('- ' + query)
