# jolokia-extra - Addons for Jolokia

*jolokia-extra*'s purpose is to provide a potpourri of extensions to the [Jolokia JMX-HTTP Bridge](http://www.jolokia.org)
 which are either to special or to big to include in the regular distribution.  


## JSR-77 Simplifier

Initially provided as a [pull request](https://github.com/rhuss/jolokia/pull/50) to Jolokia by Marcin Płonka, a set 
of simplifier is available for better access to [JSR-77](https://jcp.org/aboutJava/communityprocess/mrel/jsr077/index.html) 
defined statistic values. Although JSR-77 has been abandoned for JEE 6 and later it is somewhat continues to live
in various JEE containers, especially Websphere continues to support the JSR-77 naming and statistics scheme.

Using the profile **jsr77** will build the agents with JSR-77 support. This means they simplifiers get included then 
into the JVM and WAR agent. For OSGi you can directly deploy the `jolokia-extra-addon-jsr77.jar` to the OSGi container. 

To build the agents call maven as usual:

    mvn -Pjsr77 clean install
     
The agents can be found below `agents/`

## Health Checks

Health checks can be used to provide internal, possibly complex checks on MBeans and return a consolidated view of the
results via extra MBeans registered.

To create the specific agent, use the profile `health`

    mvn -Phealth clean install

Agents can be found in `agents/`

Currently a Proof-of-Concept is available which registers an MBean `jolokia:type=plugin,name=health` with two operations:
`cpuLoadCheck` (which takes a CPU load threshhold) and `mbeanCountCheck` which simply checks whether there are any MBeans
registered. Refer to the source in `addon/health` for details.

You can expect a much more sophisticated version with a flexible configuration here soon.

## Health Check Scripts - Proof of Concept

**Health Check Scripts (HCS)** take a different angle onto Health Checks by supplying a scripted variant of Jolokia Core's internal JmxRequest model object abstractions.  These  allow for Bulk **Data Collection Scripts (DCS)** in addition to assertion executions promoting them to be actual **Health Check Scripts (HCS)**.

The idea is to make use of Jolokia's own higher level constructs, JEXL Expressions and JSON as a Serialization format to dynamically provide and execute HCS and DCS. For briefity we refer to HCS as a derivation of DCS.

The HCS Scripts are deployed independently to Jolokia and the surveilled Applications.

To build the HCS Proof of Concept with maven:

	cd jolokia-extra
	mvn clean install
	
To experiment with Health Check Scripts change into the `health-scripts-war` directory and start the embedded Jetty Server like such:

	cd addon/health-scripts-war
	mvn jetty:run
	
*Note* you may also deploy the built `jolokia-hc.war` to Tomcat or any other compliant Servlet 2.4 Engine (the later has not been tested yet).

Using the embedded Jetty you should get a similar message in your shell:

```
[INFO] jolokia-agent-with-hcs: ################ Jolokia HC ####################
[INFO] jolokia-agent-with-hcs: ##  Using local BackendManager. As useProxiedMode = false won't export the BackendManager as an MBean.
[INFO] jolokia-agent-with-hcs: ##  Sending a Notification to RegistrationListeners: [org.jolokia.extra.addon.scripting.http.ServletContextAwareJolokiaRegistrationHook@50a5314]
[INFO] jolokia-agent-with-hcs: ##  Successfully registered and called RegistrationHook. Now registering JolokiaScriptManager as an MBean using [StandardRegistrationStrategy] ...
[INFO] jolokia-agent-with-hcs: ##  Successfully registered ScriptManager MBean under: jolokia-extra:type=ScriptManager
[INFO] jolokia-agent-with-hcs: ################################################
[INFO] Started SelectChannelConnector@0.0.0.0:8888

```

Now the **ScriptManager** is accessible as an MBean under `jolokia-extra:type=ScriptManager` awaiting Script provisioning and execution through Jolokia as you may follow the tutorial in the next section.

### A Tutorial to dynamic Health Check Scripts

This tutorial provides you with the basic steps to take for handling and executing a Health Check Script. You'll need a texteditor, cURL and the started embedded Jetty (see above) as a prerequisite to follow along.

#### Step 1: Creation of a HCS

A HCS is described as a JSON Document which seems like a natural choice as Jolokia already makes heavy use of it.

Copy & Paste the following JSON constituting your first HCS script into your texteditor and save it as `hcs-jetty7-heapMemory.json`:


```json
{
  "scriptId": {
    "name": "org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
    "version": "1.0.0"
  },
  "meta": {
    "author": "<YourName>",
    "description": "check version call through BackendManager",
    "supportLink" : "http://mywiki.company.com/support/monitoring/healthchecks"
  },
  "vars": {"heapMemoryUsageThreshold": 0.8},
  "collectionItems": [
    {
      "mbean": "java.lang:type=Memory",
      "var": "heap",
      "attribute": "HeapMemoryUsage",
      "type": "read"
    }
  ],
  "assertions": [{
    "name": "heapMemoryUsageAssertion",
    "check": "new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold",
    "descriptionExpr": "scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author"
  }],
  "conditional": {
    "check": "version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'",
    "description": "Check that the Jetty Version is 7.",
    "collectionItems": [{
      "var": "version",
      "type": "version"
    }]
  }
}
```

As you can see by looking at the JSON, it is made up of various properties (containing JSON Objects/Arrays) that make up the structure of a Script. See the Reference section below for a detailed explanation.

The above script is only executed if its conditional matches. If so, it will collect the Items defined in the collectionItems Array before using that data for the actual Assertion execution.

So what the above script basically does is to check that at least 80% of the heap memory is free.

*Note:* You only have to go through Step 2 if you don't make use of the Jolokia Client Library. 
As this tutorial is to show some usage patterns with basic tool support, let's continue with step 2.

#### Step 2: Conversion into a Jolokia POST Request

As the HealthCheck ScriptManager is to be called through Jolokia Core as a regular MBean, we have to minimize your newly created script into a single line and escape all occurancies of `"` with `\"`.

**1) JSON Compaction:**

You may find the following online Tool useful for JSON compaction:

http://jsonformatter.curiousconcept.com/

(set the "JSON Template" Selection to "Compact" and press the "Process" Button to to get the minimized JSON of your newly created script)


Your resulting JSON now should look like:

```json
{"scriptId":{"name":"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory","version":"1.0.0"},"meta":{"author":"<YourName>","description":"check version call through BackendManager","supportLink":"http://mywiki.company.com/support/monitoring/healthchecks"},"vars":{"heapMemoryUsageThreshold":0.8},"collectionItems":[{"mbean":"java.lang:type=Memory","var":"heap","attribute":"HeapMemoryUsage","type":"read"}],"assertions":[{"name":"heapMemoryUsageAssertion","check":"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold","descriptionExpr":"scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author"}],"conditional":{"check":"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'","description":"Check that the Jetty Version is 7.","collectionItems":[{"var":"version","type":"version"}]}}
```

Save it as `hcs-jetty7-heapMemory-min.json`.

**2) Escape the Strings**

