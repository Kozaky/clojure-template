# reitit-ring, malli, swagger

## Local development

Execute the following command to run the server: 
```clj
> clj -M:server
```

If we need REPL, we can just execute:
```clj
> clj -M:dev
```

Check the tests running:
```clj
> clj -X:test
```
#
## Docker Image
A local docker image can be created using the jibbit plugin:

```clj
> clojure -Ttools install io.github.atomisthq/jibbit '{:git/tag "v0.1.14"}' :as jib
```

```clj
> clj -Tjib build
```

This will create a tar file that can be imported into Docker with:
```
> docker load < example/server   
```

Once we have imported the image, we can run the image as follows:
```
> docker run --expose=3000 -p 3000:3000 -d example/server
```

In order to run the mongo service, we can run the following command:
```
> docker-compose up -d
```

Please keep the files correctly formatted by running the following commands:
```
clj -M:fmt/check
clj -M:fmt/fix  
```