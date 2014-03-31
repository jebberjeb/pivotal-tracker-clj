# pivotal-tracker-clj

[Pivotal Tracker API](https://www.pivotaltracker.com/help/api/rest/v5) Clojure wrapper.

Also included:
* Command-line interface tool
* Vim integration (to the CLI tool)

## Command Line Interface

    --file - the story file
    --load-stories - loads stories into story file
    --save-story - save a story by id
    --query - query string to use when loading stories
    --token - pivotal tracker api token
    --project-id - id of project who's stories you want to query

**Updates story name, description.

## Vim Integration

### Installation

Using Pathogen, simply copy the vim-pivotal-tracker directory into ~/.vim/bundle.

Also, add your pivotal token and project id to the top of pivotal.vim:

    " Replace with your own values
    let s:token = "ec88145a606fa874895ed411cf"
    let s:project_id = "966911"

### Dependencies

A JVM is required. "java" binary must be on the $PATH.

### Usage

    <leader>pte - load stories into current buffer
    <leader>ptw - save the current story, who's id is under the cursor

## BUGS

## TODO

* even get rid of the commit logs w/ that stuff in it
* split vim stuf out into a separate project w/ readme that points to the jar's
  source (the other project) and explains the relationship, and how to build
  it
* notify vimscripts, and pivotal-tracker's dev site of the plugin, tweet it
* Investigate leaner Clojure runtime options?
* Wrap additional API services


Copyright © 2014 Jeb Beich

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