Now lets escape the Strings in your newly created `hcs-jetty7-heapMemory-min.json` using the editor's and search and replace function so to replace all occurances of `"` with `\"`:

```
{\"scriptId\":{\"name\":\"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory\",\"version\":\"1.0.0\"},\"meta\":{\"author\":\"<YourName>\",\"description\":\"check version call through BackendManager\",\"supportLink\":\"http://mywiki.company.com/support/monitoring/healthchecks\"},\"vars\":{\"heapMemoryUsageThreshold\":0.8},\"collectionItems\":[{\"mbean\":\"java.lang:type=Memory\",\"var\":\"heap\",\"attribute\":\"HeapMemoryUsage\",\"type\":\"read\"}],\"assertions\":[{\"name\":\"heapMemoryUsageAssertion\",\"check\":\"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold\",\"descriptionExpr\":\"scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author\"}],\"conditional\":{\"check\":\"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'\",\"description\":\"Check that the Jetty Version is 7.\",\"collectionItems\":[{\"var\":\"version\",\"type\":\"version\"}]}}

```

Save it as `hcs-jetty7-heapMemory-min-escaped.json`.

The result is a none valid json document, which is ok as it will be embedded as a String in a Jolokia POST request.

**3) Package the compacted and escaped HCS into a Jolokia POST request**

