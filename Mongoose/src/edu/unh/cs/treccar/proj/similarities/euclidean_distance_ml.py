#Euclidean distance calculation using feature vector representation of paragraphs
#and determine similarity scores
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import euclidean_distances
from cbor import *

#with open('train.test200.cbor.paragraphs', 'rb') as fp:

file1 = open("deserializedParagraphs.txt", "r")
paragraphs = file1.read().split('\n')

#author  : ajesh
#version : 1.3

vectors = CountVectorizer()
feature = vectors.fit_transform(paragraphs).todense()

print(vectors.vocabulary_)

#pairwise arrays < =2
# for j in feature:
    # for i in feature:
        # print(euclidean_distances(feature[j],i))

j = 0;

#file2 = open('testfile.txt','w') 
while j < len(paragraphs):

    for i in feature:
        print(euclidean_distances(feature[j],i))
		#file2.write(euclidean_distances(feature[j],i))
		

    j = j+1;
	
#file2.close()