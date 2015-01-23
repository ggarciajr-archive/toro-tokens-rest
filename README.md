# toro-tokens-rest

FIXME

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Configuring

You can configure the path to the leveldb database by setting an
environment variable or by creating a profile.clj file under the project
directory.

For more information about [environ][2]

[2]: https://github.com/weavejester/environ

### Configuring by environment variable:

```
DATABASE_PATH=<path-where-the-levelDb-will-be-created>
```

### Configuring using the profile.clj file:

```
{:<profile> {:env {:database-url "path-to-the-levelDb-file"}}}
```

## Running

To start a web server for the application, run:

```
lein ring server
```

## License

Copyright © 2014 FIXME