Create a new JSON in your editor with the following content and save it as `jolokia-post-hcs-jetty7-heapMemory.json`.

basically you copy & paste the content of your **escaped** `hcs-jetty7-heapMemory-min-escaped.json` into the `arguments` section **as a String**:

```json
{
    "type": "EXEC",
    "mbean": "jolokia-extra:type=ScriptManager",
    "operation": "registerScript",
    "arguments": ["{\"scriptId\":{\"name\":\"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory\",\"version\":\"1.0.0\"},\"meta\":{\"author\":\"<YourName>\",\"description\":\"check version call through BackendManager\",\"supportLink\":\"http://mywiki.company.com/support/monitoring/healthchecks\"},\"vars\":{\"heapMemoryUsageThreshold\":0.8},\"collectionItems\":[{\"mbean\":\"java.lang:type=Memory\",\"var\":\"heap\",\"attribute\":\"HeapMemoryUsage\",\"type\":\"read\"}],\"assertions\":[{\"name\":\"heapMemoryUsageAssertion\",\"check\":\"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold\",\"descriptionExpr\":\"scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author\"}],\"conditional\":{\"check\":\"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'\",\"description\":\"Check that the Jetty Version is 7.\",\"collectionItems\":[{\"var\":\"version\",\"type\":\"version\"}]}}"]
}

```

Typically you want to automate Step 2 entirely if you're considering not to use the Jolokia Client APIs. 

#### Step 3: Provision your first HCS

After you're done with Step 2, let's move on and provision the script via Jolokia's Agent by means of a POST of the newly created, compacted and escaped `jolokia-post-hcs-jetty7-heapMemory.json` 

let's go and issue the POST onto the embedded Jetty 7 Servlet Engine listening on Port 8888 using cURL:

```
curl -v -H "Content-type: application/json" -X POST -d @jolokia-post-hcs-jetty7-heapMemory.json http://localhost:8888/jolokia-hc/
```

which should give you a result similar to this:

```
{
  "timestamp":1426346103,
  "status":200,
  "request":{
    "operation":"registerScript",
    "mbean":"jolokia-extra:type=ScriptManager",
    "arguments":[
      "{\"scriptId\":{\"name\":\"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory\",\"version\":\"1.0.0\"},\"meta\":{\"author\":\"<YourName>\",\"description\":\"check version call through BackendManager\",\"supportLink\":\"http:\/\/mywiki.company.com\/support\/monitoring\/healthchecks\"},\"vars\":{\"heapMemoryUsageThreshold\":0.8},\"collectionItems\":[{\"mbean\":\"java.lang:type=Memory\",\"var\":\"heap\",\"attribute\":\"HeapMemoryUsage\",\"type\":\"read\"}],\"assertions\":[{\"name\":\"heapMemoryUsageAssertion\",\"check\":\"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold\",\"descriptionExpr\":\"scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author\"}],\"conditional\":{\"check\":\"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'\",\"description\":\"Check that the Jetty Version is 7.\",\"collectionItems\":[{\"var\":\"version\",\"type\":\"version\"}]}}"
    ],
    "type":"exec"
  },
  "value":{
    "name":"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
    "version":"1.0.0"
  }
}
```

Congratulation you registered your first script under the **scriptId** org.jolokia.extra.addon.scripting.healthcheck.HeapMemory/1.0.0.

In the next step we want to collect the fruits by actually calling our newly registered HCS script.

#### Step 4: Execute and test your HCS

The provisioned Script defines an Assertion we want to check if it is met. The Assertions are used for Checking conditions within the monitored server. So, with the knowledge of the ScriptManager's `ObjectName` and the actual `ScriptId`, let's issue an EXEC Request to Jolokia as such:

