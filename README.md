# spring-boot-birt
A simple BIRT Rendition Engine

[![CircleCI](https://img.shields.io/circleci/project/github/RedSparr0w/node-csgo-parser.svg)]()
[![GitHub issues](https://img.shields.io/github/issues/maxschremser/spring-boot-birt.svg)](https://github.com/maxschremser/spring-boot-birt/issues)
[![GitHub license](https://img.shields.io/github/license/maxschremser/spring-boot-birt.svg)](https://github.com/maxschremser/spring-boot-birt/blob/master/LICENSE)

## Run the Application
To run the applicatin, use the gradle wrapper (gradlew), which will download a Gradle Runtime and start the **run** task.
If you have Gradle installed you can run the **run** task without the wrapper.
```gradle
gradlew run
```

Alternatively you can create an uber-jar using the task **installBootDist**.
```gradle
gradlew installBootDist
```
and run the report application using the *~/birt/build/install/birt-boot/bin/birt* script.

You can even execute the jar: ```java -jar birt-0.0.1.jar``` from either
*~/birt/build/install/birt/lib* or *~/birt/build/install/birt-boot/lib* directory.

Overwrite the bundled properties in one of the following ways as described in the 
Spring.io [documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html):

- Environment Variable
```gradle
BIRT_REPORT_PARAM_DATASET='{ "firstname": "Max", "lastname": "Schremser", "company": "Microsoft" }' java -jar birt/build/install/birt-boot/lib/birt-0.0.1.jar
```
- Java System Property
```gradle
java -Dbirt.report.param.dataSet='{ "firstname": "Max", "lastname": "Schremser", "company": "Amazon" }' -jar birt/build/install/birt-boot/lib/birt-0.0.1.jar
```

- Command line argument
```gradle
java -jar birt/build/install/birt-boot/lib/birt-0.0.1.jar --birt.report.param.dataSet='{ "firstname": "Max", "lastname": "Schremser", "company": "Google" }'
```

- External Properties file
```gradle
java -jar birt/build/install/birt-boot/lib/birt-0.0.1.jar --spring.config.name=ms
```

or any different environment.

Instead of setting the report parameter **dataset** in JSON format, the parameters for **firstname**, **lastname**
and **company** can be set to form the JSON object.
```gradle
gradlew -Dbirt.report.params.firstname=Max -Dbirt.report.params.lastname=Schremser -Dbirt.report.params.company=IBM run
```


## Birt Report (simple.rptdesign)
You can run the Report from within Birt Designer using the Default values. When running the spring boot application the
report parameters are taken from the **birt.properties** file.

### Data Source (dataSource)
The Data Source is the source for the data. It has two Script methods:
- open()
- close()

#### open()
Here we create a **dataSource** Object using the JSON syntax. The boolean **moreData** is used to exit the fetch() method.
If you do not return from the fetch() method, the report render task will not finish.

```javascript
dataSource = {"firstname": "Matt", "lastname": "Groening", "company": "Netflix"};
moreData = true
```

#### close()
The **close** method frees up the resources.

```javascript
dataSource = null
```

### Data Set (dataSet)
The Data Set (row) that holds the Column values. We implement the tree methods:
- open()
- close()
- fetch()

#### open()
```javascript
moreData = true
```

#### close()
```javascript
moreData = false
```

#### fetch()
Copies the values from the Data Source into the Data Set Row. Only 1 row. The values can also be calculated or fetched using
Java. For more information on using Java classes for Birt's Rhino Engine look here:
[github.com/kstojanovski/birt](https://github.com/kstojanovski/birt)

```javascript
if (!moreData)
  return false;

row["firstname"] = dataSource.firstname;
row["lastname"] = dataSource.lastname;
row["company"] = dataSource.company;

moreData = false;
return true;
```


