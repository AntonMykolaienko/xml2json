[![Build Status](https://travis-ci.org/AntonMykolaienko/xml2json.svg?branch=master)](https://travis-ci.org/AntonMykolaienko/xml2json) [![Download Xml2Json Converter](https://img.shields.io/sourceforge/dm/xml2json-converter.svg)](https://sourceforge.net/projects/xml2json-converter/files/latest/download)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.fs:xml2json)](https://sonarcloud.io/dashboard?id=com.fs%3Axml2json)
[![Dependency Status](https://www.versioneye.com/user/projects/59eb28472de28c156fca6707/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/59eb28472de28c156fca6707)
[![](http://img.shields.io/gratipay/user/AntonMykolaienko.svg)](https://gratipay.com/~AntonMykolaienko/)
# xml2json
Simple UI and Command line tool for converting large XML-files to JSON or JSON to XML written on Java

### Startup options
Application have UI written on JavaFX. Starting from 1.2.0 version Xml2Json converter supports conversion via command line in batch. 
Tool supports following command line parameters:
- noGui - to indicate processing without GUI (Example: `--noGui`)
- sourceFolder - path to folder where source files placed (Example: `--sourceFolder=C:\Temp\Input`)
- destinationFolder - path to folder where converted files will be placed (Example: `--destinationFolder=C:\Temp\Output`)
- overwrite - to force overwrite converted files. Default: application will ask confirmation. (Example: `--overwrite`)

Examples:
1. Convert list of JSON-files:
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input \
	--destinationFolder=C:\Temp\Output --pattern=*.json
	```
2. Convert list of XML-files:
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input \
	--destinationFolder=C:\Temp\Output --pattern=*.xml
	```
3. Convert only one file :
	```
	java -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input \
	--destinationFolder=C:\Temp\Output --pattern=someFile.xml
	```
	
To start Tool with GUI just run following command: 
```
java -jar xml2json-1.2.0-all.jar
```

<br>Note: When you are starting tool in command line mode it will use all free memory which you have in your system, but if you need limit memory usage then you will need to add `-Xmx=512M` to your start command right after 'java':
```
java -Xmx=512M -jar xml2json-1.2.0-all.jar --noGui --sourceFolder=C:\Temp\Input \
--destinationFolder=C:\Temp\Output --pattern=someFile.xml
```


### Requirements
- Oracle JRE 1.8 update 40 or higher


### Releases
Date | Version | Description
-----|---------|------------
2017-11-12|1.2.0|Added ability to convert files by pattern in batch mode via command line
2017-06-06|1.1.0|Fix for huge memory consumption (XML to JSON)
2016-08-10|1.0.0|Initial release

### Donation
You can make donation via [PayPal](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=33R3LMBMX3R96)