```
curl -X GET http://localhost:8888/jolokia-hc/exec/jolokia-extra:type=ScriptManager/executeScript/org.jolokia.extra.addon.scripting.healthcheck.HeapMemory/1.0.0

```

which should result in:

```json
{
  "timestamp":1426346434,
  "status":200,
  "request":{
    "operation":"executeScript",
    "mbean":"jolokia-extra:type=ScriptManager",
    "arguments":[
      "org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
      "1.0.0"
    ],
    "type":"exec"
  },
  "value":{
    "heapMemoryUsageAssertion":{
      "status":"OK"
    }
  }
}
```

As you can see, the `heapMemoryUsageAssertion` returned with `"status":"OK"` as the Jetty Server JVM has plenty of Heap Memory free.

**Further testing - provoke the Assertion to fail**

Next, lets provoke the Assertion to fail to see if our mighty script is working as expected.
You can achieve this by lowering the actual threshold defined in the vars-section of your script.

To lower the HeapMemory threshold `heapMemoryUsageThreshold` lets make use a variant of the `executeScript` method as part of the URL.

To do so set the `heapMemoryUsageThreshold` to 0.001, which means that the Assertion should fail if more than 0,1 % of Heap is used, so to make sure it actually fails.

Call the `executeScriptWithParams` operation with the new `heapMemoryUsageThreshold` onto your ScriptManager to achieve this (string escaping also applies here).

```
curl -X GET http://localhost:8888/jolokia-hc/exec/jolokia-extra:type=ScriptManager/executeScriptWithParams/org.jolokia.extra.addon.scripting.healthcheck.HeapMemory/1.0.0/"\{\"heapMemoryUsageThreshold\":0.001\}"
```

Now, you should see the Assertion failing with exta Information being evaluated as it fails:

```
{
  "timestamp":1426346906,
  "status":200,
  "request":{
    "operation":"executeScriptWithParams",
    "mbean":"jolokia-extra:type=ScriptManager",
    "arguments":[
      "org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
      "1.0.0",
      "{\"heapMemoryUsageThreshold\":0.001}"
    ],
    "type":"exec"
  },
  "value":{
    "heapMemoryUsageAssertion":{
      "check":"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold",
      "status":"FAILED",
      "description":"ScriptId{name='org.jolokia.extra.addon.scripting.healthcheck.HeapMemory', version='1.0.0'} assertion heapMemoryUsageAssertion failed as the check: JexlExpression{'new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold'}' resolved to false: 0.019674527954554235 < 0.0010 (meaning that more than 1.9674527954554235 % of the heap is used). See http:\/\/mywiki.company.com\/support\/monitoring\/healthchecks#heapMemoryUsage for further infos or contact: <YourName>"
    }
  }
}
```

Ok, you should now have a first impression of Jolokia Health Check Scripting ... :-) 

**Note** that by removing the Assertions from the Script you actually get a **Data Collection Script (DCS)**, which basically resembles a Jolokia Bulk request with some additional capabilities:

You get **conditional execution** and a **naming scheme** for your resolved metrics, as each Collection Item has a var-property assigned defining a name you may refer to while diggesting the Data Collection Results in your monitoring scripts or applications. 

#### Step 5: Collecting Basic Information about registered HCS

To conclude this tutorial let's see what further operations the ScriptManager offers for basic administration:

**The containsScript Operation**

Checking if a Script is registered by its ScriptId:

```
curl -X GET http://localhost:8888/jolokia-hc/exec/jolokia-extra:type=ScriptManager/containsScript/org.jolokia.extra.addon.scripting.healthcheck.HeapMemory/1.0.0
```

`ScriptId` consists of a canonical name and a version as you have seen.

**List all registered Scripts by their ScriptId**

```
curl -X GET http://localhost:8888/jolokia-hc/exec/jolokia-extra:type=ScriptManager/listScripts
```

**Show the Script Definition by its ScriptId**

