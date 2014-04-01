# pivotal-tracker-clj

[Pivotal Tracker API](https://www.pivotaltracker.com/help/api/rest/v5) Clojure wrapper.

## Features

* Search stories using search query
* Update story descriptions
* Check stories for modifications

## Usage

* Coming soon!

## Building

This project is build using [Leiningen](http://leiningen.org/). To build the
jar, Simply run:

    lein uberjar

## Command Line Interface

Additionally, a command line interface is included.

    --file - the story file
    --load-stories - loads stories into story file
    --save-story - save a story by id
    --query - query string to use when loading stories
    --token - pivotal tracker api token
    --project-id - id of project who's stories you want to query

## BUGS

## TODO

* Investigate leaner Java/Clojure runtime options
* Wrap additional API services

Copyright © 2014 Jeb Beich

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
