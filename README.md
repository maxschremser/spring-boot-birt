# birt-integration
A simple BIRT Rendition Engine

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


