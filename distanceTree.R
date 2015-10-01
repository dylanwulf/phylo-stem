library(cluster)
source("/home/dylan/Downloads/ctc/R/hc2Newick.R")
mydata <- read.table("dissimilarityMatrix.txt")
names <- read.table("names.txt")
rownames(mydata) <- names$V1
myagnes <- agnes(mydata, diss=TRUE, method="complete")
myhclust <- as.hclust(myagnes)
write(hc2Newick(myhclust), file="distanceTreeOut.nwk")

