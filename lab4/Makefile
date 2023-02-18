CLASSPATH := bin
SOURCEPATH := src
RESOURCE := res

n := 1000

all: build

build:
	javac -d $(CLASSPATH) $(SOURCEPATH)/*.java

clean:
	rm $(CLASSPATH)/*.class

test: build
	python gen_query.py $(n)
	hyperfine --warmup 3\
		'java -cp $(CLASSPATH) TextIndex $(RESOURCE)/alice29.txt $(RESOURCE)/alice_query.txt suffix'\
		'java -cp $(CLASSPATH) TextIndex $(RESOURCE)/alice29.txt $(RESOURCE)/alice_query.txt index'\
		'java -cp $(CLASSPATH) TextIndex $(RESOURCE)/alice29.txt $(RESOURCE)/alice_query.txt brute'
