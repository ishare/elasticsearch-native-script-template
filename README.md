# elasticsearch-native-script-template
ElasticSearch NativeScript Template build on Maven, support the newly version ElasticSearch `5.0.0`

inspired by imotov/elasticsearch-native-script-example

# structure
```
.
├── pom.xml *
├── src
│   ├── main
│   │   ├── assemblies
│   │   │   └── plugin.xml *
│   │   ├── java
│   │   │   └── org
│   │   │       └── elasticsearch
│   │   │           └── custom
│   │   │               └── nativescript
│   │   │                   ├── plugin
│   │   │                   │   └── NativeScriptPlugin.java *
│   │   │                   └── scripts
│   │   │                       └── ScoreScript.java *
│   │   └── resources
│   │       └── plugin-descriptor.properties *
│   └── test
│       └── java
└── target
    ├── elasticsearch-native-script-template-1.0-SNAPSHOT.jar
    └── releases
        └── elasticsearch-native-script-template-1.0-SNAPSHOT.zip *
```

## write your own native script, you just need to edit this two class files:
org.elasticsearch.custom.nativescript.scripts.ScoreScript
org.elasticsearch.custom.nativescript.plugin.NativeScriptPlugin#getNativeScripts()

To write `AnalysisPlugin`, `SearchPlugin`, etc, refer org.elasticsearch.plugins.Plugin for detail.

# install plugin
```
$ mvn clean package
$ bin/elasticsearch-plugin install file:///PATH_TO_DIR/elasticsearch-native-script-template/target/releases/elasticsearch-native-script-template-1.0-SNAPSHOT.zip
```

# test case
## create doc
```
curl -XPOST 'localhost:9200/testidx/user' -d '{
  "user_id": 1,
  "name": "navins",
  "company": "Chitu team",
  "title": "Software Developer",
  "text": "Engineer interested in Machine Learning, Recommendation, Information Retrieval, etc."
}'
```

## excute query using native script
```
curl -XGET 'localhost:9200/testidx/_search' -d '{
  "query": {
    "function_score": {
      "boost_mode": "replace",
      "query": {
        "bool": {
          "minimum_should_match": "1",
          "should": {
            "multi_match": {
              "fields": [
                "text",
                "company",
                "title",
                "text"
              ],
              "query": "engineer machine learning",
              "operator": "and",
              "type": "best_fields"
            }
          }
        }
      },
      "script_score": {
      	"script": {
          "inline": "score_script",
          "lang" : "native",
          "params": {
            "field": "text",
            "terms": [
              "engineer",
              "machine",
              "learning"
            ],
            "weights": [
              2.0,
              1.0,
              1.0
            ]
          }
        }
      }
    }
  }
}'
```