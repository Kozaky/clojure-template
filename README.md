# Example

## Useful commands

```clj
> clj -M:server        		# starts local server
> clj -M:dev				# runs the REPL
> clj -X:test				# checks tests
> clj -M:fmt/check			# checks the format
> clj -M:fmt/fix  			# fixes the format
```

#
## Docker Image
A local docker image can be created running:

```clj
> docker build -t example/server:1.0.0 .
```

Once we have created the image, we can run it as follows:
```
> docker run --expose=3000 -p 3000:3000 -d example/server
```

#
## Database Migration

In order to run the Postgresql service, we can run a docker image:
```
> docker run -d \
	--name postgres \
	-e POSTGRES_PASSWORD=secret \
    -e POSTGRES_DB=mydb \
    -e POSTGRES_USER=postgres \
	-e PGDATA=/var/lib/postgresql/data/pgdata \
	-p 5432:5432 \
	postgres

docker run --name mydb -e POSTGRES_PASSWORD=secret -d -p 5432:5432 postgres
```

Once it is running, we can execute the migrations from the REPL:
```
user=> (require '[ragtime.repl :as repl])
nil
user=> (repl/migrate config)
Applying 001-foo
nil
user=> (repl/rollback config)
Rolling back 001-foo
nil
```