```
curl -X GET http://localhost:8888/jolokia-hc/exec/jolokia-extra:type=ScriptManager/scriptDefinition/org.jolokia.extra.addon.scripting.healthcheck.HeapMemory/1.0.0

```

gives you the actual Script Definition "as originally provided to the ScriptManager"

```
{
  "timestamp":1425764087,
  "status":200,
  "request":{
    "operation":"scriptDefinition",
    "mbean":"jolokia-extra:type=ScriptManager",
    "arguments":[
      "org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
      "1.0.0"
    ],
    "type":"exec"
  },
  "value":{
    "vars":{
      "heapMemoryUsageThreshold":0.8
    },
    "assertions":[
      {
        "descriptionExpr":"scriptId.toString() + ' assertion ' + assertion.name + ' failed as the check: ' + assertion.check + ' resolved to false: ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max)) + ' < ' + heapMemoryUsageThreshold + ' (meaning that more than ' + (new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) * 100) +' % of the heap is used). See ' + meta.supportLink + '#heapMemoryUsage for further infos or contact: ' + meta.author",
        "check":"new('java.lang.Double', heap.used) div new('java.lang.Double', heap.max) < heapMemoryUsageThreshold",
        "name":"heapMemoryUsageAssertion"
      }
    ],
    "collectionItems":[
      {
        "var":"version",
        "type":"version"
      },
      {
        "mbean":"java.lang:type=Memory",
        "var":"heap",
        "attribute":"HeapMemoryUsage",
        "type":"read"
      }
    ],
    "conditional":{
      "check":"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == '7'",
      "description":"Check that the Jetty Version is 7.",
      "collectionItems":[
        {
          "var":"version",
          "type":"version"
        }
      ]
    },
    "scriptId":{
      "name":"org.jolokia.extra.addon.scripting.healthcheck.HeapMemory",
      "version":"1.0.0"
    },
    "meta":{
      "author":"<YourName>",
      "description":"check version call through BackendManager",
      "supportLink":"http://mywiki.company.com/support/monitoring/healthchecks"
    }
  }
}

```

*Note:* 
unfortunately neither the internal JSON Library nor Jolokia itself respect "special" Map implementations like `LinkedHashMap` that guarantees fixed order for its properties. That's the reason why your script nested inside the value-property looks a bit different in terms of property order from what you actually provisioned.


### HCS Reference

#### HCS Scripting API

The Health Check Script Support is made available as an MBean so that it can be called by the Jolokia Core. 

The **HCS ScriptManager** is registered as an MBean under the ObjectName **jolokia-extra:type=ScriptManager**
See the JavaDoc for futher explanations:

```java
/**
 * MBean Interface to expose the JolokiaScriptManager as a Standard MBean.
 */
public interface JolokiaScriptManagerMBean {
    /**
     * Registers the passed in DataCollectionScript serialized as a String.
     *
     * @param jolokiaCollectionScriptJsonAsString DataCollectionScript serialized as a String
     * @return ScriptId as a JSONObject
     */
    JSONObject registerScript(String jolokiaCollectionScriptJsonAsString);

    /**
     * Unregisters a Script by its ScriptId
     * @param canonicalName as part of the ID
     * @param version as part of the ID
     * @return true if a script with the supplied scriptId was found and removed. false otherwise
     */
    boolean unregisterScript(String canonicalName, String version);

    /**
     * Checks if a certain Script identified by its scriptId is present
     *
     * @param canonicalName
     * @param version
     * @return true if the Script with the supplied scriptId is registered.
     */
    boolean containsScript(String canonicalName, String version);

    /**
     * Gets a List of ScriptIds represented as JSONArray.
     *
     * @return the registered ScriptIds.
     */
    JSONArray listScripts();

    /**
     * Gets the actual JSON script definition of JolokiaScript defined by its scriptId (canonicalName plus version).
     * If there are is more than one version managed by this JolokiaScriptManager instance and the passed in version is null, then the latest Version will be returned.
     *
     * @param canonicalName
     * @param version
     *
     * @return the matching JolokiaScript Definition as a String or an empty result {} if no script with the supplied scriptId is registered.
     */
    JSONObject scriptDefinition(String canonicalName, String version);

    /**
     * Executes the HealthCheck (HCS) or DataCollection Script (DCS) that is identified by its scriptId ((canonicalName plus version).
     * If there are is more than one version managed by this JolokiaScriptManager instance and the passed in version is null, then the latest Version will be executed.
     * @param canonicalName
     * @param version
     * @return the actual Result of the Script execution, which depends on the type of Script (HCS or DCS)
     */
    JSONObject executeScript(String canonicalName, String version);

    /**
     * Executes the HealthCheck (HCS) or DataCollection Script (DCS) that is identified by its scriptId ((canonicalName plus version).
     * This method allows to pass in overrides as a stringified JSONObject for individual (default) parameters defined in the script's vars-section.
     * Note that properties that are not part of the vars section get stripped out for security reasons.
     *
     * @param canonicalName
     * @param version
     * @param parameterJSONAsString
     * @return the actual Result of the Script execution, which depends on the type of Script (HCS or DCS)
     */
    JSONObject executeScriptWithParams(String canonicalName, String version, String parameterJSONAsString);
}
```


