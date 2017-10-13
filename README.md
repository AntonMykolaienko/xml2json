[![Build Status](https://travis-ci.org/AntonMykolaienko/xml2json.svg?branch=master)](https://travis-ci.org/AntonMykolaienko/xml2json) [![Download Xml2Json Converter](https://img.shields.io/sourceforge/dm/xml2json-converter.svg)](https://sourceforge.net/projects/xml2json-converter/files/latest/download)
# xml2json
Simple UI and Command line tool for converting large XML-files to JSON or JSON to XML written on Java

### Startup options
Application have UI written on JavaFX. Starting from 1.2.0 version Xml2Json converter supports conversion via command line in batch. 
Tool support following command line parameters:
- noGui - to indicate processing without GUI (Example: --noGui)
- sourceFolder - path to folder where source files placed (Example: --sourceFolder=C:\Temp\Input)
- destinationFolder - path to folder where converted files will be placed (Example: --destinationFolder=C:\Temp\Output)
- overwrite - to force overwrite converted files. Default: application will ask confirmation. (Example: --overwrite)
Examples:
1. Convert list of JSON-files:
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input --destinationFolder=C:\Temp\Output --pattern=*.json
	```
2. Convert list of XML-files:
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input --destinationFolder=C:\Temp\Output --pattern=*.xml
	```
3. Convert only one file :
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input --destinationFolder=C:\Temp\Output --pattern=someFile.xml
	```
	
To start Tool with GUI just runn following command: 
	```
	java -jar xml2json-1.2.0-all.jar
	```

### Releases
Date | Version | Description
-----|---------|------------
Not released yet|1.2.0|Added ability to convert files by pattern in batch mode via command line
2017-06-06|1.1.0|Fix for huge memory consumption (XML to JSON)
2016-08-10|1.0.0|Initial release
