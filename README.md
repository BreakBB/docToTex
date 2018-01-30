# docToTeX
A compiler project to create a TeX document out of Javadoc.

## Getting started

- Make sure to install ANTLR and set your CLASSPATH-variable correctly
- Generate the Java classes for all .g4 files to a folder of your choice (default is "gen")
- Compile those generated classes

### Show parsetree
- Use "grun Javadoc documentation" inside the folder of the generated classes to run the testrig. Add an "-gui" to see the parsetree.
- Enter some Javadoc code you want to test

### Compile to Tex
- Install the ANTLR plugin for IntelliJ
- Make sure ANTLR is included as library in the module settings of the project
- Run JavadocListener and make sure "src" and "gen" are marked as "Sources Root" in IntelliJ