*Note:*
as the HCS Implementation uses Jolokia's BackendManager as a Service Facade under the hood, all restrictions that are configured to Jolokia also apply for Collection Items to be executed.
So if you have configured certain restrictions, they get inherently applied for HCS Scrips as well.

#### HCS Script Syntax

A HCS Script is a valid JSON Document that is made up of a set of properties (themselves being JSON Objects).

let's have another example so that the following reference table is better comprehensible.

```json
{
  "scriptId":{
    "name":"org.jolokia.extra.addon.scripting.test.VersionTest",
    "version":"1.0.0"
  },
  "meta":{
    "description":"check version call through BackendManager",
    "author":"MM"
  },
  "vars":{
    "expectedVendorName": "clipse",
    "expectedVersion": "7"
  },
  "collectionItems":[
    {
      "var":"version",
      "type":"version"
    }
  ],
  "assertions":[
    {
      "name":"versionAssertion",
      "check":"version.info.vendor == expectedVendorName",
      "descriptionExpr":"scriptId.toString()  + ' assertion '  + assertion.name + ' failed. it checks that the actual vendor is Eclipse: '  + assertion.check"
    }
  ],
  "conditional":{
    "check":"version.info.product.toLowerCase() == 'jetty' && version.info.version.substring(0,1) == expectedVersion",
    "description":"Check that the Jetty Version is 7.",
    "collectionItems":[
      {
        "var":"version",
        "type":"version"
      }
    ]
  }
}
```

##### HCS Properties Overview Table


property  | mandatory  | description
-------- | ----------- | -----------
scriptId | yes | gives the Script an identity so that it can be stored by the HCS ScriptManager.
meta     | yes | allows to supply some overarching information about the author, description and so on.
vars     | no  | allows you to define some (default) variables
collectionItems | yes | the place where the beef goes into. Here you define the actual MBean queries and operations to be executed.
assertions | no | Assertions operate on the resolved Information assembled by the collection item executions. Note: if you don't have Assertions defined the resulting Script will actually be a Data Collection Script (DCS) instead of a HealthCheck Script, i.e. you get the raw MBean information from it just like ordinary Jolokia Bulk Requests, but without the need to POST the operations on each Data Collection execution.
conditional | no | a Condition judges if the script is to be executed at all. Here you would check that the script runs in its intended environment or let the script do additional checks in case of an exceptional state within the monitored JVM. 

##### HCS Structure Details 
	
Each top level property of the HCS Script is made up of certain allowed properties/structures, which are detailed in this section.

###### scriptId

The scriptId is defined by the following properties.
 
