XLIFF_PATH=lib/file2xliff4j-20070711.jar:.:lib/commons-io-1.3.1.jar:lib/jodconverter-2.2.0.jar:lib/jpedalSTD.jar:lib/juh-2.1.0.jar:lib/jurt-2.1.0.jar:lib/nekohtml-0.9.1.jar:lib/ridl-2.1.0.jar:lib/unoil-2.1.0.jar:lib/xercesImpl-X.jar:lib/xpp3-1.1.3_8.jar:lib/xstream-1.2.2.jar:lib/slf4j-api-1.4.0.jar:lib/slf4j-jdk14-1.4.0.jar:lib/commons-logging-1.1.1/commons-logging-1.1.1.jar:lib/commons-logging-1.1.1/commons-logging-1.1.1-javadoc.jar:lib/commons-logging-1.1.1/commons-logging-1.1.1-sources.jar:lib/commons-logging-1.1.1/commons-logging-adapters-1.1.1.jar:lib/commons-logging-1.1.1/commons-logging-api-1.1.1.jar:lib/commons-logging-1.1.1/commons-logging-tests.jar
export XLIFF_PATH

rm XliffConverter/*.class
javac -cp lib/file2xliff4j-20070711.jar XliffConverter/*.java 

#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.html 1 en
java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.pdf 1 de
#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.txt 1 es
#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.xml 1 ch
#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.properties 1 ch
#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.doc 1 en
#java -cp $XLIFF_PATH XliffConverter/XliffConvert mary.odt 1 en