property  | mandatory  | description
-------- | ----------- | -----------
name | yes | Canonical Name of the Script. You might choose to use a package structure as encouraged for Java programs.
version | yes | Version for your Script as plain text. You should stick to semantic versioning to ease interpretation and sorting (solving the "latest script"-version problem). e.g. 1.0.0, 1.0.0-SNAPSHOT, 2.1.2.RC1, 2.1.2.RELEASE

Other properties within the scriptId are simply ignored.
The scriptId is accessible in JEXL Expressions via the `scriptId` property key.

###### meta

The meta Information is made up of the following properties. You may supply other meta properties as well (like "comment" etc.).
They will be available in JEXL Expressions under the `meta` property key (e.g. `meta.author`, `meta.comment`).
 
property | mandatory   | description
-------- | ----------- | -----------
author | yes | gives other people a clue from whom this script originated.
description | yes | should contain a short summary of what the Script does (not how).
&lt;any other&gt; | false | Any other property that represents meta information for your script (e.g. comment, supportLink ...)

###### vars

The vars information is made up of any variables you want to define as default values. 
Note that parameters supplied with the call to `executeScriptWithParams` are only able to actually override the properties defined in the vars-section.
Typically you use them for defining constants and thresholds.
To make your script maintainable you should make heavy use of vars in your JEXL Expressions.

*Note*: Currently the vars processing does not allow for recursive definitions, i.e. one variable cannot be derived from another, i.e. for example `"x":1, "y": "${x}+1"` **does not** work.  

property | mandatory   | description
-------- | ----------- | -----------
&lt;any property&gt; | no | allows to define variables and thresholds to refer to in JEXL expressions. 


###### collectionItems

The collectionItems property contains a JSON Array that consists of the actual Jolokia operations.
These operations are defined as JSON Objects to collect MBean Information, execute MBean Operations and the like.

*Note* as this is a Proof of Concept the HCS Scripting feature currently only supports the VERSION and READ Operations from Jolokia Core.

Common to ALL Collection Item is that they define a `var` property that is later directly referable in the JEXL expressions (i.e. within conditional.check, assertion.check and assertion.descriptionExpr Expressions) as well as a `type` property. The later defines the actual Jolokia Core request type.

*Version Collection Item*

The *Version Collection Item* represents Jolokia Core's JmxVersionRequest. It defines the following properties:

property | mandatory   | description
-------- | ----------- | -----------
var | yes | saves the result of the Jolokia Version Request (i.e. the `value` property of the JSON response) under the supplied variable name.
type | yes | the type of the Request represented by this Collection Item. Needs to be `version` for that the Collection Item is interpreted as an actual Version Request.

*Note* that any other properties, beside `var` are simply ignored if the type equals to `version`

*Read Collection Item*

The Read Collection Item is identified by its `type` property, which must be set to `read`. It is a Wrapper around Jolokia's JmxReadRequest.
The following properties are defined that besides `var` are reflected by Jolokia Core's Read Request.

property | mandatory   | description
-------- | ----------- | -----------
var | yes | saves the result of the Jolokia Version Request (i.e. the `value` property of the JSON response)  under the supplied variable name.
type | yes | the type of the Request represented by this Collection Item. Needs to be `read` for that the Collection Item is interpreted as a MBean Read Request.
mbean | yes | Name of the MBean. May contain a valid MBean Query Pattern if there are potentially more than one candidate to match.
attribute | yes | Single attribute to resolve. You may not use it in conjunction with `attributes`.
attributes | yes | Multiple attributes to resolve, supplied as a JSON Array of Strings. You may not use it in conjunction with `attribute`.
path | no | the path property to navigate within the result as supported by jolokia

###### assertions

The optional `assertions` property contains a JSON Array of Assertions to be executed as part of the script execution. They operate on the previously collected MBean information which is gathered by executing the Collection Items against the Jolokia Core.

Recall that the `var` value of each Collection Item gives that Item a variable name. 
As such the Collection Items serve as a variables during the Assertion execution.

Each Assertion is made up of a JEXL Expression assigned to the `check` property. 

The Assertion Expressions typically refer to the Collection Item variables in addition to the variables defined in the `vars` section of your script as local variables.
Typically you use the variables that belong to the `vars` section as defaults and thresholds to check against in the JEXL Expressions so that the actual assertions evaluate to a boolean value.

A result of `true` means that the assertion is met, `false` means otherwise.

The following table lists the properties you may define for individual Assertions.

property | mandatory   | description
-------- | ----------- | -----------
name | yes | name of the Assertion. Ideally the name should contain an *Assertion*, so to make it more explicit as such (e.g. heapMemoryUsage*Assertion*)
check | yes | the actual JEXL Expression to be executed. Make sure it evaluates to a Boolean result.
description | yes | use description if you want to have a fixed text if the Assertion fails. You may use only description or descriptionExpr, but not both.
descriptionExpr | yes | another JEXL Expression to dynamically construct the failed assertion description based on actual values. You may use only description or descriptionExpr, but not both.

*Note* that either `description` or `descriptionExpr` are only evaluated and returned if the Assertion actually fails, so to provide the caller with details about why the assertion failed.

*descriptionExpr*

as mentioned, if an assertion fails and a JEXL Expression is defined in `descriptionExpr` an `assertion` variable is added to be used during expression evaluation.

The `assertion` variable within your `descriptionExpr` expression defines the following properties that you may refer to like such: e.g. `assertion.name`

property | description
-------- | ------------
name     | name of the assertion that failed
check    | the JEXL Expression of the Assertion as a String 
status   | the Status String of the Assertion (being either OK, FAILED, EXCEPTION)
value    | the actual value of the Assertion evaluation (being either a boolean `false` or an Exception instance you may use to extract additional information)

###### conditional

The optional `conditional` property contains a JSON Object that allows to define a JEXL Expression assigned to the `check` property.

If `conditional` is present, it is evaluated first before the regular script actually proceeds.
It checks to see if the script is to be executed at all.
To be of any use it has to collect some basic information to reason on.

Therefore a `conditional` defines its own Collection Items array for collecting the necessary information to judge on.
This Collection Items array is just as the one for Data Collection but with the sole purpose to just get enough information so that the condition can be evaluated.

The following table lists the properties you may define for a Conditional.

property | mandatory   | description
-------- | ----------- | -----------
check | yes | the actual JEXL Expression to be executed as part of the condition evaluation. Make sure it evaluates to a boolean result.
description | yes | plain text to describe what is to be checked here (currently used only to enhance human readability)
collectionItems | yes | Array of individual Collection Items executed for the sole purpose to get information to reason on in `check` expression.

### POC Limitations

**Limitations of the Proof of Concept (POC)**

* not all Jolokia Operations are currently supported

* global ScriptConfiguration Support not actually implemented

* the JEXL Evaluator should be a Sandbox security wise (making explicitly use of a JSE SecurtiyManager)

	*see:*
	http://stackoverflow.com/questions/6551073/how-do-you-create-a-secure-jexl-scripting-sandbox

	or 
	
	http://commons.apache.org/proper/commons-jexl/xref-test/org/apache/commons/jexl2/SandboxTest.html
	

* validation support should provide better feedback messages that give users detailed information about the hierarchy of the model object that failed validation (and potentially collect all validation failures together before reporting them)

* no global configuration support for conditionally logging the Assertion failures to the process logfile

* no support for a logger to refer to within JEXL scripts

* no support for other output formats (XML, CSV etc.) 

* An HTML form which allows a script to be provided and executed in one go for instant feedback should be provided (to remove the tedious compaction, escaping and jolokia post preparation steps). It should print out the jolokia post as well so that the user could copy & paste them for shell scripting. Basic Editing would be a plus (decomposition of the script structure so to provide wizard like functionality for the various sections while prototyping the script).

* currently only supports the Jolokia Web Agent runtime.

* No perstistence support for provisioned scripts, so that they are automatically loaded and available when the JVM is rebooted.

* The POC is based on an inofficial API (as BackendManager and JmxRequest was not intended for direct use by Extensions), this should be changed as soon as Jolokia 2 defines a stable API to code against.